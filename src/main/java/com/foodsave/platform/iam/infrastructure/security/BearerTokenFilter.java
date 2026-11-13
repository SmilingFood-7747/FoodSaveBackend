package com.foodsave.platform.iam.infrastructure.security;

import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.foodsave.platform.iam.application.TokenService;

@Component
public class BearerTokenFilter extends OncePerRequestFilter {
    private final TokenService tokens;
    public BearerTokenFilter(TokenService tokens) { this.tokens = tokens; }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                var claims = tokens.verify(authorization.substring(7));
                var principal = new FoodSavePrincipal(claims.accountId(), claims.role());
                var authentication = new UsernamePasswordAuthenticationToken(principal, null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + claims.role())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (IllegalArgumentException exception) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired access token");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    public record FoodSavePrincipal(Long accountId, String role) {}
}
