package br.tec.db.votacao.messaging.producer;

import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.KafkaTemplate;

@Component
@RequiredArgsConstructor
public class VotoProducer {
    private final KafkaTemplate<String, VotoEventoDTO> kafkaTemplate;

    public void publicar(VotoEventoDTO voto) {
        kafkaTemplate.send("votos-processar", voto);
    }
}
