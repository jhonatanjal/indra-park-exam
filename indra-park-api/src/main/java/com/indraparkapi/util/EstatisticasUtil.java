package com.indraparkapi.util;

import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.repository.OperacaoRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstatisticasUtil {

    private final OperacaoRepository repository;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

    public EstatisticasUtil(OperacaoRepository repository) {
        this.repository = repository;
    }

    public HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia() {
        HashMap<String, Map<ModeloVeiculo, Integer>> veiculosPorDia = new HashMap<>();
        LocalDate hoje = LocalDate.now();

        List<LocalDate> dias = getUltimosSeteDias(hoje);

        dias.forEach(dia -> {
            HashMap<ModeloVeiculo, Integer> quantidadeDeUmModeloNoDia = new HashMap<>();
            for (ModeloVeiculo modelo : ModeloVeiculo.values()) {
                Integer count = buscaQuantidadePorModeloNoDia(dia, modelo);
                quantidadeDeUmModeloNoDia.put(modelo, count);
            }
            veiculosPorDia.put(formatter.format(dia), quantidadeDeUmModeloNoDia);
        });

        return veiculosPorDia;
    }

    private Integer buscaQuantidadePorModeloNoDia(LocalDate dia, ModeloVeiculo modelo) {
        return repository.countByDataHoraEntradaBetweenAndVeiculo_Modelo(
                dia.atTime(0, 0),
                dia.atTime(23, 59),
                modelo
        );
    }

    private List<LocalDate> getUltimosSeteDias(LocalDate hoje) {
        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            dates.add(hoje.minusDays(i));
        }
        return dates;
    }
}
