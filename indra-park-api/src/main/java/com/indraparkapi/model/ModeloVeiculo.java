package com.indraparkapi.model;

import java.math.BigDecimal;

public enum ModeloVeiculo {

    CARRO(new BigDecimal(15)),
    MOTO(new BigDecimal(10)),
    CAMINHAO(new BigDecimal(35)),
    CAMINHONETE(new BigDecimal(20));


    private BigDecimal precoEstacionamentoPorHora;

    ModeloVeiculo(BigDecimal precoEstacionamentoPorHora) {
        this.precoEstacionamentoPorHora = precoEstacionamentoPorHora;
    }

    public BigDecimal getPrecoEstacionamentoPorHora() {
        return precoEstacionamentoPorHora;
    }
}
