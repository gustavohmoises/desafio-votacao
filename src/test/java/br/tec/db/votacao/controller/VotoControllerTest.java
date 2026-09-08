package br.tec.db.votacao.controller;

import br.tec.db.votacao.cache.PautaCache;
import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.enums.TipoVotoEnum;
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
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void limparBanco() {
        votoRepository.deleteAll();
        associadoRepository.deleteAll();
        pautaRepository.deleteAll();
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
                .build();

        associado = associadoRepository.save(associado);

        PautaCache pautaCache = new PautaCache(
                pauta.getId(),
                pauta.getInicioVotacao(),
                pauta.getFimVotacao()
        );

        when(pautaCacheService.buscar(pauta.getId()))
                .thenReturn(Optional.of(pautaCache));

        CriarVotoDTO dto = new CriarVotoDTO(
                pauta.getId(),
                associado.getId(),
                TipoVotoEnum.SIM
        );

        mockMvc.perform(post("/api/v1/votos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.pautaId").value(pauta.getId().toString()))
                .andExpect(jsonPath("$.associadoId").value(associado.getId().toString()))
                .andExpect(jsonPath("$.voto").value("SIM"));

        assertEquals(1, votoRepository.count());

        Voto votoSalvo = votoRepository.findAll().get(0);

        assertEquals(pauta.getId(), votoSalvo.getPauta().getId());
        assertEquals(associado.getId(), votoSalvo.getAssociado().getId());
        assertEquals(TipoVotoEnum.SIM, votoSalvo.getVoto());

        verify(pautaCacheService).buscar(pauta.getId());
    }
}