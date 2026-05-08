package com.elafinance.finopt.controller;

import com.elafinance.finopt.domain.Usuario;
import com.elafinance.finopt.infraestrutura.UsuarioRepositorio;
import com.elafinance.finopt.usecase.ServicoJWT;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ControleAutenticacao {

    private final AuthenticationManager authManager;
    private final ServicoJWT servicoJWT;
    private final UsuarioRepositorio repositorio;
    private final PasswordEncoder encoder;

    public ControleAutenticacao(AuthenticationManager authManager, ServicoJWT servicoJWT,
                                 UsuarioRepositorio repositorio, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.servicoJWT = servicoJWT;
        this.repositorio = repositorio;
        this.encoder = encoder;
    }

    @PostMapping("/login")
    public RespostaToken login(@RequestBody @Valid DadosLogin dados) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(dados.email(), dados.senha()));
        String token = servicoJWT.gerar(dados.email());
        return new RespostaToken(token);
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, String> registrar(@RequestBody @Valid DadosRegistro dados) {
        if (repositorio.findByEmail(dados.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado.");
        }
        repositorio.save(new Usuario(dados.nome(), dados.email(), encoder.encode(dados.senha())));
        return Map.of("mensagem", "Cadastro realizado com sucesso.");
    }
}
