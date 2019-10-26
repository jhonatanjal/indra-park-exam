package com.indraparkapi.model;

import com.indraparkapi.exception.OperacaoJaEstaFinalizadaException;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class OperacaoTest {


    @Test
    public void deve_FinalizarOperacao() {
        Operacao operacao = new Operacao(LocalDateTime.now(), new Veiculo());

        operacao.finalizaOperacao();

        assertNotNull(operacao.getDataHoraSaida());
        assertTrue(operacao.getEstado().isSaida());
    }

    @Test(expected = OperacaoJaEstaFinalizadaException.class)
    public void deve_LancarOperacaoJaEstaFinalizadaException_QuandoTentaFinalizarOperacaoJaFinalizada() {
        Operacao operacao = new Operacao(LocalDateTime.now(), new Veiculo());
        operacao.finalizaOperacao();

        operacao.finalizaOperacao();
    }

    @Test
    public void deve_CalcularValorDoPeriodoEstacionado() {
        LocalDateTime dataHoraEntrada = LocalDateTime.now().minusHours(2).minusMinutes(15);
        BigDecimal valorEspera = new BigDecimal("33.75");

        Operacao operacao = new Operacao(dataHoraEntrada, new Veiculo("ffff3344", ModeloVeiculo.CARRO));
        BigDecimal valorCobrado = operacao.calculaValorDoPeriodo();

        assertThat(valorCobrado, is(valorEspera));
    }
}