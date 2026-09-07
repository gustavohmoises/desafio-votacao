package br.tec.db.votacao.dto.Associado;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record ListarAssociadoDTO(
        UUID id,
        String nome,
        String cpf,
        LocalDateTime dataCadastro
) {
}
