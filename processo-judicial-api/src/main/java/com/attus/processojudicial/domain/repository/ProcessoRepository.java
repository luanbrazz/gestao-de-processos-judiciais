package com.attus.processojudicial.domain.repository;

import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessoRepository extends JpaRepository<Processo, Long> {
    Page<Processo> findByStatus(StatusProcesso status, Pageable pageable);
    boolean existsByNumero(String numero);
}
