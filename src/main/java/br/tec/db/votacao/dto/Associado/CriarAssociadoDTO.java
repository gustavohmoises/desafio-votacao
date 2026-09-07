package br.tec.db.votacao.dto.Associado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CriarAssociadoDTO(
        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 255, message = "Nome deve possuir no máximo 255 caracteres")
        String nome,

        @NotBlank(message = "CPF é obrigatório")
        @Size(min = 11, max = 11, message = "CPF deve possuir 11 caracteres")
        String cpf
) {
}