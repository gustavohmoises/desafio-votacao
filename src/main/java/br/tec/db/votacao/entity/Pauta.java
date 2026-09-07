package br.tec.db.votacao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pauta")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Pauta {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "inicio_votacao")
    private LocalDateTime inicioVotacao;

    @Column(name = "fim_votacao")
    private LocalDateTime fimVotacao;

    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
    }

    public boolean votacaoAberta() {
        if (inicioVotacao == null || fimVotacao == null) {
            return false;
        }

        LocalDateTime agora = LocalDateTime.now();
        return !agora.isBefore(inicioVotacao) && agora.isBefore(fimVotacao);
    }
}
