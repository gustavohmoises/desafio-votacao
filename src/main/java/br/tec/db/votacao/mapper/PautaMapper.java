package br.tec.db.votacao.mapper;

import br.tec.db.votacao.dto.Pauta.CriarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ListarPautaDTO;
import br.tec.db.votacao.entity.Pauta;

import java.util.List;

public final class PautaMapper {
    private PautaMapper() {
    }

    public static Pauta toEntity(CriarPautaDTO dto) {
        return Pauta.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .build();
    }

    public static ListarPautaDTO toDto(Pauta entity) {
        return ListarPautaDTO.builder()
                .id(entity.getId())
                .titulo(entity.getTitulo())
                .descricao(entity.getDescricao())
                .dataCadastro(entity.getDataCadastro())
                .inicioVotacao(entity.getInicioVotacao())
                .fimVotacao(entity.getFimVotacao())
                .build();
    }

    public static List<ListarPautaDTO> toDto(List<Pauta> pautas) {
        return pautas.stream()
                .map(PautaMapper::toDto)
                .toList();
    }
}
