package com.attus.processojudicial.api.controller;

import com.attus.processojudicial.application.dto.ParteRequestDTO;
import com.attus.processojudicial.application.dto.ParteResponseDTO;
import com.attus.processojudicial.application.service.ParteServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/processos/{processoId}/partes")
@RequiredArgsConstructor
@Tag(name = "Partes", description = "Gestão de Partes do Processo (Autor e Réu)")
public class ParteController {

    private final ParteServiceI parteService;

    @PostMapping
    @Operation(summary = "Adicionar parte ao processo com busca automática de endereço por CEP")
    public ResponseEntity<ParteResponseDTO> adicionar(
            @PathVariable Long processoId,
            @Valid @RequestBody ParteRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parteService.adicionar(processoId, dto));
    }

    @GetMapping
    @Operation(summary = "Listar partes do processo")
    public ResponseEntity<List<ParteResponseDTO>> listar(@PathVariable Long processoId) {
        return ResponseEntity.ok(parteService.listarPorProcesso(processoId));
    }
}