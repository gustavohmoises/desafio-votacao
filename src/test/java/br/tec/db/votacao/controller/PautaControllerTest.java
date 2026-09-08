package br.tec.db.votacao.controller;

import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Pauta.AbrirVotacaoDTO;
import br.tec.db.votacao.dto.Pauta.CriarPautaDTO;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.repository.AssociadoRepository;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class PautaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PautaRepository pautaRepository;

    @Autowired
    private AssociadoRepository associadoRepository;

    @Autowired
    private VotoRepository votoRepository;

    @MockitoBean
    private PautaCacheService pautaCacheService;

    @BeforeEach
    void limparBanco() {
        votoRepository.deleteAll();
        associadoRepository.deleteAll();
        pautaRepository.deleteAll();
    }

    @Test
    void deveCriarPauta() throws Exception {
        CriarPautaDTO dto = new CriarPautaDTO(
                "Pauta de teste",
                "Descrição da pauta"
        );

        mockMvc.perform(post("/api/v1/pautas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.titulo").value("Pauta de teste"))
                .andExpect(jsonPath("$.descricao").value("Descrição da pauta"));

        assertEquals(1, pautaRepository.count());
    }

    @Test
    void deveAbrirVotacao() throws Exception {
        Pauta pauta = Pauta.builder()
                .titulo("Pauta de teste")
                .descricao("Descrição")
                .dataCadastro(LocalDateTime.now())
                .build();

        pauta = pautaRepository.save(pauta);

        AbrirVotacaoDTO dto = new AbrirVotacaoDTO(10);

        mockMvc.perform(post("/api/v1/pautas/" + pauta.getId() + "/abrir-votacao")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pauta.getId().toString()))
                .andExpect(jsonPath("$.inicioVotacao").exists())
                .andExpect(jsonPath("$.fimVotacao").exists());

        Pauta pautaAtualizada = pautaRepository
                .findById(pauta.getId())
                .orElseThrow();

        assertNotNull(pautaAtualizada.getInicioVotacao());
        assertNotNull(pautaAtualizada.getFimVotacao());
    }
}