package com.attus.processojudicial.api.controller;

import com.attus.processojudicial.application.dto.ProcessoRequestDTO;
import com.attus.processojudicial.application.dto.ProcessoResponseDTO;
import com.attus.processojudicial.application.service.ProcessoServiceI;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/processos")
@RequiredArgsConstructor
@Tag(name = "Processos", description = "Gestão de Processos Judiciais")
public class ProcessoController {

    private final ProcessoServiceI processoService;

    @PostMapping
    @Operation(summary = "Criar novo processo")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProcessoResponseDTO> criar(@Valid @RequestBody ProcessoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(processoService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar processos com paginação e filtro por status")
    public ResponseEntity<Page<ProcessoResponseDTO>> listar(
            @RequestParam(required = false) StatusProcesso status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
        return ResponseEntity.ok(processoService.listar(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar processo por ID")
    public ResponseEntity<ProcessoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar processo")
    public ResponseEntity<ProcessoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProcessoRequestDTO dto) {
        return ResponseEntity.ok(processoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do processo")
    public ResponseEntity<ProcessoResponseDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusProcesso status) {
        return ResponseEntity.ok(processoService.atualizarStatus(id, status));
    }
}