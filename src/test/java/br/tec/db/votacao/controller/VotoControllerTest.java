package br.tec.db.votacao.controller;

import br.tec.db.votacao.service.PautaCacheService;
import br.tec.db.votacao.dto.Admin.LogarDTO;
import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.entity.Admin;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.enums.TipoVotoEnum;
import br.tec.db.votacao.messaging.producer.VotoProducer;
import br.tec.db.votacao.repository.AdminRepository;
import br.tec.db.votacao.repository.AssociadoRepository;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
public class VotoControllerTest {
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

    @MockitoBean
    private VotoProducer votoProducer;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    @BeforeEach
    void limparBanco() {
        votoRepository.deleteAll();
        associadoRepository.deleteAll();
        pautaRepository.deleteAll();
        adminRepository.deleteAll();
    }

    @Test
    void deveRegistrarVoto() throws Exception {
        LocalDateTime agora = LocalDateTime.now();

        Pauta pauta = Pauta.builder()
                .titulo("Pauta de teste")
                .descricao("Descrição da pauta")
                .dataCadastro(agora)
                .inicioVotacao(agora.minusMinutes(5))
                .fimVotacao(agora.plusMinutes(5))
                .build();

        pauta = pautaRepository.save(pauta);

        Associado associado = Associado.builder()
                .nome("João da Silva")
                .cpf("12345678901")
                .dataCadastro(agora)
                .build();

        associado = associadoRepository.save(associado);

        CriarVotoDTO dto = new CriarVotoDTO(
                pauta.getId(),
                associado.getId(),
                TipoVotoEnum.SIM
        );

        mockMvc.perform(post("/api/v1/votos")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + obterToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId().toString()))
                .andExpect(jsonPath("$.associadoId").value(associado.getId().toString()))
                .andExpect(jsonPath("$.voto").value("SIM"))
                .andExpect(jsonPath("$.dataEnvio").exists())
                .andExpect(jsonPath("$.mensagem").value("Voto em processamento."));

        verify(votoProducer).publicar(any(VotoEventoDTO.class));

        assertEquals(0, votoRepository.count());
    }

    private String obterToken() throws Exception {
        Admin admin = new Admin(
                "admin",
                passwordEncoder.encode("123456")
        );

        adminRepository.save(admin);

        LogarDTO dto = new LogarDTO(
                "admin",
                "123456"
        );

        MvcResult result = mockMvc.perform(post("/api/v1/admin/logar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper
                .readTree(result.getResponse().getContentAsString())
                .get("token")
                .asText();
    }
}