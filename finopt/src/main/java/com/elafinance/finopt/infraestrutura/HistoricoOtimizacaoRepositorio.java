package com.elafinance.finopt.infraestrutura;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoOtimizacaoRepositorio extends JpaRepository<HistoricoOtimizacao, Long> {
}
