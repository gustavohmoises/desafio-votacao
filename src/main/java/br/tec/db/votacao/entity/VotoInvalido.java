package br.tec.db.votacao.entity;

import br.tec.db.votacao.enums.TipoVotoEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "voto_invalido")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class VotoInvalido {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pauta_id")
    private UUID pautaId;

    @Column(name = "associado_id")
    private UUID associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private TipoVotoEnum voto;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(nullable = false, length = 255)
    private String mensagem;

    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
    }
}