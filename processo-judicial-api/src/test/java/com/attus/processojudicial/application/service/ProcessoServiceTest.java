package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.api.exception.RegraDeNegocioException;
import com.attus.processojudicial.application.dto.ProcessoRequestDTO;
import com.attus.processojudicial.application.dto.ProcessoResponseDTO;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProcessoService - Testes Unitários")
class ProcessoServiceTest {

    @Mock private ProcessoRepository processoRepository;
    @Mock private ParteServiceI parteService;
    @Mock private MovimentacaoServiceI movimentacaoService;

    @InjectMocks private ProcessoService processoService;

    private ProcessoRequestDTO requestDTO;
    private Processo processo;

    @BeforeEach
    void setUp() {
        requestDTO = new ProcessoRequestDTO();
        requestDTO.setNumero("0001234-55.2024.8.26.0100");
        requestDTO.setAssunto("Ação de Cobrança");
        requestDTO.setVara("2ª Vara Cível");
        requestDTO.setDataAbertura(LocalDate.of(2024, 1, 15));

        processo = Processo.builder()
                .id(1L)
                .numero(requestDTO.getNumero())
                .assunto(requestDTO.getAssunto())
                .vara(requestDTO.getVara())
                .dataAbertura(requestDTO.getDataAbertura())
                .status(StatusProcesso.ATIVO)
                .criadoEm(LocalDateTime.now())
                .atualizadoEm(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve criar processo com sucesso")
    void deveCriarProcessoComSucesso() {
        when(processoRepository.existsByNumero(requestDTO.getNumero())).thenReturn(false);
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);
        when(parteService.listarPorProcesso(1L)).thenReturn(java.util.List.of());
        when(movimentacaoService.listarPorProcesso(1L)).thenReturn(java.util.List.of());

        ProcessoResponseDTO response = processoService.criar(requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getNumero()).isEqualTo(requestDTO.getNumero());
        assertThat(response.getStatus()).isEqualTo(StatusProcesso.ATIVO);
        verify(processoRepository).save(any(Processo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar processo com número duplicado")
    void deveLancarExcecaoParaNumeroDuplicado() {
        when(processoRepository.existsByNumero(requestDTO.getNumero())).thenReturn(true);

        assertThatThrownBy(() -> processoService.criar(requestDTO))
                .isInstanceOf(RegraDeNegocioException.class)
                .hasMessageContaining(requestDTO.getNumero());

        verify(processoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar processo por ID com sucesso")
    void deveBuscarProcessoPorId() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));
        when(parteService.listarPorProcesso(1L)).thenReturn(java.util.List.of());
        when(movimentacaoService.listarPorProcesso(1L)).thenReturn(java.util.List.of());

        ProcessoResponseDTO response = processoService.buscarPorId(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando processo não encontrado")
    void deveLancarExcecaoQuandoProcessoNaoEncontrado() {
        when(processoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> processoService.buscarPorId(99L))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Deve atualizar status do processo com sucesso")
    void deveAtualizarStatusDoProcesso() {
        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));
        when(processoRepository.save(any(Processo.class))).thenReturn(processo);
        when(parteService.listarPorProcesso(1L)).thenReturn(java.util.List.of());
        when(movimentacaoService.listarPorProcesso(1L)).thenReturn(java.util.List.of());

        ProcessoResponseDTO response = processoService.atualizarStatus(1L, StatusProcesso.ENCERRADO);

        assertThat(response).isNotNull();
        verify(processoRepository).save(any(Processo.class));
    }
}