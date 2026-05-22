package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.application.dto.MovimentacaoRequestDTO;
import com.attus.processojudicial.application.dto.MovimentacaoResponseDTO;
import com.attus.processojudicial.domain.entity.Movimentacao;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.repository.MovimentacaoRepository;
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
@DisplayName("MovimentacaoService - Testes Unitários")
class MovimentacaoServiceTest {

    @Mock private MovimentacaoRepository movimentacaoRepository;
    @Mock private ProcessoRepository processoRepository;

    @InjectMocks private MovimentacaoService movimentacaoService;

    private Processo processo;
    private MovimentacaoRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        processo = Processo.builder()
                .id(1L)
                .numero("0001234-55.2024.8.26.0100")
                .assunto("Ação de Cobrança")
                .vara("2ª Vara Cível")
                .dataAbertura(LocalDate.of(2024, 1, 15))
                .status(StatusProcesso.ATIVO)
                .build();

        requestDTO = new MovimentacaoRequestDTO();
        requestDTO.setDescricao("Petição inicial protocolada");
    }

    @Test
    @DisplayName("Deve adicionar movimentação com sucesso")
    void deveAdicionarMovimentacaoComSucesso() {
        Movimentacao movimentacao = Movimentacao.builder()
                .id(1L)
                .processo(processo)
                .descricao("Petição inicial protocolada")
                .dataMovimentacao(LocalDateTime.now())
                .build();

        when(processoRepository.findById(1L)).thenReturn(Optional.of(processo));
        when(movimentacaoRepository.save(any(Movimentacao.class))).thenReturn(movimentacao);

        MovimentacaoResponseDTO response = movimentacaoService.adicionar(1L, requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getDescricao()).isEqualTo("Petição inicial protocolada");
        verify(movimentacaoRepository).save(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando processo não encontrado ao adicionar movimentação")
    void deveLancarExcecaoQuandoProcessoNaoEncontrado() {
        when(processoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimentacaoService.adicionar(99L, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");

        verify(movimentacaoRepository, never()).save(any());
    }
}