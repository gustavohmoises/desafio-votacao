package br.tec.db.votacao.mapper;

import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;

import java.time.LocalDateTime;
import java.util.List;

public final class VotoMapper {
    private VotoMapper() {
    }

    public static Voto toEntity(CriarVotoDTO dto, Pauta pauta, Associado associado) {
        return Voto.builder()
                .pauta(pauta)
                .associado(associado)
                .voto(dto.voto())
                .dataCadastro(LocalDateTime.now())
                .build();
    }

    public static ListarVotoDTO toDto(Voto entity) {
        return ListarVotoDTO.builder()
                .id(entity.getId())
                .pautaId(entity.getPauta().getId())
                .associadoId(entity.getAssociado().getId())
                .voto(entity.getVoto())
                .dataCadastro(entity.getDataCadastro())
                .build();
    }

    public static List<ListarVotoDTO> toDto(List<Voto> votos) {
        return votos.stream()
                .map(VotoMapper::toDto)
                .toList();
    }
}
