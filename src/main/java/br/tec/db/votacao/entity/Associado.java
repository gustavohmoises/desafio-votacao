package br.tec.db.votacao.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "associado")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Associado {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, length = 11, unique = true)
    private String cpf;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    @PrePersist
    public void prePersist() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
    }
}
