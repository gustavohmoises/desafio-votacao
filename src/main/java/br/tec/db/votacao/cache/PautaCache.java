package br.tec.db.votacao.cache;

import java.time.LocalDateTime;
import java.util.UUID;

public record PautaCache(
        UUID id,
        LocalDateTime inicioVotacao,
        LocalDateTime fimVotacao
) {
    public boolean votacaoAberta() {
        LocalDateTime agora = LocalDateTime.now();

        return !agora.isBefore(inicioVotacao) && agora.isBefore(fimVotacao);
    }
}
