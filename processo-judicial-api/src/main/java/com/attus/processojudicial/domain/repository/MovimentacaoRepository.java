package com.attus.processojudicial.domain.repository;

import com.attus.processojudicial.domain.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, UUID> {
    List<Movimentacao> findByProcessoIdOrderByDataMovimentacaoDesc(UUID processoId);
}