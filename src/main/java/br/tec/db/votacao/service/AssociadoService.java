package br.tec.db.votacao.service;

import br.tec.db.votacao.dto.Associado.CriarAssociadoDTO;
import br.tec.db.votacao.dto.Associado.ListarAssociadoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.exception.NotFoundException;
import br.tec.db.votacao.mapper.AssociadoMapper;
import br.tec.db.votacao.repository.AssociadoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AssociadoService {
    private final AssociadoRepository associadoRepository;

    public AssociadoService(AssociadoRepository associadoRepository) {
        this.associadoRepository = associadoRepository;
    }

    @Transactional
    public ListarAssociadoDTO create(CriarAssociadoDTO dto) {
        try {
            Associado associado = AssociadoMapper.toEntity(dto);
            return AssociadoMapper.toDto(associadoRepository.saveAndFlush(associado));
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Já existe um associado com este CPF.");
        }
    }

    @Transactional(readOnly = true)
    public List<ListarAssociadoDTO> getAll() {
        return AssociadoMapper.toDto(associadoRepository.findAll());
    }

    @Transactional(readOnly = true)
    public ListarAssociadoDTO getById(UUID id) {
        Associado associado = associadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Associado não localizado."));

        return AssociadoMapper.toDto(associado);
    }

    @Transactional
    public void delete(UUID id) {
        Associado associado = associadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Associado não localizado."));

        associadoRepository.delete(associado);
    }

}
