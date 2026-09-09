package br.tec.db.votacao.repository;

import br.tec.db.votacao.entity.VotoInvalido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VotoInvalidoRepository extends JpaRepository<VotoInvalido, UUID> {
}
