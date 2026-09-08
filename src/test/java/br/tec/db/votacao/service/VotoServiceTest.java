package br.tec.db.votacao.service;

import br.tec.db.votacao.cache.PautaCache;
import br.tec.db.votacao.cache.PautaCacheService;
import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.enums.TipoVotoEnum;
import br.tec.db.votacao.repository.AssociadoRepository;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotoServiceTest {

    @Mock
    private VotoRepository votoRepository;

    @Mock
    private PautaRepository pautaRepository;

    @Mock
    private AssociadoRepository associadoRepository;

    @Mock
    private PautaCacheService pautaCacheService;

    @InjectMocks
    private VotoService votoService;

    @Test
    void deveRealizarVotoComSucesso() {
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        CriarVotoDTO dto = new CriarVotoDTO(
                pautaId,
                associadoId,
                TipoVotoEnum.SIM
        );

        LocalDateTime inicioVotacao = LocalDateTime.now().minusMinutes(1);
        LocalDateTime fimVotacao = LocalDateTime.now().plusMinutes(10);

        PautaCache pautaCache = new PautaCache(
                pautaId,
                inicioVotacao,
                fimVotacao
        );

        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Pauta teste")
                .descricao("Descrição da pauta")
                .inicioVotacao(inicioVotacao)
                .fimVotacao(fimVotacao)
                .build();

        Associado associado = Associado.builder()
                .id(associadoId)
                .nome("João")
                .cpf("12345678901")
                .build();

        Voto votoSalvo = Voto.builder()
                .id(UUID.randomUUID())
                .pauta(pauta)
                .associado(associado)
                .voto(TipoVotoEnum.SIM)
                .build();

        when(pautaCacheService.buscar(pautaId))
                .thenReturn(Optional.of(pautaCache));

        when(pautaRepository.getReferenceById(pautaId))
                .thenReturn(pauta);

        when(associadoRepository.getReferenceById(associadoId))
                .thenReturn(associado);

        when(votoRepository.saveAndFlush(any(Voto.class)))
                .thenReturn(votoSalvo);

        ListarVotoDTO resultado = votoService.votar(dto);

        assertNotNull(resultado);
        assertEquals(votoSalvo.getId(), resultado.id());
        assertEquals(pautaId, resultado.pautaId());
        assertEquals(associadoId, resultado.associadoId());
        assertEquals(TipoVotoEnum.SIM, resultado.voto());

        verify(pautaCacheService).buscar(pautaId);
        verify(pautaRepository).getReferenceById(pautaId);
        verify(associadoRepository).getReferenceById(associadoId);
        verify(votoRepository).saveAndFlush(any(Voto.class));

        verify(pautaRepository, never()).findById(any());
        verify(associadoRepository, never()).findById(any());
        verify(votoRepository, never()).save(any());
    }
}