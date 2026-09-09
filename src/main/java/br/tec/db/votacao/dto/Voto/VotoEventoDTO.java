package br.tec.db.votacao.dto.Voto;

import br.tec.db.votacao.enums.TipoVotoEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record VotoEventoDTO(
        UUID pautaId,
        UUID associadoId,
        TipoVotoEnum voto,
        LocalDateTime dataEnvio,
        String mensagem
) {
}