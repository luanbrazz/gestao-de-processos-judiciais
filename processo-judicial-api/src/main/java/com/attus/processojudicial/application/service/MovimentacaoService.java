package com.attus.processojudicial.application.service;

import com.attus.processojudicial.api.exception.RecursoNaoEncontradoException;
import com.attus.processojudicial.api.exception.RegraDeNegocioException;
import com.attus.processojudicial.application.dto.MovimentacaoRequestDTO;
import com.attus.processojudicial.application.dto.MovimentacaoResponseDTO;
import com.attus.processojudicial.domain.entity.Movimentacao;
import com.attus.processojudicial.domain.entity.Processo;
import com.attus.processojudicial.domain.enums.StatusProcesso;
import com.attus.processojudicial.domain.repository.MovimentacaoRepository;
import com.attus.processojudicial.domain.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovimentacaoService implements MovimentacaoServiceI {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProcessoRepository processoRepository;

    @Override
    @Transactional
    public MovimentacaoResponseDTO adicionar(UUID processoId, MovimentacaoRequestDTO dto) {
        Processo processo = buscarProcessoOuLancarExcecao(processoId);
        if (processo.getStatus() == StatusProcesso.ENCERRADO) {
            throw new RegraDeNegocioException(
                    "Não é possível adicionar partes a um processo encerrado."
            );
        }
        Movimentacao movimentacao = Movimentacao.builder()
                .processo(processo)
                .descricao(dto.getDescricao())
                .build();
        log.info("Adicionando movimentação ao processo {}", processoId);
        return toResponseDTO(movimentacaoRepository.save(movimentacao));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorProcesso(UUID processoId) {
        return movimentacaoRepository.findByProcessoIdOrderByDataMovimentacaoDesc(processoId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private Processo buscarProcessoOuLancarExcecao(UUID processoId) {
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