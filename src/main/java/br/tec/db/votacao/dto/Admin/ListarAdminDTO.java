package br.tec.db.votacao.dto.Admin;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ListarAdminDTO(
        UUID id,
        String login,
        LocalDateTime dataCadastro
) {
}
