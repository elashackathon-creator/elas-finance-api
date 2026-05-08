package com.elafinance.finopt.usecase;

import com.elafinance.finopt.infraestrutura.UsuarioRepositorio;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicoAutenticacao implements UserDetailsService {

    private final UsuarioRepositorio repositorio;

    public ServicoAutenticacao(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return repositorio.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuária não encontrada: " + email));
    }
}
