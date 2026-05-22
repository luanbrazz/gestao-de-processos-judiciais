package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.ErroDeIntegracaoException;
import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.application.dto.EnderecoDTO;
import com.attus.processojudicial.application.dto.ParteRequestDTO;
import com.attus.processojudicial.application.dto.ParteResponseDTO;
import com.attus.processojudicial.domain.entity.Parte;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.repository.ParteRepository;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import com.attus.processojudicial.infrastructure.client.ViaCepClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParteService implements ParteServiceI {

    private final ParteRepository parteRepository;
    private final ProcessoRepository processoRepository;
    private final ViaCepClient viaCepClient;

    @Override
    @Transactional
    public ParteResponseDTO adicionar(Long processoId, ParteRequestDTO dto) {
        Processo processo = buscarProcessoOuLancarExcecao(processoId);
        Parte parte = montarParte(dto, processo);
        preencherEnderecoViaCep(parte, dto.getCep());
        return toResponseDTO(parteRepository.save(parte));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParteResponseDTO> listarPorProcesso(Long processoId) {
        return parteRepository.findByProcessoId(processoId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Processo buscarProcessoOuLancarExcecao(Long processoId) {
        return processoRepository.findById(processoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Processo não encontrado: " + processoId));
    }

    private Parte montarParte(ParteRequestDTO dto, Processo processo) {
        return Parte.builder()
                .processo(processo)
                .tipo(dto.getTipo())
                .nome(dto.getNome())
                .documento(dto.getDocumento())
                .cep(dto.getCep())
                .build();
    }

    private void preencherEnderecoViaCep(Parte parte, String cep) {
        if (cep == null || cep.isBlank()) return;
        try {
            log.info("Buscando endereço para CEP: {}", cep);
            EnderecoDTO endereco = viaCepClient.buscarEnderecoPorCep(cep.replaceAll("-", ""));
            parte.setLogradouro(endereco.getLogradouro());
            parte.setBairro(endereco.getBairro());
            parte.setCidade(endereco.getLocalidade());
            parte.setUf(endereco.getUf());
            log.info("Endereço encontrado: {} - {}", endereco.getLogradouro(), endereco.getLocalidade());
        } catch (ErroDeIntegracaoException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Falha ao buscar CEP {} - continuando sem endereço: {}", cep, e.getMessage());
        }
    }

    private ParteResponseDTO toResponseDTO(Parte p) {
        ParteResponseDTO dto = new ParteResponseDTO();
        dto.setId(p.getId());
        dto.setTipo(p.getTipo());
        dto.setNome(p.getNome());
        dto.setDocumento(p.getDocumento());
        dto.setCep(p.getCep());
        dto.setLogradouro(p.getLogradouro());
        dto.setBairro(p.getBairro());
        dto.setCidade(p.getCidade());
        dto.setUf(p.getUf());
        return dto;
    }
}