package com.indraparkapi.util;

import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EstatisticasUtil {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    public static HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia(List<Operacao> operacoes) {
        Map<String, List<Operacao>> operacoesPorDia = getOperacoesPorDia(operacoes);

        return getQuantidadesDeVeiculosEmCadaDia(operacoesPorDia);
    }

    private static HashMap<String, Map<ModeloVeiculo, Integer>> getQuantidadesDeVeiculosEmCadaDia(
            Map<String, List<Operacao>> operacoesPorDia
    ) {

        HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia = new HashMap<>();

        Set<String> dias = operacoesPorDia.keySet();
        preencheEspacoEmVazio(veiculosPorDia, dias);

        dias.forEach(dia -> {
            Map<ModeloVeiculo, List<Operacao>> operacoesPorModelo = getOperacoesPorModelo(operacoesPorDia, dia);

            HashMap<ModeloVeiculo, Integer> quantidadeDeUmModeloNoDia = new HashMap<>();

            operacoesPorModelo.forEach((modeloVeiculo, operacoes) ->
                    quantidadeDeUmModeloNoDia.put(modeloVeiculo, operacoes.size()));

            veiculosPorDia.put(dia, quantidadeDeUmModeloNoDia);
        });

        return veiculosPorDia;
    }

    private static void preencheEspacoEmVazio(HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia,
                                              Set<String> dias) {
        if (dias.size() < 7) {
            LocalDate amanha = LocalDate.now().plusDays(1);
            LocalDate dataAtual = LocalDate.now().minusDays(6);

            while (dataAtual.isBefore(amanha)) {
                String dataStr = formatter.format(dataAtual);
                HashMap<ModeloVeiculo, Integer> quantidadeDeUmModeloNoDia = new HashMap<>();

                ModeloVeiculo[] values = ModeloVeiculo.values();
                for (ModeloVeiculo modelo : values) {
                    quantidadeDeUmModeloNoDia.put(modelo, 0);
                }

                veiculosPorDia.put(dataStr, quantidadeDeUmModeloNoDia);
                dataAtual = dataAtual.plusDays(1);
            }
        }
    }

    private static Map<ModeloVeiculo, List<Operacao>> getOperacoesPorModelo(
            Map<String, List<Operacao>> operacoesPorDia,
            String dia
    ) {
        return operacoesPorDia.get(dia).stream()
                .collect(Collectors.groupingBy(o -> o.getVeiculo().getModelo()));
    }

    private static Map<String, List<Operacao>> getOperacoesPorDia(List<Operacao> operacoes) {
        return operacoes.stream()
                .collect(Collectors.groupingBy(o -> formatter.format(o.getDataHoraEntrada())));
    }
}
