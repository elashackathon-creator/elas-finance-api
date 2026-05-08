package com.elafinance.finopt.config;

import com.elafinance.finopt.usecase.ServicoAutenticacao;
import com.elafinance.finopt.usecase.ServicoJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroJWT extends OncePerRequestFilter {

    private final ServicoJWT servicoJWT;
    private final ServicoAutenticacao servicoAutenticacao;

    public FiltroJWT(ServicoJWT servicoJWT, ServicoAutenticacao servicoAutenticacao) {
        this.servicoJWT = servicoJWT;
        this.servicoAutenticacao = servicoAutenticacao;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (servicoJWT.valido(token)) {
                String email = servicoJWT.extrairEmail(token);
                UserDetails usuario = servicoAutenticacao.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        chain.doFilter(request, response);
    }
}
