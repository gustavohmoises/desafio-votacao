package br.tec.db.votacao.service;

import br.tec.db.votacao.cache.PautaCache;
import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoInvalidoDTO;
import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.entity.VotoInvalido;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.exception.NotFoundException;
import br.tec.db.votacao.mapper.VotoMapper;
import br.tec.db.votacao.producer.VotoProducer;
import br.tec.db.votacao.repository.AssociadoRepository;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoInvalidoRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VotoService {
    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final AssociadoRepository associadoRepository;
    private final PautaCacheService pautaCacheService;
    private final VotoProducer votoProducer;
    private final VotoInvalidoRepository votoInvalidoRepository;

    public VotoService(
            VotoRepository votoRepository,
            PautaRepository pautaRepository,
            AssociadoRepository associadoRepository,
            PautaCacheService pautaCacheService,
            VotoProducer votoProducer,
            VotoInvalidoRepository votoInvalidoRepository
    ) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.associadoRepository = associadoRepository;
        this.pautaCacheService = pautaCacheService;
        this.votoProducer = votoProducer;
        this.votoInvalidoRepository = votoInvalidoRepository;
    }

    @Transactional(readOnly = true)
    public List<ListarVotoDTO> getAll() {
        return VotoMapper.toDtoList(votoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ListarVotoInvalidoDTO> getAllInvalidos() {
        return VotoMapper.toDtoInvalidoList(votoInvalidoRepository.findAll());
    }

    public VotoEventoDTO votar(CriarVotoDTO dto) {
        VotoEventoDTO votoEventoDTO = new VotoEventoDTO(
                dto.pautaId(),
                dto.associadoId(),
                dto.voto(),
                LocalDateTime.now(),
                "Voto em processamento."
        );

        votoProducer.publicar(votoEventoDTO);

        return votoEventoDTO;
    }

    public void processarVoto(VotoEventoDTO dto) {
        try {
            PautaCache pautaCache = pautaCacheService.buscar(dto.pautaId())
                    .orElseGet(() -> {
                        Pauta pauta = pautaRepository.findById(dto.pautaId())
                                .orElseThrow(() -> new NotFoundException("Pauta não encontrada."));

                        return new PautaCache(
                                pauta.getId(),
                                pauta.getInicioVotacao(),
                                pauta.getFimVotacao()
                        );
                    });

            LocalDateTime dataEnvio = dto.dataEnvio();
            LocalDateTime inicioVotacao = pautaCache.inicioVotacao();
            LocalDateTime fimVotacao = pautaCache.fimVotacao();

            if (dataEnvio == null
                    || inicioVotacao == null
                    || fimVotacao == null
                    || dataEnvio.isBefore(inicioVotacao)
                    || dataEnvio.isAfter(fimVotacao)) {

                throw new ConflictException("A votação desta pauta não está aberta.");
            }

            Pauta pauta = pautaRepository.getReferenceById(dto.pautaId());
            Associado associado = associadoRepository.getReferenceById(dto.associadoId());

            Voto voto = VotoMapper.toEntity(dto, pauta, associado);

            votoRepository.save(voto);

        } catch (NotFoundException | ConflictException e) {
            salvarVotoInvalido(dto, e.getMessage());

        } catch (DataIntegrityViolationException e) {
            String mensagem = e.getMostSpecificCause().getMessage();

            if (mensagem.contains("uk_voto_associado_pauta")) {
                salvarVotoInvalido(dto, "O associado já votou nesta pauta.");
                return;
            }

            if (mensagem.contains("fk_voto_associado")) {
                salvarVotoInvalido(dto, "Associado não encontrado.");
                return;
            }

            salvarVotoInvalido(dto, "Falha ao salvar voto.");
        }
    }

    private void salvarVotoInvalido(VotoEventoDTO dto, String mensagem) {
        VotoInvalido votoInvalido = VotoMapper.toEntity(dto, mensagem);

        votoInvalidoRepository.save(votoInvalido);
    }
}
