package com.indraparkapi.model;

import com.indraparkapi.exception.OperacaoJaEstaFinalizadaException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.time.temporal.ChronoUnit.MINUTES;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Operacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoOperacao estado;

    private LocalDateTime dataHoraEntrada;

    private LocalDateTime dataHoraSaida;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Veiculo veiculo;

    public Operacao(LocalDateTime dataHoraEntrada, Veiculo veiculo) {
        this.dataHoraEntrada = dataHoraEntrada;
        this.veiculo = veiculo;
        this.estado = EstadoOperacao.ENTRADA;
    }

    public void finalizaOperacao() {
        if (estaFinalizada()) {
            throw new OperacaoJaEstaFinalizadaException();
        }

        if (Objects.isNull(dataHoraSaida)) {
            this.dataHoraSaida = LocalDateTime.now();
        }

        this.estado = EstadoOperacao.SAIDA;
    }

    private boolean estaFinalizada() {
        return estado.isSaida() && Objects.nonNull(dataHoraSaida);
    }

    public BigDecimal calculaValorDoPeriodo() {
        if (!estaFinalizada()) {
            finalizaOperacao();
        }

        BigDecimal minutosEntreEntradaEhSaida = BigDecimal.valueOf(MINUTES.between(
                this.dataHoraEntrada, this.dataHoraSaida));

        BigDecimal horasEstacionadas = converteMinutosParaHoras(minutosEntreEntradaEhSaida);

        return mutiplicaHorasEstacionadasPorPrecoDaHoraDeEstacionamento(horasEstacionadas);
    }

    private BigDecimal mutiplicaHorasEstacionadasPorPrecoDaHoraDeEstacionamento(BigDecimal horasEstacionadas) {
        return horasEstacionadas.multiply(this.veiculo.getModelo().getPrecoEstacionamentoPorHora());
    }

    private BigDecimal converteMinutosParaHoras(BigDecimal minutos) {
        return minutos.divide(new BigDecimal(60), 2, RoundingMode.CEILING);
    }
}
