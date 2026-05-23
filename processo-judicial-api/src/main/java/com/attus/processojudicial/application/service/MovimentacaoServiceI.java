package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.MovimentacaoRequestDTO;
import com.attus.processojudicial.application.dto.MovimentacaoResponseDTO;
import java.util.List;
import java.util.UUID;

public interface MovimentacaoServiceI {
    MovimentacaoResponseDTO adicionar(UUID processoId, MovimentacaoRequestDTO dto);
    List<MovimentacaoResponseDTO> listarPorProcesso(UUID processoId);
}