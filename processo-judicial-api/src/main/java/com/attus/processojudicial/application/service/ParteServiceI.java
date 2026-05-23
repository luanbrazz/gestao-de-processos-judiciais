package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.ParteRequestDTO;
import com.attus.processojudicial.application.dto.ParteResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ParteServiceI {
    ParteResponseDTO adicionar(UUID processoId, ParteRequestDTO dto);

    List<ParteResponseDTO> listarPorProcesso(UUID processoId);
}