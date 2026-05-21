package com.attus.processojudicial.domain.repository;

import com.attus.processojudicial.domain.entity.Parte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParteRepository extends JpaRepository<Parte, Long> {
    List<Parte> findByProcessoId(Long processoId);
}