package br.tec.db.votacao.service;

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

    public VotoService(VotoRepository votoRepository, PautaRepository pautaRepository, AssociadoRepository associadoRepository) {
        this.votoRepository = votoRepository;
        this.pautaRepository = pautaRepository;
        this.associadoRepository = associadoRepository;
    }

    @Transactional(readOnly = true)
    public List<ListarVotoDTO> getAll() {
        return VotoMapper.toDto(votoRepository.findAll());
    }

    @Transactional
    public ListarVotoDTO votar(CriarVotoDTO dto) {
        Pauta pauta = pautaRepository.findById(dto.pautaId())
                .orElseThrow(() -> new NotFoundException("Pauta não encontrada."));

        if (!pauta.votacaoAberta()) {
            throw new ConflictException("A votação desta pauta não está aberta.");
        }

        Associado associado = associadoRepository.findById(dto.associadoId())
                .orElseThrow(() -> new NotFoundException("Associado não encontrado."));

        Voto voto = VotoMapper.toEntity(dto, pauta, associado);

        try {
            return VotoMapper.toDto(votoRepository.save(voto));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("O associado já votou nesta pauta.");
        }
    }
}
