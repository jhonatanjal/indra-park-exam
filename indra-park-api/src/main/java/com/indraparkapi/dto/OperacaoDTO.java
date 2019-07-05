package com.indraparkapi.dto;

import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.EstadoOperacao;

import java.time.LocalDateTime;

public class OperacaoDTO {
    private Long id;
    private String placa;
    private ModeloVeiculo modeloVeiculo;
    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;
    private EstadoOperacao estado;

    public OperacaoDTO(Operacao operacao) {
        this.id = operacao.getId();
        this.placa = operacao.getVeiculo().getPlaca();
        this.modeloVeiculo = operacao.getVeiculo().getModelo();
        this.dataHoraEntrada = operacao.getDataHoraEntrada();
        this.dataHoraSaida = operacao.getDataHoraSaida();
        this.estado = operacao.getEstado();
    }

    public Long getId() {
        return id;
    }

    public String getPlaca() {
        return placa;
    }

    public ModeloVeiculo getModeloVeiculo() {
        return modeloVeiculo;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }

    public EstadoOperacao getEstado() {
        return estado;
    }
}
