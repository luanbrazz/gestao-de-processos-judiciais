package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.ProcessoRequestDTO;
import com.attus.processojudicial.application.dto.ProcessoResponseDTO;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProcessoServiceI {
    ProcessoResponseDTO criar(ProcessoRequestDTO dto);
    Page<ProcessoResponseDTO> listar(StatusProcesso status, Pageable pageable);
    ProcessoResponseDTO buscarPorId(UUID id);
    ProcessoResponseDTO atualizar(UUID id, ProcessoRequestDTO dto);
    ProcessoResponseDTO atualizarStatus(UUID id, StatusProcesso status);
}