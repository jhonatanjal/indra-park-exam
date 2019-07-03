package com.indraparkapi.model;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class VeiculoTest {

    @Test(expected = RuntimeException.class)
    public void adicionaOperacaoNaoDeveAdicionarOperacoesDoMesmoTipoEmSequencia() {
        Veiculo veiculo = new Veiculo("we2334", ModeloVeiculo.CARRO, new Operacao(LocalDateTime.now()));

        veiculo.adicionaOperacao(new Operacao(LocalDateTime.now()));
    }

    @Test
    public void adicionaOperacaoDeveAtualizarVeiculoDaOperacao() {
        Operacao operacao = new Operacao(LocalDateTime.now());

        Veiculo veiculo = new Veiculo("we2334", ModeloVeiculo.CARRO, operacao);

        assertThat(operacao.getVeiculo(), is(veiculo));
    }
}