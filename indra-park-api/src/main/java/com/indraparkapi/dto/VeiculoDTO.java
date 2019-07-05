package com.indraparkapi.dto;

import com.indraparkapi.model.ModeloVeiculo;

public class VeiculoDTO {
    private final String placa;
    private final ModeloVeiculo modelo;

    public VeiculoDTO(String placa, ModeloVeiculo modelo) {
        this.placa = placa;
        this.modelo = modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public ModeloVeiculo getModelo() {
        return modelo;
    }

}
