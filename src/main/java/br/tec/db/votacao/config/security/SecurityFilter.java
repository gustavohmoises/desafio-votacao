package br.tec.db.votacao.config.security;

import br.tec.db.votacao.repository.AdminRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
        String token = recuperarToken(request);

        if (token != null) {
            String login = tokenService.validateToken(token);

            if (login != null) {
                adminRepository.findByLogin(login)
                        .ifPresent(admin -> {
                            var authentication = new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities());

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {
            return null;
        }

        return authorization.substring(7);
    }
}
