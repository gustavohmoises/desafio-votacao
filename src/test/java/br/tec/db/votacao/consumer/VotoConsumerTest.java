package br.tec.db.votacao.consumer;

import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.enums.TipoVotoEnum;
import br.tec.db.votacao.service.VotoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VotoConsumerTest {
    @Mock
    private VotoService votoService;

    @InjectMocks
    private VotoConsumer votoConsumer;

    @Test
    void deveProcessarVotoRecebido() {
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        VotoEventoDTO dto = new VotoEventoDTO(
                pautaId,
                associadoId,
                TipoVotoEnum.SIM,
                LocalDateTime.now(),
                "Voto em processamento."
        );

        votoConsumer.consumir(dto);

        verify(votoService).processarVoto(dto);
    }
}
