package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Associado.CriarAssociadoDTO;
import br.tec.db.votacao.dto.Associado.ListarAssociadoDTO;
import br.tec.db.votacao.service.AssociadoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/associados")
public class AssociadoController {
    private final AssociadoService associadoService;

    public AssociadoController(AssociadoService associadoService) {
        this.associadoService = associadoService;
    }

    @PostMapping
    public ResponseEntity<ListarAssociadoDTO> create(@RequestBody @Valid CriarAssociadoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(associadoService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ListarAssociadoDTO>> getAll() {
        return ResponseEntity.ok(associadoService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListarAssociadoDTO> getById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(associadoService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        associadoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
