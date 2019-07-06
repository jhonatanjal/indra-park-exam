package com.indraparkapi.model;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class OperacaoTest {

    @Test
    public void finalizaOperacaoDeveRetornarValorDoPeriodoEstacionado() {
        LocalDateTime dataHoraEntrada = LocalDateTime.now().minusHours(2).minusMinutes(15);
        BigDecimal valorEspera = new BigDecimal("33.75");

        Operacao operacao = new Operacao(dataHoraEntrada);
        new Veiculo("ffff3344", ModeloVeiculo.CARRO, operacao);
        BigDecimal valorCobrado = operacao.calculaValorDoPeriodo();

        assertThat(valorCobrado, is(valorEspera));
    }
}