package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Voto.CriarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoDTO;
import br.tec.db.votacao.dto.Voto.ListarVotoInvalidoDTO;
import br.tec.db.votacao.dto.Voto.VotoEventoDTO;
import br.tec.db.votacao.entity.VotoInvalido;
import br.tec.db.votacao.service.VotoService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/votos")
@SecurityRequirement(name = "bearerAuth")
public class VotoController {
    private final VotoService votoService;

    public VotoController(VotoService votoService) {
        this.votoService = votoService;
    }

    @GetMapping
    public ResponseEntity<List<ListarVotoDTO>> getAll() {
        return ResponseEntity.ok(votoService.getAll());
    }

    @GetMapping("/invalidos")
    public ResponseEntity<List<ListarVotoInvalidoDTO>> getAllInvalidos() {
        return ResponseEntity.ok(votoService.getAllInvalidos());
    }

    @PostMapping
    public ResponseEntity<VotoEventoDTO> create(@RequestBody @Valid CriarVotoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body((votoService.votar(dto)));
    }
}
