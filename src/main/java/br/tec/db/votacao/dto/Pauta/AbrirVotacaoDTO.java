package br.tec.db.votacao.dto.Pauta;

import jakarta.validation.constraints.Min;

public record AbrirVotacaoDTO(
        @Min(value = 1, message = "A duração deve ser de no mínimo 1 minuto")
        Integer duracaoMinutos
) {
}