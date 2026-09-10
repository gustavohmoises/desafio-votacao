package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Admin.LogarDTO;
import br.tec.db.votacao.dto.Admin.RegistrarDTO;
import br.tec.db.votacao.entity.Admin;
import br.tec.db.votacao.repository.AdminRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void limparBanco() {
        adminRepository.deleteAll();
    }

    @Test
    void deveRegistrarAdmin() throws Exception {
        var dto = new RegistrarDTO(
                "admin",
                "123456"
        );

        mockMvc.perform(post("/api/v1/admin/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.login").value("admin"))
                .andExpect(jsonPath("$.dataCadastro").isNotEmpty());

        var admin = adminRepository.findByLogin("admin");

        assertThat(admin).isPresent();
        assertThat(admin.get().getLogin()).isEqualTo("admin");
        assertThat(admin.get().getSenha()).isNotEqualTo("123456");
        assertThat(passwordEncoder.matches("123456", admin.get().getSenha())).isTrue();
    }

    @Test
    void naoDeveRegistrarAdminComLoginDuplicado() throws Exception {
        var admin = new Admin(
                "admin",
                passwordEncoder.encode("123456")
        );

        adminRepository.save(admin);

        var dto = new RegistrarDTO(
                "admin",
                "outra-senha"
        );

        mockMvc.perform(post("/api/v1/admin/registrar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem")
                        .value("Já existe um administrador com este login."));
    }

    @Test
    void deveLogarAdmin() throws Exception {
        var admin = new Admin(
                "admin",
                passwordEncoder.encode("123456")
        );

        adminRepository.save(admin);

        var dto = new LogarDTO(
                "admin",
                "123456"
        );

        mockMvc.perform(post("/api/v1/admin/logar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void naoDeveLogarComSenhaInvalida() throws Exception {
        var admin = new Admin(
                "admin",
                passwordEncoder.encode("123456")
        );

        adminRepository.save(admin);

        var dto = new LogarDTO(
                "admin",
                "senha-incorreta"
        );

        mockMvc.perform(post("/api/v1/admin/logar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

}
