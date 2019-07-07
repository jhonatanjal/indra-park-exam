package com.indraparkapi.util;

import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EstatisticasUtil {

    public static HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia(List<Operacao> operacoes) {
        Map<String, List<Operacao>> operacoesPorDia = getOperacoesPorDia(operacoes);

        return getQuantidadesDeVeiculosEmCadaDia(operacoesPorDia);
    }

    private static HashMap<String, Map<ModeloVeiculo, Integer>> getQuantidadesDeVeiculosEmCadaDia(
            Map<String, List<Operacao>> operacoesPorDia
    ) {

        HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia = new HashMap<>();

        operacoesPorDia.keySet().forEach(dia -> {
            Map<ModeloVeiculo, List<Operacao>> operacoesPorModelo = getOperacoesPorModelo(operacoesPorDia, dia);

            HashMap<ModeloVeiculo, Integer> quantidadeDeUmModeloNoDia = new HashMap<>();

            operacoesPorModelo.forEach((modeloVeiculo, operacoes) ->
                    quantidadeDeUmModeloNoDia.put(modeloVeiculo, operacoes.size()));

            veiculosPorDia.put(dia, quantidadeDeUmModeloNoDia);
        });

        return veiculosPorDia;
    }

    private static Map<ModeloVeiculo, List<Operacao>> getOperacoesPorModelo(
            Map<String, List<Operacao>> operacoesPorDia,
            String dia
    ) {
        return operacoesPorDia.get(dia).stream()
                .collect(Collectors.groupingBy(o -> o.getVeiculo().getModelo()));
    }

    private static Map<String, List<Operacao>> getOperacoesPorDia(List<Operacao> operacoes) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        return operacoes.stream()
                .collect(Collectors.groupingBy(o -> formatter.format(o.getDataHoraEntrada())));
    }
}
