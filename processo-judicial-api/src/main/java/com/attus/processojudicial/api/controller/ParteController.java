package com.attus.processojudicial.api.controller;

import com.attus.processojudicial.application.dto.ParteRequestDTO;
import com.attus.processojudicial.application.dto.ParteResponseDTO;
import com.attus.processojudicial.application.service.ParteServiceI;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/processos/{processoId}/partes")
@RequiredArgsConstructor
@Tag(name = "Partes", description = "Gestão de Partes do Processo (Autor e Réu)")
public class ParteController {

    private final ParteServiceI parteService;

    @PostMapping
    @Operation(summary = "Adicionar parte ao processo",
            description = "Adiciona Autor ou Réu com preenchimento automático de endereço via ViaCEP")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Parte adicionada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ParteResponseDTO> adicionar(
            @PathVariable
            @Parameter(description = "ID do processo", example = "550e8400-e29b-41d4-a716-446655440000") UUID processoId,

            @Valid @RequestBody
            @Schema(description = "Dados da parte") ParteRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(parteService.adicionar(processoId, dto));
    }

    @GetMapping
    @Operation(summary = "Listar partes do processo")
    public ResponseEntity<List<ParteResponseDTO>> listar(
            @PathVariable
            @Parameter(example = "550e8400-e29b-41d4-a716-446655440000") UUID processoId) {

        return ResponseEntity.ok(parteService.listarPorProcesso(processoId));
    }
}