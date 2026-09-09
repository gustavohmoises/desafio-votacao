package br.tec.db.votacao.dto.Voto;

import br.tec.db.votacao.enums.TipoVotoEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ListarVotoInvalidoDTO(
        UUID id,
        UUID pautaId,
        UUID associadoId,
        TipoVotoEnum voto,
        LocalDateTime dataEnvio,
        LocalDateTime dataCadastro,
        String mensagem
) {
}