package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.MovimentacaoRequestDTO;
import com.attus.processojudicial.application.dto.MovimentacaoResponseDTO;
import java.util.List;

public interface MovimentacaoServiceI {
    MovimentacaoResponseDTO adicionar(Long processoId, MovimentacaoRequestDTO dto);
    List<MovimentacaoResponseDTO> listarPorProcesso(Long processoId);
}