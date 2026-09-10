package br.tec.db.votacao.dto.Admin;

import jakarta.validation.constraints.NotBlank;

public record LogarDTO(
        @NotBlank(message = "Login é obrigatório")
        String login,

        @NotBlank(message = "Senha é obrigatória")
        String senha
) {
}
