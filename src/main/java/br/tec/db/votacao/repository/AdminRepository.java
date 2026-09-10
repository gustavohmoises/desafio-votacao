package br.tec.db.votacao.repository;

import br.tec.db.votacao.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByLogin(String login);
    boolean existsByLogin(String login);
}

