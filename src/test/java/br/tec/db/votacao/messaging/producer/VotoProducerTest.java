package br.tec.db.votacao.messaging.producer;

import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.enums.TipoVotoEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class VotoProducerTest {
    @Mock
    private KafkaTemplate<String, VotoEventoDTO> kafkaTemplate;

    @InjectMocks
    private VotoProducer votoProducer;

    @Test
    void devePublicarVotoNoTopicoCorreto() {
        UUID pautaId = UUID.randomUUID();
        UUID associadoId = UUID.randomUUID();

        VotoEventoDTO dto = new VotoEventoDTO(
                pautaId,
                associadoId,
                TipoVotoEnum.SIM,
                LocalDateTime.now(),
                "Voto em processamento."
        );

        votoProducer.publicar(dto);

        verify(kafkaTemplate).send("votos-processar", dto);
    }
}