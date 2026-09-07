package br.tec.db.votacao.dto.Pauta;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ResultadoVotacaoDTO(
        UUID pautaId,
        String titulo,
        long totalVotos,
        long votosSim,
        long votosNao,
        String resultado
) {
}
