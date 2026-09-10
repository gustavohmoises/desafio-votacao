package br.tec.db.votacao.service;

import br.tec.db.votacao.config.security.TokenService;
import br.tec.db.votacao.dto.Admin.ListarAdminDTO;
import br.tec.db.votacao.dto.Admin.LogarDTO;
import br.tec.db.votacao.dto.Admin.RegistrarDTO;
import br.tec.db.votacao.dto.Admin.TokenDTO;
import br.tec.db.votacao.entity.Admin;
import br.tec.db.votacao.exception.ConflictException;
import br.tec.db.votacao.mapper.AdminMapper;
import br.tec.db.votacao.mapper.AssociadoMapper;
import br.tec.db.votacao.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public ListarAdminDTO registrar(RegistrarDTO dto) {
        if (adminRepository.existsByLogin(dto.login())) {
            throw new ConflictException("Já existe um administrador com este login.");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());

        Admin admin = new Admin(dto.login(), senhaCriptografada);

        return AdminMapper.toDto(adminRepository.save(admin));
    }

    public TokenDTO logar(LogarDTO dto) {
        var authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        dto.login(),
                        dto.senha()
                );

        var authentication = authenticationManager.authenticate(authenticationToken);

        var admin = (Admin) authentication.getPrincipal();

        String token = tokenService.generateToken(admin);

        return new TokenDTO(token);
    }

    public List<ListarAdminDTO> listar() {
        return adminRepository.findAll()
                .stream()
                .map(AdminMapper::toDto)
                .toList();
    }
}
