package com.attus.processojudicial.application.service;

import com.attus.processojudicial.application.dto.*;
import com.attus.processojudicial.domain.entity.*;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.repository.*;
import com.attus.processojudicial.infrastructure.client.ViaCepClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessoService {

    private final ProcessoRepository processoRepository;
    private final ParteRepository parteRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ViaCepClient viaCepClient;

    @Transactional
    public ProcessoResponseDTO criar(ProcessoRequestDTO dto) {
        log.info("Criando processo com número: {}", dto.getNumero());
        if (processoRepository.existsByNumero(dto.getNumero())) {
            throw new IllegalArgumentException("Já existe um processo com o número: " + dto.getNumero());
        }
        Processo processo = Processo.builder()
                .numero(dto.getNumero())
                .assunto(dto.getAssunto())
                .vara(dto.getVara())
                .dataAbertura(dto.getDataAbertura())
                .status(StatusProcesso.ATIVO)
                .build();
        processo = processoRepository.save(processo);
        log.info("Processo criado com sucesso. ID: {}", processo.getId());
        return toResponseDTO(processo);
    }

    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> listar(StatusProcesso status, Pageable pageable) {
        Page<Processo> page = status != null
                ? processoRepository.findByStatus(status, pageable)
                : processoRepository.findAll(pageable);
        return page.map(this::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public ProcessoResponseDTO buscarPorId(Long id) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + id));
        return toResponseDTO(processo);
    }

    @Transactional
    public ProcessoResponseDTO atualizar(Long id, ProcessoRequestDTO dto) {
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + id));
        processo.setAssunto(dto.getAssunto());
        processo.setVara(dto.getVara());
        processo.setDataAbertura(dto.getDataAbertura());
        return toResponseDTO(processoRepository.save(processo));
    }

    @Transactional
    public ProcessoResponseDTO atualizarStatus(Long id, StatusProcesso status) {
        log.info("Atualizando status do processo {} para {}", id, status);
        Processo processo = processoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + id));
        processo.setStatus(status);
        return toResponseDTO(processoRepository.save(processo));
    }

    @Transactional
    public ParteResponseDTO adicionarParte(Long processoId, ParteRequestDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + processoId));

        Parte parte = Parte.builder()
                .processo(processo)
                .tipo(dto.getTipo())
                .nome(dto.getNome())
                .documento(dto.getDocumento())
                .cep(dto.getCep())
                .build();

        if (dto.getCep() != null && !dto.getCep().isBlank()) {
            try {
                log.info("Buscando endereço para CEP: {}", dto.getCep());
                EnderecoDTO endereco = viaCepClient.buscarEnderecoPorCep(dto.getCep().replaceAll("-", ""));
                parte.setLogradouro(endereco.getLogradouro());
                parte.setBairro(endereco.getBairro());
                parte.setCidade(endereco.getLocalidade());
                parte.setUf(endereco.getUf());
                log.info("Endereço encontrado: {} - {}", endereco.getLogradouro(), endereco.getLocalidade());
            } catch (Exception e) {
                log.warn("Falha ao buscar CEP {}: {}", dto.getCep(), e.getMessage());
            }
        }

        return toParteResponseDTO(parteRepository.save(parte));
    }

    @Transactional
    public MovimentacaoResponseDTO adicionarMovimentacao(Long processoId, MovimentacaoRequestDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RuntimeException("Processo não encontrado: " + processoId));
        Movimentacao mov = Movimentacao.builder()
                .processo(processo)
                .descricao(dto.getDescricao())
                .build();
        log.info("Adicionando movimentação ao processo {}", processoId);
        return toMovimentacaoResponseDTO(movimentacaoRepository.save(mov));
    }

    private ProcessoResponseDTO toResponseDTO(Processo p) {
        ProcessoResponseDTO dto = new ProcessoResponseDTO();
        dto.setId(p.getId());
        dto.setNumero(p.getNumero());
        dto.setAssunto(p.getAssunto());
        dto.setVara(p.getVara());
        dto.setStatus(p.getStatus());
        dto.setDataAbertura(p.getDataAbertura());
        dto.setCriadoEm(p.getCriadoEm());
        dto.setAtualizadoEm(p.getAtualizadoEm());
        dto.setPartes(parteRepository.findByProcessoId(p.getId()).stream().map(this::toParteResponseDTO).toList());
        dto.setMovimentacoes(movimentacaoRepository.findByProcessoIdOrderByDataMovimentacaoDesc(p.getId()).stream().map(this::toMovimentacaoResponseDTO).toList());
        return dto;
    }

    private ParteResponseDTO toParteResponseDTO(Parte p) {
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

    private MovimentacaoResponseDTO toMovimentacaoResponseDTO(Movimentacao m) {
        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();
        dto.setId(m.getId());
        dto.setDescricao(m.getDescricao());
        dto.setDataMovimentacao(m.getDataMovimentacao());
        return dto;
    }
}