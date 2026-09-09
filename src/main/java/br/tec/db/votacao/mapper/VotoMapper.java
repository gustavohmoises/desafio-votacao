package br.tec.db.votacao.mapper;

import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoInvalidoDTO;
import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.entity.VotoInvalido;

import java.time.LocalDateTime;
import java.util.List;

public final class VotoMapper {

    private VotoMapper() {
    }

    public static Voto toEntity(VotoEventoDTO dto, Pauta pauta, Associado associado) {
        return Voto.builder()
                .pauta(pauta)
                .associado(associado)
                .voto(dto.voto())
                .dataEnvio(dto.dataEnvio())
                .dataCadastro(LocalDateTime.now())
                .build();
    }

    public static VotoInvalido toEntity(VotoEventoDTO dto, String mensagem) {
        return VotoInvalido.builder()
                .pautaId(dto.pautaId())
                .associadoId(dto.associadoId())
                .voto(dto.voto())
                .dataEnvio(dto.dataEnvio())
                .dataCadastro(LocalDateTime.now())
                .mensagem(mensagem)
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

    public static ListarVotoInvalidoDTO toDto(VotoInvalido entity) {
        return ListarVotoInvalidoDTO.builder()
                .id(entity.getId())
                .pautaId(entity.getPautaId())
                .associadoId(entity.getAssociadoId())
                .voto(entity.getVoto())
                .dataEnvio(entity.getDataEnvio())
                .dataCadastro(entity.getDataCadastro())
                .mensagem(entity.getMensagem())
                .build();
    }

    public static List<ListarVotoDTO> toDtoList(List<Voto> votos) {
        return votos.stream()
                .map(VotoMapper::toDto)
                .toList();
    }

    public static List<ListarVotoInvalidoDTO> toDtoInvalidoList(List<VotoInvalido> votos) {
        return votos.stream()
                .map(VotoMapper::toDto)
                .toList();
    }
}