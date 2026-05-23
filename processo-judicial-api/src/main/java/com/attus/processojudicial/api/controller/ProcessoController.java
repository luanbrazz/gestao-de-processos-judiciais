package com.attus.processojudicial.api.controller;

import com.attus.processojudicial.application.dto.ProcessoRequestDTO;
import com.attus.processojudicial.application.dto.ProcessoResponseDTO;
import com.attus.processojudicial.application.service.ProcessoServiceI;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/processos")
@RequiredArgsConstructor
@Tag(name = "Processos", description = "Gestão principal de Processos Judiciais")
public class ProcessoController {

    private final ProcessoServiceI processoService;

    @PostMapping
    @Operation(summary = "Criar novo processo judicial", description = "Cria um processo com validação de número único")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Processo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Número do processo já existe", content = @Content)
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProcessoResponseDTO> criar(
            @Valid @RequestBody
            @Schema(description = "Dados do processo a ser criado") ProcessoRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(processoService.criar(dto));
    }

    @GetMapping
    @Operation(summary = "Listar processos", description = "Lista processos com paginação e filtro opcional por status")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    public ResponseEntity<Page<ProcessoResponseDTO>> listar(
            @RequestParam(required = false)
            @Parameter(description = "Filtrar por status do processo") StatusProcesso status,

            @RequestParam(defaultValue = "0")
            @Parameter(description = "Número da página") int page,

            @RequestParam(defaultValue = "10")
            @Parameter(description = "Quantidade de registros por página") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("criadoEm").descending());
        return ResponseEntity.ok(processoService.listar(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar processo por ID", description = "Retorna um processo completo com partes e movimentações")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processo encontrado"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    public ResponseEntity<ProcessoResponseDTO> buscarPorId(
            @PathVariable
            @Parameter(description = "ID do processo (UUID)", example = "550e8400-e29b-41d4-a716-446655440000") UUID id) {

        return ResponseEntity.ok(processoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar processo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processo atualizado"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado")
    })
    public ResponseEntity<ProcessoResponseDTO> atualizar(
            @PathVariable
            @Parameter(example = "550e8400-e29b-41d4-a716-446655440000") UUID id,
            @Valid @RequestBody ProcessoRequestDTO dto) {

        return ResponseEntity.ok(processoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do processo",
            description = "Altera o status do processo (ATIVO, SUSPENSO, ENCERRADO)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Processo não encontrado"),
            @ApiResponse(responseCode = "400", description = "Status inválido")
    })
    public ResponseEntity<ProcessoResponseDTO> atualizarStatus(
            @PathVariable UUID id,
            @RequestParam
            @Parameter(description = "Novo status do processo",
                    example = "ENCERRADO",
                    schema = @Schema(allowableValues = {"ATIVO", "SUSPENSO", "ENCERRADO"}))
            StatusProcesso status) {

        return ResponseEntity.ok(processoService.atualizarStatus(id, status));
    }
}