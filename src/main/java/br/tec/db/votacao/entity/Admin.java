package br.tec.db.votacao.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "admin")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String login;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String senha;

    @Column(name = "data_cadastro", nullable = false)
    private LocalDateTime dataCadastro;

    public Admin(String login, String senha) {
        this.login = login;
        this.senha = senha;
        this.dataCadastro = LocalDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}