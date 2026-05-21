package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.*;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProcessoServiceI {
    ProcessoResponseDTO criar(ProcessoRequestDTO dto);
    Page<ProcessoResponseDTO> listar(StatusProcesso status, Pageable pageable);
    ProcessoResponseDTO buscarPorId(Long id);
    ProcessoResponseDTO atualizar(Long id, ProcessoRequestDTO dto);
    ProcessoResponseDTO atualizarStatus(Long id, StatusProcesso status);
    ParteResponseDTO adicionarParte(Long processoId, ParteRequestDTO dto);
    MovimentacaoResponseDTO adicionarMovimentacao(Long processoId, MovimentacaoRequestDTO dto);
}