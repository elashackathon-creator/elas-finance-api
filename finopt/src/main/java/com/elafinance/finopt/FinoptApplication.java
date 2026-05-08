package com.elafinance.finopt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinoptApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinoptApplication.class, args);

        String boasVindas = """
               **********************************************
                *      ELAFINANCE API INICIALIZADA         *
                *      Foco: Autonomia Financeira          *
                *      Tecnologia: Java 21 & Spring 3.5    *
               **********************************************
               """;
        System.out.println(boasVindas);
    }
}