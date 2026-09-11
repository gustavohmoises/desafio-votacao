package br.tec.db.votacao.service;

import br.tec.db.votacao.dto.Pauta.PautaCacheDTO;
import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.entity.Associado;
import br.tec.db.votacao.entity.Pauta;
import br.tec.db.votacao.entity.Voto;
import br.tec.db.votacao.entity.VotoInvalido;
import br.tec.db.votacao.enums.TipoVotoEnum;
import br.tec.db.votacao.messaging.producer.VotoProducer;
import br.tec.db.votacao.repository.AssociadoRepository;
import br.tec.db.votacao.repository.PautaRepository;
import br.tec.db.votacao.repository.VotoInvalidoRepository;
import br.tec.db.votacao.repository.VotoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Mock
    private VotoProducer votoProducer;

    @Mock
    private VotoInvalidoRepository votoInvalidoRepository;

    @Test
    void deveCadastrarVotoParaProcessar() {
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        CriarVotoDTO dto = new CriarVotoDTO(
                pautaId,
                associadoId,
                TipoVotoEnum.SIM
        );

        doNothing().when(votoProducer).publicar(any(VotoEventoDTO.class));

        VotoEventoDTO resultado = votoService.votar(dto);

        assertNotNull(resultado);
        assertEquals(pautaId, resultado.pautaId());
        assertEquals(associadoId, resultado.associadoId());
        assertEquals(TipoVotoEnum.SIM, resultado.voto());
        assertNotNull(resultado.dataEnvio());
        assertEquals("Voto em processamento.", resultado.mensagem());

        verify(votoProducer).publicar(any(VotoEventoDTO.class));

        verifyNoInteractions(
                pautaCacheService,
                pautaRepository,
                associadoRepository,
                votoRepository
        );
    }

    @Test
    void deveSalvarVotoValido() {
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        LocalDateTime inicio = LocalDateTime.now().minusMinutes(10);
        LocalDateTime fim = LocalDateTime.now().plusMinutes(10);
        LocalDateTime dataEnvio = LocalDateTime.now();

        VotoEventoDTO dto = new VotoEventoDTO(
                pautaId,
                associadoId,
                TipoVotoEnum.SIM,
                dataEnvio,
                "Voto em processamento."
        );

        PautaCacheDTO pautaCacheDTO = new PautaCacheDTO(
                pautaId,
                inicio,
                fim
        );

        Pauta pauta = new Pauta();
        Associado associado = new Associado();
        Voto voto = new Voto();

        when(pautaCacheService.buscar(pautaId))
                .thenReturn(Optional.of(pautaCacheDTO));

        when(pautaRepository.getReferenceById(pautaId))
                .thenReturn(pauta);

        when(associadoRepository.getReferenceById(associadoId))
                .thenReturn(associado);

        votoService.processarVoto(dto);

        verify(pautaCacheService).buscar(pautaId);
        verify(pautaRepository).getReferenceById(pautaId);
        verify(associadoRepository).getReferenceById(associadoId);
        verify(votoRepository).save(any(Voto.class));

        verify(pautaRepository, never()).findById(any());
        verify(votoInvalidoRepository, never()).save(any(VotoInvalido.class));
    }

    @Test
    void deveSalvarVotoInvalidoQuandoAssociadoJaVotou() {
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        LocalDateTime inicio = LocalDateTime.now().minusMinutes(10);
        LocalDateTime fim = LocalDateTime.now().plusMinutes(10);

        VotoEventoDTO dto = new VotoEventoDTO(
                pautaId,
                associadoId,
                TipoVotoEnum.SIM,
                LocalDateTime.now(),
                "Voto em processamento."
        );

        PautaCacheDTO pautaCacheDTO = new PautaCacheDTO(
                pautaId,
                inicio,
                fim
        );

        Pauta pauta = new Pauta();
        Associado associado = new Associado();

        when(pautaCacheService.buscar(pautaId))
                .thenReturn(Optional.of(pautaCacheDTO));

        when(pautaRepository.getReferenceById(pautaId))
                .thenReturn(pauta);

        when(associadoRepository.getReferenceById(associadoId))
                .thenReturn(associado);

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "duplicate key",
                        new RuntimeException("violates constraint uk_voto_associado_pauta")
                );

        doThrow(exception)
                .when(votoRepository)
                .save(any(Voto.class));

        votoService.processarVoto(dto);

        ArgumentCaptor<VotoInvalido> captor = ArgumentCaptor.forClass(VotoInvalido.class);

        verify(votoInvalidoRepository).save(captor.capture());

        assertThat(captor.getValue()).isNotNull();

        verify(votoRepository).save(any(Voto.class));

        assertThat(captor.getValue().getMensagem()).isEqualTo("O associado já votou nesta pauta.");
    }
}