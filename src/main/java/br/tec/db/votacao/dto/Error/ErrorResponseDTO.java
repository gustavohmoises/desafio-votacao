package br.tec.db.votacao.dto.Error;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String mensagem,
        LocalDateTime timestamp
) {
}