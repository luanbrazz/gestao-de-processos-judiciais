package com.attus.processojudicial.api.controller;

import com.attus.processojudicial.application.dto.*;
import com.attus.processojudicial.application.service.ProcessoService;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/processos")
@RequiredArgsConstructor
@Tag(name = "Processos", description = "Gestão de Processos Judiciais")
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    @Operation(summary = "Criar novo processo")
    public ResponseEntity<ProcessoResponseDTO> criar(@Valid @RequestBody ProcessoRequestDTO dto) {
        return ResponseEntity.status(201).body(processoService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar processos com paginação e filtro por status")
    public ResponseEntity<Page<ProcessoResponseDTO>> listar(
            @RequestParam(required = false) StatusProcesso status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(processoService.listar(status, PageRequest.of(page, size, Sort.by("criadoEm").descending())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar processo por ID")
    public ResponseEntity<ProcessoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar processo")
    public ResponseEntity<ProcessoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProcessoRequestDTO dto) {
        return ResponseEntity.ok(processoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do processo")
    public ResponseEntity<ProcessoResponseDTO> atualizarStatus(@PathVariable Long id, @RequestParam StatusProcesso status) {
        return ResponseEntity.ok(processoService.atualizarStatus(id, status));
    }

    @PostMapping("/{id}/partes")
    @Operation(summary = "Adicionar parte ao processo (busca endereço pelo CEP automaticamente)")
    public ResponseEntity<ParteResponseDTO> adicionarParte(@PathVariable Long id, @Valid @RequestBody ParteRequestDTO dto) {
        return ResponseEntity.status(201).body(processoService.adicionarParte(id, dto));
    }

    @PostMapping("/{id}/movimentacoes")
    @Operation(summary = "Adicionar movimentação ao processo")
    public ResponseEntity<MovimentacaoResponseDTO> adicionarMovimentacao(@PathVariable Long id, @Valid @RequestBody MovimentacaoRequestDTO dto) {
        return ResponseEntity.status(201).body(processoService.adicionarMovimentacao(id, dto));
    }
}