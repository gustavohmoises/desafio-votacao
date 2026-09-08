package br.tec.db.votacao.service;

import br.tec.db.votacao.cache.PautaCache;
import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.exception.NotFoundException;
import br.tec.db.votacao.mapper.VotoMapper;
import br.tec.db.votacao.repository.AssociadoRepository;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VotoService {
    private final VotoRepository votoRepository;
    private final PautaRepository pautaRepository;
    private final AssociadoRepository associadoRepository;
    private final PautaCacheService pautaCacheService;

    public VotoService(VotoRepository votoRepository, PautaRepository pautaRepository, AssociadoRepository associadoRepository, PautaCacheService pautaCacheService) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.associadoRepository = associadoRepository;
        this.pautaCacheService = pautaCacheService;
    }

    @Transactional(readOnly = true)
    public List<ListarVotoDTO> getAll() {
        return VotoMapper.toDto(votoRepository.findAll());
    }

    @Transactional
    public ListarVotoDTO votar(CriarVotoDTO dto) {
        PautaCache pautaCache = pautaCacheService.buscar(dto.pautaId())
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada ou não aberta para votação."));

        if (!pautaCache.votacaoAberta()) {
            throw new ConflictException("A votação desta pauta não está aberta.");
        }

        Pauta pauta = pautaRepository.getReferenceById(dto.pautaId());
        Associado associado = associadoRepository.getReferenceById(dto.associadoId());

        Voto voto = VotoMapper.toEntity(dto, pauta, associado);

        try {
            Voto votoSalvo = votoRepository.saveAndFlush(voto);

            return VotoMapper.toDto(votoSalvo);
        } catch (DataIntegrityViolationException e) {
            String mensagem = e.getMostSpecificCause().getMessage();

            if (mensagem.contains("uk_voto_associado_pauta")) {
                throw new ConflictException("O associado já votou nesta pauta.");
            }

            if (mensagem.contains("fk_voto_associado")) {
                throw new NotFoundException("Associado não encontrado.");
            }

            throw new DataIntegrityViolationException("Falha ao salvar voto.");
        }
    }
}
