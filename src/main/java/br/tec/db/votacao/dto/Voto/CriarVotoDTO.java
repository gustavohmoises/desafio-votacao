package br.tec.db.votacao.dto.Voto;

import br.tec.db.votacao.enums.TipoVotoEnum;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CriarVotoDTO(
        @NotNull(message = "Pauta é obrigatória")
        UUID pautaId,

        @NotNull(message = "Associado é obrigatório")
        UUID associadoId,

        @NotNull(message = "Voto é obrigatório")
        TipoVotoEnum voto
) {
}
