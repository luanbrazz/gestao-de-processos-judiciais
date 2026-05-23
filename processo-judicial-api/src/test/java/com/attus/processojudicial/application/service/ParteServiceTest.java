package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.application.dto.*;
import com.attus.processojudicial.domain.entity.PessoaFisica;
import com.attus.processojudicial.domain.entity.PessoaJuridica;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.enums.TipoParte;
import com.attus.processojudicial.domain.repository.ParteRepository;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import com.attus.processojudicial.infrastructure.client.BrasilApiService;
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
    @Mock private BrasilApiService brasilApiService;

    @InjectMocks private ParteService parteService;

    private Processo processo;
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
    }

    @Test
    @DisplayName("Deve adicionar PessoaFisica sem CEP com sucesso")
    void deveAdicionarPessoaFisicaSemCep() {
        PessoaFisicaRequestDTO dto = new PessoaFisicaRequestDTO();
        dto.setTipo(TipoParte.AUTOR);
        dto.setNome("João da Silva");
        dto.setDocumento("123.456.789-09");
        dto.setTipoPessoa("PESSOA_FISICA");

        PessoaFisica entidade = PessoaFisica.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.AUTOR)
                .nome("João da Silva")
                .documento("123.456.789-09")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(parteRepository.save(any(PessoaFisica.class))).thenReturn(entidade);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, dto);

        assertThat(response).isNotNull().isInstanceOf(PessoaFisicaResponseDTO.class);
        assertThat(response.getNome()).isEqualTo("João da Silva");
        assertThat(response.getTipo()).isEqualTo(TipoParte.AUTOR);
        verifyNoInteractions(viaCepService);
    }

    @Test
    @DisplayName("Deve preencher endereço via ViaCEP ao adicionar PessoaFisica com CEP")
    void devePreencherEnderecoViaCepParaPessoaFisica() {
        PessoaFisicaRequestDTO dto = new PessoaFisicaRequestDTO();
        dto.setTipo(TipoParte.REU);
        dto.setNome("Maria Oliveira");
        dto.setDocumento("987.654.321-00");
        dto.setTipoPessoa("PESSOA_FISICA");
        dto.setCep("12030-145");

        EnderecoDTO endereco = new EnderecoDTO();
        endereco.setCep("12030-145");
        endereco.setLogradouro("Rua das Flores");
        endereco.setBairro("Jardim América");
        endereco.setLocalidade("Taubaté");
        endereco.setUf("SP");

        PessoaFisica entidade = PessoaFisica.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.REU)
                .nome("Maria Oliveira")
                .documento("987.654.321-00")
                .cep("12030-145")
                .logradouro("Rua das Flores")
                .bairro("Jardim América")
                .cidade("Taubaté")
                .uf("SP")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(viaCepService.buscarEndereco("12030-145")).thenReturn(endereco);
        when(parteRepository.save(any(PessoaFisica.class))).thenReturn(entidade);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, dto);

        assertThat(response).isInstanceOf(PessoaFisicaResponseDTO.class);
        assertThat(response.getCidade()).isEqualTo("Taubaté");
        verify(viaCepService).buscarEndereco("12030-145");
    }

    @Test
    @DisplayName("Deve continuar sem endereço quando ViaCEP falhar (fallback gracioso)")
    void deveContinuarSemEnderecoQuandoViaCepFalhar() {
        PessoaFisicaRequestDTO dto = new PessoaFisicaRequestDTO();
        dto.setTipo(TipoParte.AUTOR);
        dto.setNome("João da Silva");
        dto.setDocumento("123.456.789-09");
        dto.setTipoPessoa("PESSOA_FISICA");
        dto.setCep("99999-999");

        PessoaFisica entidade = PessoaFisica.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.AUTOR)
                .nome("João da Silva")
                .documento("123.456.789-09")
                .cep("99999-999")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(viaCepService.buscarEndereco(any())).thenThrow(new RuntimeException("CEP inválido"));
        when(parteRepository.save(any(PessoaFisica.class))).thenReturn(entidade);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, dto);

        assertThat(response).isNotNull();
        assertThat(response.getLogradouro()).isNull();
        verify(parteRepository).save(any());
    }

    @Test
    @DisplayName("Deve adicionar PessoaJuridica com dados preenchidos via BrasilAPI")
    void deveAdicionarPessoaJuridicaComDadosBrasilApi() {
        PessoaJuridicaRequestDTO dto = new PessoaJuridicaRequestDTO();
        dto.setTipo(TipoParte.AUTOR);
        dto.setNome("Empresa XYZ Ltda");
        dto.setDocumento("12.345.678/0001-90");
        dto.setTipoPessoa("PESSOA_JURIDICA");

        CnpjDTO cnpjDTO = new CnpjDTO();
        cnpjDTO.setCnpj("12345678000190");
        cnpjDTO.setRazaoSocial("Empresa XYZ Ltda");
        cnpjDTO.setNaturezaJuridica("206-2 - Sociedade Empresária Limitada");
        cnpjDTO.setDescricaoSituacaoCadastral("ATIVA");
        cnpjDTO.setLogradouro("Av. Paulista");
        cnpjDTO.setBairro("Bela Vista");
        cnpjDTO.setMunicipio("São Paulo");
        cnpjDTO.setUf("SP");
        cnpjDTO.setCep("01310-100");

        PessoaJuridica entidade = PessoaJuridica.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.AUTOR)
                .nome("Empresa XYZ Ltda")
                .documento("12.345.678/0001-90")
                .razaoSocial("Empresa XYZ Ltda")
                .naturezaJuridica("206-2 - Sociedade Empresária Limitada")
                .situacao("ATIVA")
                .cidade("São Paulo")
                .uf("SP")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(brasilApiService.buscarCnpj("12.345.678/0001-90")).thenReturn(cnpjDTO);
        when(parteRepository.save(any(PessoaJuridica.class))).thenReturn(entidade);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, dto);

        assertThat(response).isNotNull().isInstanceOf(PessoaJuridicaResponseDTO.class);
        PessoaJuridicaResponseDTO pjResponse = (PessoaJuridicaResponseDTO) response;
        assertThat(pjResponse.getRazaoSocial()).isEqualTo("Empresa XYZ Ltda");
        assertThat(pjResponse.getSituacao()).isEqualTo("ATIVA");
        verify(brasilApiService).buscarCnpj("12.345.678/0001-90");
        verifyNoInteractions(viaCepService);
    }

    @Test
    @DisplayName("Deve continuar sem dados da empresa quando BrasilAPI falhar (fallback gracioso)")
    void deveContinuarSemDadosCnpjQuandoBrasilApiFalhar() {
        PessoaJuridicaRequestDTO dto = new PessoaJuridicaRequestDTO();
        dto.setTipo(TipoParte.REU);
        dto.setNome("Empresa Fantasma Ltda");
        dto.setDocumento("99.999.999/0001-99");
        dto.setTipoPessoa("PESSOA_JURIDICA");

        PessoaJuridica entidade = PessoaJuridica.builder()
                .id(UUID.randomUUID())
                .processo(processo)
                .tipo(TipoParte.REU)
                .nome("Empresa Fantasma Ltda")
                .documento("99.999.999/0001-99")
                .build();

        when(processoRepository.findById(PROCESSO_ID)).thenReturn(Optional.of(processo));
        when(brasilApiService.buscarCnpj(any())).thenThrow(new RuntimeException("CNPJ não encontrado"));
        when(parteRepository.save(any(PessoaJuridica.class))).thenReturn(entidade);

        ParteResponseDTO response = parteService.adicionar(PROCESSO_ID, dto);

        assertThat(response).isNotNull().isInstanceOf(PessoaJuridicaResponseDTO.class);
        assertThat(response.getLogradouro()).isNull();
        verify(parteRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar RecursoNaoEncontradoException quando processo não existir")
    void deveLancarExcecaoQuandoProcessoNaoEncontrado() {
        UUID idInexistente = UUID.fromString("550e8400-e29b-41d4-a716-446655449999");

        PessoaFisicaRequestDTO dto = new PessoaFisicaRequestDTO();
        dto.setTipo(TipoParte.AUTOR);
        dto.setNome("João da Silva");
        dto.setDocumento("123.456.789-09");
        dto.setTipoPessoa("PESSOA_FISICA");

        when(processoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parteService.adicionar(idInexistente, dto))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining(idInexistente.toString());

        verify(parteRepository, never()).save(any());
    }
}
