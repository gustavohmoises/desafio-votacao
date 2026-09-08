package br.tec.db.votacao.cache;

import br.tec.db.votacao.entity.Pauta;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PautaCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String PREFIXO = "pauta:";

    public void salvar(Pauta pauta) {
        PautaCache cache = new PautaCache(
                pauta.getId(),
                pauta.getInicioVotacao(),
                pauta.getFimVotacao()
        );

        Duration ttl = Duration.between(
                pauta.getInicioVotacao(),
                pauta.getFimVotacao()
        );

        redisTemplate.opsForValue()
                .set(PREFIXO + pauta.getId(), cache, ttl);
    }

    public Optional<PautaCache> buscar(UUID pautaId) {
        Object valor = redisTemplate.opsForValue().get(PREFIXO + pautaId);

        return Optional.ofNullable(valor)
                .map(v -> objectMapper.convertValue(v, PautaCache.class));
    }
}
