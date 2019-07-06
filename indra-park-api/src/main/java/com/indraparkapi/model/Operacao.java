package com.indraparkapi.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import static java.time.temporal.ChronoUnit.MINUTES;

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

    public void finalizaOperacao() {
        if (estado.equals(EstadoOperacao.SAIDA)) {
            throw new RuntimeException("Esta Operação ja esta finalizada");
        }
        if (this.dataHoraSaida == null) {
            this.dataHoraSaida = LocalDateTime.now();
        }
        this.estado = EstadoOperacao.SAIDA;
    }

    public BigDecimal calculaValorDoPeriodo() {
        if (this.dataHoraSaida == null) {
            this.dataHoraSaida = LocalDateTime.now();
        }

        BigDecimal minutos = BigDecimal.valueOf(MINUTES.between(this.dataHoraEntrada, this.dataHoraSaida));

        BigDecimal resutado = minutos.divide(new BigDecimal(60), 2, RoundingMode.CEILING);

        return resutado.multiply(this.veiculo.getModelo().getPrecoEstacionamentoPorHora());
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
