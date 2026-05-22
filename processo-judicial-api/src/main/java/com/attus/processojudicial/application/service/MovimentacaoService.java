package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.application.dto.MovimentacaoRequestDTO;
import com.attus.processojudicial.application.dto.MovimentacaoResponseDTO;
import com.attus.processojudicial.domain.entity.Movimentacao;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.repository.MovimentacaoRepository;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimentacaoService implements MovimentacaoServiceI {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProcessoRepository processoRepository;

    @Override
    @Transactional
    public MovimentacaoResponseDTO adicionar(Long processoId, MovimentacaoRequestDTO dto) {
        Processo processo = buscarProcessoOuLancarExcecao(processoId);
        Movimentacao movimentacao = Movimentacao.builder()
                .processo(processo)
                .descricao(dto.getDescricao())
                .build();
        log.info("Adicionando movimentação ao processo {}", processoId);
        return toResponseDTO(movimentacaoRepository.save(movimentacao));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorProcesso(Long processoId) {
        return movimentacaoRepository.findByProcessoIdOrderByDataMovimentacaoDesc(processoId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Processo buscarProcessoOuLancarExcecao(Long processoId) {
        return processoRepository.findById(processoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Processo não encontrado: " + processoId));
    }

    private MovimentacaoResponseDTO toResponseDTO(Movimentacao m) {
        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();
        dto.setId(m.getId());
        dto.setDescricao(m.getDescricao());
        dto.setDataMovimentacao(m.getDataMovimentacao());
        return dto;
    }
}