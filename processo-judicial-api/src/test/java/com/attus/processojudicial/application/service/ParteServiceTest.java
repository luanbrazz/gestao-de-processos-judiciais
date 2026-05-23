package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.application.dto.ParteRequestDTO;
import com.attus.processojudicial.application.dto.ParteResponseDTO;
import com.attus.processojudicial.domain.entity.Parte;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.enums.TipoParte;
import com.attus.processojudicial.domain.repository.ParteRepository;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import com.attus.processojudicial.infrastructure.client.ViaCepService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParteService - Testes Unitários")
class ParteServiceTest {

    @Mock private ParteRepository parteRepository;
    @Mock private ProcessoRepository processoRepository;
    @Mock private ViaCepService viaCepService;

    @InjectMocks private ParteService parteService;

    private Processo processo;
    private ParteRequestDTO requestDTO;
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

        requestDTO = new ParteRequestDTO();
        requestDTO.setTipo(TipoParte.AUTOR);
        requestDTO.setNome("João da Silva");
        requestDTO.setDocumento("123.456.789-00");
    }

    @Test
    @DisplayName("Deve adicionar parte sem CEP com sucesso")
    void deveAdicionarParteSemCep() {
        Parte parte = Parte.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.AUTOR)
                .nome("João da Silva")
                .documento("123.456.789-00")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(parteRepository.save(any(Parte.class))).thenReturn(parte);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getNome()).isEqualTo("João da Silva");
        assertThat(response.getTipo()).isEqualTo(TipoParte.AUTOR);
        verifyNoInteractions(viaCepService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando processo não encontrado ao adicionar parte")
    void deveLancarExcecaoQuandoProcessoNaoEncontrado() {
        UUID idInexistente = UUID.fromString("550e8400-e29b-41d4-a716-446655449999");

        when(processoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parteService.adicionar(idInexistente, requestDTO))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining(idInexistente.toString());

        verify(parteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve continuar sem endereço quando ViaCEP falhar")
    void deveContinuarSemEnderecoQuandoViaCepFalhar() {
        requestDTO.setCep("99999-999");

        Parte parte = Parte.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.AUTOR)
                .nome("João da Silva")
                .documento("123.456.789-00")
                .cep("99999-999")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(viaCepService.buscarEndereco(any())).thenThrow(new RuntimeException("CEP inválido"));
        when(parteRepository.save(any(Parte.class))).thenReturn(parte);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, requestDTO);

        assertThat(response).isNotNull();
        assertThat(response.getLogradouro()).isNull();
        verify(parteRepository).save(any());
    }
}