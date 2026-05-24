package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.api.exception.RegraDeNegocioException;
import com.attus.processojudicial.application.dto.*;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.validator.DocumentoValidator;
import com.attus.processojudicial.domain.entity.Parte;
import com.attus.processojudicial.domain.entity.PessoaFisica;
import com.attus.processojudicial.domain.entity.PessoaJuridica;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.repository.ParteRepository;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import com.attus.processojudicial.infrastructure.client.BrasilApiService;
import com.attus.processojudicial.infrastructure.client.ViaCepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParteService implements ParteServiceI {

    private final ParteRepository parteRepository;
    private final ProcessoRepository processoRepository;
    private final ViaCepService viaCepService;
    private final BrasilApiService brasilApiService;

    @Override
    @Transactional
    public ParteResponseDTO adicionar(UUID processoId, ParteRequestDTO dto) {
        Processo processo = buscarProcessoOuLancarExcecao(processoId);

        if (processo.getStatus() == StatusProcesso.ENCERRADO) {
            throw new RegraDeNegocioException(
                    "Não é possível adicionar partes a um processo encerrado."
            );
        }

        if (dto instanceof PessoaFisicaRequestDTO pf) {
            return adicionarPessoaFisica(pf, processo);
        } else if (dto instanceof PessoaJuridicaRequestDTO pj) {
            return adicionarPessoaJuridica(pj, processo);
        }

        throw new RegraDeNegocioException("Tipo de pessoa inválido. Use PESSOA_FISICA ou PESSOA_JURIDICA.");
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParteResponseDTO> listarPorProcesso(UUID processoId) {
        return parteRepository.findByProcessoId(processoId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private ParteResponseDTO adicionarPessoaFisica(PessoaFisicaRequestDTO dto, Processo processo) {
        DocumentoValidator.validarCpf(dto.getDocumento());
        validarMaioridade(dto.getDataNascimento());

        PessoaFisica pessoa = PessoaFisica.builder()
                .processo(processo)
                .tipo(dto.getTipo())
                .nome(dto.getNome())
                .documento(dto.getDocumento())
                .dataNascimento(dto.getDataNascimento())
                .cep(dto.getCep())
                .build();

        preencherEnderecoViaCep(pessoa, dto.getCep());
        return toResponseDTO(parteRepository.save(pessoa));
    }

    private void validarMaioridade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            throw new RegraDeNegocioException(
                    "Data de nascimento é obrigatória para Pessoa Física");
        }
        int idade = Period.between(dataNascimento, LocalDate.now()).getYears();
        if (idade < 18) {
            throw new RegraDeNegocioException(
                    "Pessoa Física deve ter 18 anos ou mais para ser parte em um processo judicial. " +
                    "Idade calculada: " + idade + " anos.");
        }
    }

    private void preencherEnderecoViaCep(PessoaFisica pessoa, String cep) {
        if (cep == null || cep.isBlank()) return;
        try {
            EnderecoDTO endereco = viaCepService.buscarEndereco(cep);
            pessoa.setLogradouro(endereco.getLogradouro());
            pessoa.setBairro(endereco.getBairro());
            pessoa.setCidade(endereco.getLocalidade());
            pessoa.setUf(endereco.getUf());
            log.info("Endereço preenchido para CEP: {}", cep);
        } catch (Exception e) {
            log.warn("Não foi possível preencher endereço para CEP {}: {}", cep, e.getMessage());
        }
    }

    private ParteResponseDTO adicionarPessoaJuridica(PessoaJuridicaRequestDTO dto, Processo processo) {
        DocumentoValidator.validarCnpj(dto.getDocumento());

        PessoaJuridica pessoa = PessoaJuridica.builder()
                .processo(processo)
                .tipo(dto.getTipo())
                .nome(dto.getNome())
                .documento(dto.getDocumento())
                .build();

        preencherDadosCnpj(pessoa, dto.getDocumento());
        return toResponseDTO(parteRepository.save(pessoa));
    }

    private void preencherDadosCnpj(PessoaJuridica pessoa, String cnpj) {
        if (cnpj == null || cnpj.isBlank()) return;
        try {
            CnpjDTO dados = brasilApiService.buscarCnpj(cnpj);
            pessoa.setRazaoSocial(dados.getRazaoSocial());
            pessoa.setNaturezaJuridica(dados.getNaturezaJuridica());
            pessoa.setSituacao(dados.getDescricaoSituacaoCadastral());
            pessoa.setCep(dados.getCep());
            pessoa.setLogradouro(dados.getLogradouro());
            pessoa.setBairro(dados.getBairro());
            pessoa.setCidade(dados.getMunicipio());
            pessoa.setUf(dados.getUf());

            if (dados.getCnaeFiscal() != null) {
                pessoa.setCnae(dados.getCnaeFiscal() + " - " + dados.getCnaeFiscalDescricao());
            }

            log.info("Dados da empresa preenchidos para CNPJ: {}", cnpj);
        } catch (Exception e) {
            log.warn("Não foi possível preencher dados para CNPJ {}: {}", cnpj, e.getMessage());
        }
    }

    private Processo buscarProcessoOuLancarExcecao(UUID processoId) {
        return processoRepository.findById(processoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Processo não encontrado: " + processoId));
    }

    private ParteResponseDTO toResponseDTO(Parte parte) {
        if (parte instanceof PessoaFisica pf) {
            PessoaFisicaResponseDTO dto = new PessoaFisicaResponseDTO();
            preencherCamposBase(dto, pf);
            dto.setTipoPessoa("PESSOA_FISICA");
            dto.setDocumento(pf.getDocumento());
            dto.setCep(pf.getCep());
            dto.setLogradouro(pf.getLogradouro());
            dto.setBairro(pf.getBairro());
            dto.setCidade(pf.getCidade());
            dto.setUf(pf.getUf());
            dto.setDataNascimento(pf.getDataNascimento());
            return dto;
        } else if (parte instanceof PessoaJuridica pj) {
            PessoaJuridicaResponseDTO dto = new PessoaJuridicaResponseDTO();
            preencherCamposBase(dto, pj);
            dto.setTipoPessoa("PESSOA_JURIDICA");
            dto.setDocumento(pj.getDocumento());
            dto.setCep(pj.getCep());
            dto.setLogradouro(pj.getLogradouro());
            dto.setBairro(pj.getBairro());
            dto.setCidade(pj.getCidade());
            dto.setUf(pj.getUf());
            dto.setRazaoSocial(pj.getRazaoSocial());
            dto.setCnae(pj.getCnae());
            dto.setNaturezaJuridica(pj.getNaturezaJuridica());
            dto.setSituacao(pj.getSituacao());
            return dto;
        }

        throw new IllegalStateException("Tipo de entidade Parte desconhecido: " + parte.getClass().getName());
    }

    private void preencherCamposBase(ParteResponseDTO dto, Parte parte) {
        dto.setId(parte.getId());
        dto.setTipo(parte.getTipo());
        dto.setNome(parte.getNome());
    }
}
