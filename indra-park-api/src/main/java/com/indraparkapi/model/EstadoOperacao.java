package com.indraparkapi.model;

public enum EstadoOperacao {
    ENTRADA, SAIDA;

    public boolean isEntrada() {
        return this.equals(ENTRADA);
    }

    public boolean isSaida() {
        return this.equals(SAIDA);
    }
}
