package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Pauta.AbrirVotacaoDTO;
import br.tec.db.votacao.dto.Pauta.CriarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ListarPautaDTO;
import br.tec.db.votacao.dto.Pauta.ResultadoVotacaoDTO;
import br.tec.db.votacao.service.PautaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/pautas")
@SecurityRequirement(name = "bearerAuth")
public class PautaController {
    private final PautaService pautaService;

    public PautaController(PautaService pautaService) {
        this.pautaService = pautaService;
    }

    @PostMapping
    public ResponseEntity<ListarPautaDTO> create(@RequestBody @Valid CriarPautaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pautaService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ListarPautaDTO>> getAll() {
        return ResponseEntity.ok(pautaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListarPautaDTO> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(pautaService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        pautaService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/abrir-votacao")
    public ResponseEntity<ListarPautaDTO> abrirVotacao(@PathVariable("id") UUID pautaId, @RequestBody(required = false) @Valid AbrirVotacaoDTO dto) {
        return ResponseEntity.ok(pautaService.abrirVotacao(pautaId, dto));
    }

    @GetMapping("/{id}/resultado-votacao")
    public ResponseEntity<ResultadoVotacaoDTO> resultadoVotacao(@PathVariable("id") UUID pautaId) {
        return ResponseEntity.ok(pautaService.resultadoVotacao(pautaId));
    }
}
