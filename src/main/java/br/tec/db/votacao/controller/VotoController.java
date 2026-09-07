package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.service.VotoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/votos")
public class VotoController {
    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @GetMapping
    public ResponseEntity<List<ListarVotoDTO>> getAll() {
        return ResponseEntity.ok(votoService.getAll());
    }

    @PostMapping
    public ResponseEntity<ListarVotoDTO> create(@RequestBody @Valid CriarVotoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body((votoService.votar(dto)));
    }
}
