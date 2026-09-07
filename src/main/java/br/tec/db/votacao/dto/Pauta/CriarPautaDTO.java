package br.tec.db.votacao.dto.Pauta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CriarPautaDTO(
        @NotBlank(message = "Título é obrigatório")
        @Size(max = 255, message = "Título deve possuir no máximo 255 caracteres")
        String titulo,

        @NotBlank(message = "Descrição é obrigatória")
        String descricao
) {
}
