package br.tec.db.votacao.dto.Pauta;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ListarPautaDTO(
        UUID id,
        String titulo,
        String descricao,
        LocalDateTime dataCadastro,
        LocalDateTime inicioVotacao,
        LocalDateTime fimVotacao
) {
}
