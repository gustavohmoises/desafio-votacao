package br.tec.db.votacao.controller;

import br.tec.db.votacao.dto.Admin.ListarAdminDTO;
import br.tec.db.votacao.dto.Admin.LogarDTO;
import br.tec.db.votacao.dto.Admin.RegistrarDTO;
import br.tec.db.votacao.dto.Admin.TokenDTO;
import br.tec.db.votacao.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/logar")
    public ResponseEntity<TokenDTO> logar(@RequestBody @Valid LogarDTO dto) {
        return ResponseEntity.ok(adminService.logar(dto));
    }

    @PostMapping("/registrar")
    public ResponseEntity<ListarAdminDTO> registrar(@RequestBody @Valid RegistrarDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ListarAdminDTO>> listar() {
        return ResponseEntity.ok(adminService.listar());
    }
}
