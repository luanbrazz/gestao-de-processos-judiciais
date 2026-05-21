package com.attus.processojudicial.domain.repository;

import com.attus.processojudicial.domain.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    List<Movimentacao> findByProcessoIdOrderByDataMovimentacaoDesc(Long processoId);
}