package com.indraparkapi.service;

import com.indraparkapi.dto.OperacaoDTO;
import com.indraparkapi.exception.DataFinalMenorQueDataInicialException;
import com.indraparkapi.exception.OperacaoNaoEncontradaException;
import com.indraparkapi.model.EstadoOperacao;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.repository.OperacaoRepository;
import com.indraparkapi.util.EstatisticasUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class OperacaoService {

    private OperacaoRepository repository;

    public List<OperacaoDTO> buscaOperacoesDoDia() {
        LocalDateTime hoje = LocalDateTime.now().with(LocalTime.of(0, 0));
        LocalDateTime amanha = hoje.plusDays(1);

        List<Operacao> operacoes = repository
                .findByDataHoraEntradaIsBetweenOrEstadoIs(hoje, amanha, EstadoOperacao.ENTRADA);

        return operacoes.stream()
                .map(OperacaoDTO::new)
                .collect(toList());
    }

    public List<OperacaoDTO> filtraOperacoesComDataHoraEntradaEntre(LocalDate dataInicial, LocalDate dataFinal) {
        validaDataFinalMaiorQueDataInicial(dataInicial, dataFinal);

        List<Operacao> operacoes = repository.findByDataHoraEntradaIsBetween(
                dataInicial.atTime(LocalTime.of(0, 0)),
                dataFinal.atTime(LocalTime.of(23, 59)));

        return operacoes.stream()
                .map(OperacaoDTO::new)
                .collect(toList());
    }

    private void validaDataFinalMaiorQueDataInicial(LocalDate dataInicial, LocalDate dataFinal) {
        if (dataFinal.isBefore(dataInicial)) {
            throw new DataFinalMenorQueDataInicialException();
        }
    }

    public OperacaoDTO relizaSaida(Long operacaoId) {
        Operacao operacao = buscaOperacaoPorId(operacaoId);
        operacao.finalizaOperacao();
        return new OperacaoDTO(repository.save(operacao));
    }

    public OperacaoDTO realizaEntrada(Veiculo veiculo) {
        Operacao operacao = new Operacao(LocalDateTime.now(), veiculo);

        return new OperacaoDTO(repository.save(operacao));
    }

    public HashMap<String, Map<ModeloVeiculo, Integer>> geraEstatisticasDaSemana() {
        return new EstatisticasUtil(repository).veiculosPorDia();
    }

    public HashMap<String, BigDecimal> calculaValorEstacionamento(Long operacaoId) {
        Operacao operacao = buscaOperacaoPorId(operacaoId);

        BigDecimal valorDoPeriodo = operacao.calculaValorDoPeriodo();

        HashMap<String, BigDecimal> resposta = new HashMap<>();
        resposta.put("valorDoPeriodo", valorDoPeriodo);

        return resposta;
    }

    private Operacao buscaOperacaoPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new OperacaoNaoEncontradaException(id));
    }

    public List<OperacaoDTO> buscaOperacoesDoVeiculo(String placaVeiculo) {
        return repository.findByVeiculoPlacaIgnoreCase(placaVeiculo).stream()
                .map(OperacaoDTO::new)
                .collect(toList());
    }

    public Optional<Operacao> buscaOperacaoEmAbertoDoVeiculo(String placaVeiculo) {
        return repository.findByVeiculoPlacaIgnoreCaseAndEstado(placaVeiculo, EstadoOperacao.ENTRADA);
    }
}
