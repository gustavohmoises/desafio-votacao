package br.tec.db.votacao.consumer;

import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.service.VotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VotoConsumer {
    private final VotoService votoService;

    @KafkaListener(topics = "votos-processar", groupId = "voto-consumer")
    public void consumir(VotoEventoDTO dto) {
        votoService.processarVoto(dto);
    }
}
