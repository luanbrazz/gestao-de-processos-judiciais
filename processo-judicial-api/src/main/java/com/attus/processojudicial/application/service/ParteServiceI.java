package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.ParteRequestDTO;
import com.attus.processojudicial.application.dto.ParteResponseDTO;
import java.util.List;

public interface ParteServiceI {
    ParteResponseDTO adicionar(Long processoId, ParteRequestDTO dto);
    List<ParteResponseDTO> listarPorProcesso(Long processoId);
}