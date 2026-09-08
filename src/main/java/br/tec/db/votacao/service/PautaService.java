package br.tec.db.votacao.service;

import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Pauta.AbrirVotacaoDTO;
import br.tec.db.votacao.dto.Pauta.CriarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ListarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ResultadoVotacaoDTO;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.enums.TipoVotoEnum;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.exception.NotFoundException;
import br.tec.db.votacao.mapper.PautaMapper;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PautaService {
    private final PautaRepository pautaRepository;
    private final VotoRepository votoRepository;
    private final PautaCacheService pautaCacheService;

    public PautaService(PautaRepository pautaRepository, VotoRepository votoRepository, PautaCacheService pautaCacheService) {
        this.pautaRepository = pautaRepository;
        this.votoRepository = votoRepository;
        this.pautaCacheService = pautaCacheService;
    }

    @Transactional
    public ListarPautaDTO create(CriarPautaDTO dto) {
        Pauta pauta = PautaMapper.toEntity(dto);
        return PautaMapper.toDto(pautaRepository.save(pauta));
    }

    @Transactional(readOnly = true)
    public List<ListarPautaDTO> getAll() {
        return PautaMapper.toDto(pautaRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ListarPautaDTO getById(UUID id) {
        Pauta pauta = pautaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pauta não localizada."));

        return PautaMapper.toDto(pauta);
    }

    @Transactional
    public void delete(UUID id) {
        Pauta pauta = pautaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pauta não localizada."));

        pautaRepository.delete(pauta);
    }

    @Transactional
    public ListarPautaDTO abrirVotacao(UUID pautaId, AbrirVotacaoDTO dto) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new NotFoundException("Pauta não localizada."));

        if (pauta.votacaoAberta()) {
            throw new ConflictException("A votação desta pauta já está aberta.");
        }

        LocalDateTime inicioVotacao = LocalDateTime.now();

        int duracaoMinutos = dto != null && dto.duracaoMinutos() != null  ? dto.duracaoMinutos()  : 1;

        pauta.setInicioVotacao(inicioVotacao);
        pauta.setFimVotacao(inicioVotacao.plusMinutes(duracaoMinutos));

        Pauta pautaSalva = pautaRepository.save(pauta);

        pautaCacheService.salvar(pauta);

        return PautaMapper.toDto(pautaSalva);
    }

    @Transactional(readOnly = true)
    public ResultadoVotacaoDTO resultadoVotacao(UUID pautaId) {
        Pauta pauta = pautaRepository.findById(pautaId)
                .orElseThrow(() -> new NotFoundException("Pauta não localizada."));

        if (pauta.getFimVotacao() == null) {
            throw new ConflictException("A votação desta pauta ainda não foi aberta.");
        }

        if (pauta.votacaoAberta()) {
            throw new ConflictException("A votação desta pauta ainda está em andamento.");
        }

        List<Voto> votos = votoRepository.findByPautaId(pautaId);

        long votosSim = votos.stream()
                .filter(voto -> voto.getVoto() == TipoVotoEnum.SIM)
                .count();

        long votosNao = votos.stream()
                .filter(voto -> voto.getVoto() == TipoVotoEnum.NAO)
                .count();

        long totalVotos = votos.size();

        String resultado;

        if (votosSim > votosNao) {
            resultado = "APROVADA";
        } else if (votosNao > votosSim) {
            resultado = "REPROVADA";
        } else {
            resultado = "EMPATE";
        }

        return ResultadoVotacaoDTO.builder()
                .pautaId(pauta.getId())
                .titulo(pauta.getTitulo())
                .totalVotos(totalVotos)
                .votosSim(votosSim)
                .votosNao(votosNao)
                .resultado(resultado)
                .build();
    }

}
