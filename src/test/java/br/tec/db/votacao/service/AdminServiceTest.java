package br.tec.db.votacao.service;

import br.tec.db.votacao.config.security.TokenService;
import br.tec.db.votacao.dto.Admin.ListarAdminDTO;
import br.tec.db.votacao.dto.Admin.LogarDTO;
import br.tec.db.votacao.dto.Admin.RegistrarDTO;
import br.tec.db.votacao.dto.Admin.TokenDTO;
import br.tec.db.votacao.entity.Admin;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AdminService adminService;

    @Test
    void deveRegistrarAdmin() {
        RegistrarDTO dto = new RegistrarDTO(
                "admin",
                "123456"
        );

        Admin admin = new Admin(
                "admin",
                "senha-criptografada"
        );

        when(adminRepository.existsByLogin(dto.login()))
                .thenReturn(false);

        when(passwordEncoder.encode(dto.senha()))
                .thenReturn("senha-criptografada");

        when(adminRepository.save(any(Admin.class)))
                .thenReturn(admin);

        ListarAdminDTO resultado = adminService.registrar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.login()).isEqualTo("admin");

        verify(adminRepository).existsByLogin("admin");
        verify(passwordEncoder).encode("123456");

        ArgumentCaptor<Admin> captor = ArgumentCaptor.forClass(Admin.class);

        verify(adminRepository).save(captor.capture());

        Admin adminSalvo = captor.getValue();

        assertThat(adminSalvo.getLogin()).isEqualTo("admin");
        assertThat(adminSalvo.getSenha()).isEqualTo("senha-criptografada");
    }

    @Test
    void deveLancarExcecaoAoRegistrarAdminComLoginExistente() {
        RegistrarDTO dto = new RegistrarDTO(
                "admin",
                "123456"
        );

        when(adminRepository.existsByLogin(dto.login()))
                .thenReturn(true);

        assertThatThrownBy(() -> adminService.registrar(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Já existe um administrador com este login.");

        verify(adminRepository).existsByLogin("admin");

        verify(passwordEncoder, never()).encode(anyString());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void deveRealizarLogin() {
        LogarDTO dto = new LogarDTO(
                "admin",
                "123456"
        );

        Admin admin = new Admin(
                "admin",
                "senha-criptografada"
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        admin,
                        null,
                        admin.getAuthorities()
                );

        when(authenticationManager.authenticate(any(
                UsernamePasswordAuthenticationToken.class
        ))).thenReturn(authentication);

        when(tokenService.generateToken(admin))
                .thenReturn("jwt-token");

        TokenDTO resultado = adminService.logar(dto);

        assertThat(resultado).isNotNull();
        assertThat(resultado.token()).isEqualTo("jwt-token");

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager).authenticate(captor.capture());

        UsernamePasswordAuthenticationToken authenticationEnviada = captor.getValue();

        assertThat(authenticationEnviada.getPrincipal()).isEqualTo("admin");
        assertThat(authenticationEnviada.getCredentials()).isEqualTo("123456");

        verify(tokenService).generateToken(admin);
    }
}
