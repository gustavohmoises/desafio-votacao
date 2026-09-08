package br.tec.db.votacao.service;

import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Pauta.AbrirVotacaoDTO;
import br.tec.db.votacao.dto.Pauta.CriarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ListarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ResultadoVotacaoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.enums.TipoVotoEnum;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PautaServiceTest {

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaCacheService pautaCacheService;

    @InjectMocks
    private PautaService pautaService;

    @Test
    void deveCriarPautaComSucesso() {
        UUID id = UUID.randomUUID();

        CriarPautaDTO dto = new CriarPautaDTO(
                "Pauta teste",
                "Descrição da pauta"
        );

        Pauta pauta = Pauta.builder()
                .id(id)
                .titulo("Pauta teste")
                .descricao("Descrição da pauta")
                .dataCadastro(LocalDateTime.now())
                .build();

        when(pautaRepository.save(any(Pauta.class)))
                .thenReturn(pauta);

        ListarPautaDTO resultado = pautaService.create(dto);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
        assertEquals("Pauta teste", resultado.titulo());
        assertEquals("Descrição da pauta", resultado.descricao());

        verify(pautaRepository).save(any(Pauta.class));
    }

    @Test
    void deveAbrirVotacaoComDuracaoInformada() {
        UUID pautaId = UUID.randomUUID();

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Pauta teste")
                .descricao("Descrição da pauta")
                .dataCadastro(LocalDateTime.now())
                .build();

        AbrirVotacaoDTO dto = new AbrirVotacaoDTO(10);

        when(pautaRepository.findById(pautaId))
                .thenReturn(Optional.of(pauta));

        when(pautaRepository.save(any(Pauta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ListarPautaDTO resultado = pautaService.abrirVotacao(pautaId, dto);

        assertNotNull(resultado);
        assertEquals(pautaId, resultado.id());
        assertNotNull(resultado.inicioVotacao());
        assertNotNull(resultado.fimVotacao());

        assertEquals(
                resultado.inicioVotacao().plusMinutes(10),
                resultado.fimVotacao()
        );

        verify(pautaRepository).findById(pautaId);
        verify(pautaRepository).save(pauta);
    }

    @Test
    void deveAbrirVotacaoComDuracaoPadraoDeUmMinuto() {
        UUID pautaId = UUID.randomUUID();

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Pauta teste")
                .descricao("Descrição da pauta")
                .dataCadastro(LocalDateTime.now())
                .build();

        AbrirVotacaoDTO dto = new AbrirVotacaoDTO(null);

        when(pautaRepository.findById(pautaId))
                .thenReturn(Optional.of(pauta));

        when(pautaRepository.save(any(Pauta.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ListarPautaDTO resultado = pautaService.abrirVotacao(pautaId, dto);

        assertNotNull(resultado.inicioVotacao());
        assertNotNull(resultado.fimVotacao());

        assertEquals(
                resultado.inicioVotacao().plusMinutes(1),
                resultado.fimVotacao()
        );

        verify(pautaRepository).save(pauta);
    }

    @Test
    void deveRetornarResultadoComoAprovada() {
        UUID pautaId = UUID.randomUUID();

        Pauta pauta = criarPautaEncerrada(pautaId);

        Associado associado = criarAssociado();

        List<Voto> votos = List.of(
                criarVoto(pauta, associado, TipoVotoEnum.SIM),
                criarVoto(pauta, associado, TipoVotoEnum.SIM),
                criarVoto(pauta, associado, TipoVotoEnum.SIM),
                criarVoto(pauta, associado, TipoVotoEnum.NAO)
        );

        when(pautaRepository.findById(pautaId))
                .thenReturn(Optional.of(pauta));

        when(votoRepository.findByPautaId(pautaId))
                .thenReturn(votos);

        ResultadoVotacaoDTO resultado =
                pautaService.resultadoVotacao(pautaId);

        assertEquals(pautaId, resultado.pautaId());
        assertEquals("Pauta teste", resultado.titulo());
        assertEquals(4, resultado.totalVotos());
        assertEquals(3, resultado.votosSim());
        assertEquals(1, resultado.votosNao());
        assertEquals("APROVADA", resultado.resultado());

        verify(votoRepository).findByPautaId(pautaId);
    }

    @Test
    void deveRetornarResultadoComoReprovada() {
        UUID pautaId = UUID.randomUUID();

        Pauta pauta = criarPautaEncerrada(pautaId);
        Associado associado = criarAssociado();

        List<Voto> votos = List.of(
                criarVoto(pauta, associado, TipoVotoEnum.NAO),
                criarVoto(pauta, associado, TipoVotoEnum.NAO),
                criarVoto(pauta, associado, TipoVotoEnum.NAO),
                criarVoto(pauta, associado, TipoVotoEnum.SIM)
        );

        when(pautaRepository.findById(pautaId))
                .thenReturn(Optional.of(pauta));

        when(votoRepository.findByPautaId(pautaId))
                .thenReturn(votos);

        ResultadoVotacaoDTO resultado =
                pautaService.resultadoVotacao(pautaId);

        assertEquals(4, resultado.totalVotos());
        assertEquals(1, resultado.votosSim());
        assertEquals(3, resultado.votosNao());
        assertEquals("REPROVADA", resultado.resultado());
    }

    @Test
    void deveRetornarResultadoComoEmpate() {
        UUID pautaId = UUID.randomUUID();

        Pauta pauta = criarPautaEncerrada(pautaId);
        Associado associado = criarAssociado();

        List<Voto> votos = List.of(
                criarVoto(pauta, associado, TipoVotoEnum.SIM),
                criarVoto(pauta, associado, TipoVotoEnum.SIM),
                criarVoto(pauta, associado, TipoVotoEnum.NAO),
                criarVoto(pauta, associado, TipoVotoEnum.NAO)
        );

        when(pautaRepository.findById(pautaId))
                .thenReturn(Optional.of(pauta));

        when(votoRepository.findByPautaId(pautaId))
                .thenReturn(votos);

        ResultadoVotacaoDTO resultado =
                pautaService.resultadoVotacao(pautaId);

        assertEquals(4, resultado.totalVotos());
        assertEquals(2, resultado.votosSim());
        assertEquals(2, resultado.votosNao());
        assertEquals("EMPATE", resultado.resultado());
    }

    private Pauta criarPautaEncerrada(UUID pautaId) {
        return Pauta.builder()
                .id(pautaId)
                .titulo("Pauta teste")
                .descricao("Descrição da pauta")
                .dataCadastro(LocalDateTime.now())
                .inicioVotacao(LocalDateTime.now().minusMinutes(10))
                .fimVotacao(LocalDateTime.now().minusMinutes(1))
                .build();
    }

    private Associado criarAssociado() {
        return Associado.builder()
                .id(UUID.randomUUID())
                .nome("João")
                .cpf("12345678901")
                .build();
    }

    private Voto criarVoto(
            Pauta pauta,
            Associado associado,
            TipoVotoEnum tipo
    ) {
        return Voto.builder()
                .id(UUID.randomUUID())
                .pauta(pauta)
                .associado(associado)
                .voto(tipo)
                .build();
    }
}