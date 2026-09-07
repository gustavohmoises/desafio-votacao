package br.tec.db.votacao.dto.Error;

import java.time.OffsetDateTime;

public record ErrorResponseDTO(
        int status,
        String mensagem,
        OffsetDateTime timestamp
) {
}