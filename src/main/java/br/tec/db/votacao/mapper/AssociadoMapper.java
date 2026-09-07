package br.tec.db.votacao.mapper;

import br.tec.db.votacao.dto.Associado.CriarAssociadoDTO;
import br.tec.db.votacao.dto.Associado.ListarAssociadoDTO;
import br.tec.db.votacao.entity.Associado;

import java.util.List;

public final class AssociadoMapper {
    private AssociadoMapper() {
    }

    public static Associado toEntity(CriarAssociadoDTO dto) {
        return Associado.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .build();
    }

    public static ListarAssociadoDTO toDto(Associado entity) {
        return ListarAssociadoDTO.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .cpf(entity.getCpf())
                .dataCadastro(entity.getDataCadastro())
                .build();
    }

    public static List<ListarAssociadoDTO> toDto(List<Associado> associados) {
        return associados.stream()
                .map(AssociadoMapper::toDto)
                .toList();
    }
}