package br.tec.db.votacao.dto.Pauta;

import java.time.LocalDateTime;
import java.util.UUID;

public record PautaCacheDTO(
        UUID id,
        LocalDateTime inicioVotacao,
        LocalDateTime fimVotacao
) {
}
