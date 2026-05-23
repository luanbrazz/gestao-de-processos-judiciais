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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MovimentacaoService - Testes Unitários")
class MovimentacaoServiceTest {

    @Mock
    private MovimentacaoRepository movimentacaoRepository;
    @Mock
    private ProcessoRepository processoRepository;

    @InjectMocks
    private MovimentacaoService movimentacaoService;

    private Processo processo;
    private MovimentacaoRequestDTO requestDTO;
    private final UUID PROCESSO_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");

    @BeforeEach
    void setUp() {
        processo = Processo.builder()
                .id(PROCESSO_ID)
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
                .id(UUID.randomUUID())
                .processo(processo)
                .descricao("Petição inicial protocolada")
                .dataMovimentacao(LocalDateTime.now())
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(movimentacaoRepository.save(any(Movimentacao.class))).thenReturn(movimentacao);

        MovimentacaoResponseDTO response = movimentacaoService.adicionar(PROCESSO_ID, requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getDescricao()).isEqualTo("Petição inicial protocolada");
        verify(movimentacaoRepository).save(any(Movimentacao.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando processo não encontrado ao adicionar movimentação")
    void deveLancarExcecaoQuandoProcessoNaoEncontrado() {
        UUID idInexistente = UUID.fromString("550e8400-e29b-41d4-a716-446655449999");

        when(processoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimentacaoService.adicionar(idInexistente, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining(idInexistente.toString());

        verify(movimentacaoRepository, never()).save(any());
    }
}