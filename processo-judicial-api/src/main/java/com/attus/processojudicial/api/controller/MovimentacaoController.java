package com.attus.processojudicial.api.controller;

import com.attus.processojudicial.application.dto.MovimentacaoRequestDTO;
import com.attus.processojudicial.application.dto.MovimentacaoResponseDTO;
import com.attus.processojudicial.application.service.MovimentacaoServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processos/{processoId}/movimentacoes")
@RequiredArgsConstructor
@Tag(name = "Movimentações", description = "Gestão de Movimentações/Andamentos do Processo")
public class MovimentacaoController {

    private final MovimentacaoServiceI movimentacaoService;

    @PostMapping
    @Operation(summary = "Adicionar movimentação ao processo")
    public ResponseEntity<MovimentacaoResponseDTO> adicionar(
            @PathVariable Long processoId,
            @Valid @RequestBody MovimentacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimentacaoService.adicionar(processoId, dto));
    }

    @GetMapping
    @Operation(summary = "Listar movimentações do processo")
    public ResponseEntity<List<MovimentacaoResponseDTO>> listar(@PathVariable Long processoId) {
        return ResponseEntity.ok(movimentacaoService.listarPorProcesso(processoId));
    }
}