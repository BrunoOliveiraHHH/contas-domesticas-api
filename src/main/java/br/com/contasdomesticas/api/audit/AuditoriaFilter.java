package br.com.contasdomesticas.api.audit;

import br.com.contasdomesticas.api.domain.Auditoria;
import br.com.contasdomesticas.api.repository.AuditoriaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

/**
 * Registra na tabela de auditoria cada requisicao HTTP tratada pela API:
 * usuario, metodo, endpoint, status, IP e data/hora.
 */
@Component
@Order(1)
public class AuditoriaFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(AuditoriaFilter.class);
    private static final Set<String> PREFIXOS_IGNORADOS =
            Set.of("/actuator", "/swagger-ui", "/v3/api-docs", "/error", "/favicon.ico");

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaFilter(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return PREFIXOS_IGNORADOS.stream().anyMatch(uri::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } finally {
            registrar(request, response);
        }
    }

    private void registrar(HttpServletRequest request, HttpServletResponse response) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setUsuario(usuarioAtual());
            auditoria.setMetodoHttp(request.getMethod());
            auditoria.setEndpoint(request.getRequestURI());
            auditoria.setStatusResposta(response.getStatus());
            auditoria.setEnderecoIp(enderecoIp(request));
            auditoria.setDataHora(Instant.now());
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // Auditoria nunca deve quebrar a requisicao.
            log.warn("Falha ao registrar auditoria: {}", e.getMessage());
        }
    }

    private String usuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return "anonimo";
        }
        return authentication.getName();
    }

    private String enderecoIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
