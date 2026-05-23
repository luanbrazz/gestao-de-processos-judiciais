package com.attus.processojudicial.domain.repository;

import com.attus.processojudicial.domain.entity.Parte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParteRepository extends JpaRepository<Parte, UUID> {
    List<Parte> findByProcessoId(UUID processoId);
}