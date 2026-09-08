package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Associado.CriarAssociadoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.repository.AssociadoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AssociadoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AssociadoRepository associadoRepository;

    @BeforeEach
    void limparBanco() {
        associadoRepository.deleteAll();
    }

    @Test
    void deveCriarAssociadoComSucesso() throws Exception {
        CriarAssociadoDTO dto = new CriarAssociadoDTO(
                "João da Silva",
                "12345678901"
        );

        mockMvc.perform(post("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("João da Silva"))
                .andExpect(jsonPath("$.cpf").value("12345678901"))
                .andExpect(jsonPath("$.dataCadastro").exists());

        assertEquals(1, associadoRepository.count());

        Associado associado = associadoRepository.findAll().get(0);

        assertEquals("João da Silva", associado.getNome());
        assertEquals("12345678901", associado.getCpf());
        assertNotNull(associado.getId());
        assertNotNull(associado.getDataCadastro());
    }

    @Test
    void deveRetornarConflictQuandoCpfJaExistir() throws Exception {
        CriarAssociadoDTO primeiroDto = new CriarAssociadoDTO(
                "João da Silva",
                "12345678901"
        );

        mockMvc.perform(post("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(primeiroDto)))
                .andExpect(status().isCreated());

        CriarAssociadoDTO segundoDto = new CriarAssociadoDTO(
                "Maria da Silva",
                "12345678901"
        );

        mockMvc.perform(post("/api/v1/associados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(segundoDto)))
                .andExpect(status().isConflict());

        assertEquals(1, associadoRepository.count());
    }
}