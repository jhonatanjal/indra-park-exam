package com.indraparkapi.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Operacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoOperacao estado;

    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;

    @ManyToOne
    private Veiculo veiculo;

    @Deprecated
    public Operacao() {
    }

    public Operacao(LocalDateTime dataHoraEntrada) {
        this.dataHoraEntrada = dataHoraEntrada;
        this.estado = EstadoOperacao.ENTRADA;
    }

    public BigDecimal finalizaOperacao() {
        if (estado.equals(EstadoOperacao.SAIDA)) {
            throw new RuntimeException("Esta Operação ja esta finalizada");
        }
        this.dataHoraSaida = LocalDateTime.now();
        this.estado = EstadoOperacao.SAIDA;

        return BigDecimal.TEN;
    }

    public Long getId() {
        return id;
    }

    public EstadoOperacao getEstado() {
        return estado;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }
}
