package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.api.exception.RegraDeNegocioException;
import com.attus.processojudicial.application.dto.ProcessoRequestDTO;
import com.attus.processojudicial.application.dto.ProcessoResponseDTO;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessoService implements ProcessoServiceI {

    private final ProcessoRepository processoRepository;
    private final ParteServiceI parteService;
    private final MovimentacaoServiceI movimentacaoService;

    @Override
    @Transactional
    public ProcessoResponseDTO criar(ProcessoRequestDTO dto) {
        log.info("Criando processo número: {}", dto.getNumero());
        validarNumeroUnico(dto.getNumero());
        Processo processo = Processo.builder()
                .numero(dto.getNumero())
                .assunto(dto.getAssunto())
                .vara(dto.getVara())
                .dataAbertura(dto.getDataAbertura())
                .status(StatusProcesso.ATIVO)
                .build();
        Processo salvo = processoRepository.save(processo);
        log.info("Processo criado. ID: {}", salvo.getId());
        return toResponseDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProcessoResponseDTO> listar(StatusProcesso status, Pageable pageable) {
        Page<Processo> page = status != null
                ? processoRepository.findByStatus(status, pageable)
                : processoRepository.findAll(pageable);
        return page.map(this::toResponseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessoResponseDTO buscarPorId(Long id) {
        return toResponseDTO(buscarOuLancarExcecao(id));
    }

    @Override
    @Transactional
    public ProcessoResponseDTO atualizar(Long id, ProcessoRequestDTO dto) {
        Processo processo = buscarOuLancarExcecao(id);
        processo.setAssunto(dto.getAssunto());
        processo.setVara(dto.getVara());
        processo.setDataAbertura(dto.getDataAbertura());
        return toResponseDTO(processoRepository.save(processo));
    }

    @Override
    @Transactional
    public ProcessoResponseDTO atualizarStatus(Long id, StatusProcesso status) {
        log.info("Atualizando status do processo {} para {}", id, status);
        Processo processo = buscarOuLancarExcecao(id);
        processo.setStatus(status);
        return toResponseDTO(processoRepository.save(processo));
    }

    private void validarNumeroUnico(String numero) {
        if (processoRepository.existsByNumero(numero)) {
            throw new RegraDeNegocioException("Já existe um processo com o número: " + numero);
        }
    }

    private Processo buscarOuLancarExcecao(Long id) {
        return processoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Processo não encontrado: " + id));
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
        dto.setPartes(parteService.listarPorProcesso(p.getId()));
        dto.setMovimentacoes(movimentacaoService.listarPorProcesso(p.getId()));
        return dto;
    }
}