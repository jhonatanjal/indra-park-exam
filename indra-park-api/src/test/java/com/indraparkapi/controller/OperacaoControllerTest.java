package com.indraparkapi.controller;

import com.indraparkapi.dto.OperacaoDTO;
import com.indraparkapi.exception.DataFinalMenorQueDataInicialException;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.service.OperacaoService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class OperacaoControllerTest {

    @InjectMocks
    private OperacaoController controller;

    @Mock
    private OperacaoService service;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private List<OperacaoDTO> operacoes;

    @Before
    public void setUp() {
        operacoes = Arrays.asList(new OperacaoDTO(), new OperacaoDTO());
    }

    @Test
    public void deve_RetornarOperacoesDoDia() {
        doReturn(operacoes).when(service).buscaOperacoesDoDia();
        List<OperacaoDTO> operacoes = controller.operacoes();

        verify(service).buscaOperacoesDoDia();
        assertThat(operacoes, hasSize(2));
    }

    @Test
    public void deve_FiltrarOperacoesEntreDatas_QuandoRecebeDatas() {
        doReturn(operacoes).when(service).filtraOperacoesComDataHoraEntradaEntre(any(), any());
        LocalDate dataInicial = LocalDate.now().minusDays(1);
        LocalDate dataFinal = LocalDate.now();

        ResponseEntity<?> response = controller.operacoesEntre(dataInicial, dataFinal);

        verify(service).filtraOperacoesComDataHoraEntradaEntre(dataInicial, dataFinal);
        assertThat(operacoes, hasSize(2));
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void deve_RetornarLancarDataFinalMenorQueDataInicialException_QuandoRecebeParametrosInvalidos() {
        doThrow(new DataFinalMenorQueDataInicialException())
                .when(service).filtraOperacoesComDataHoraEntradaEntre(any(), any());
        thrown.expect(DataFinalMenorQueDataInicialException.class);
        thrown.expectMessage("A data final é anterior a inicial");

        controller.operacoesEntre(LocalDate.now(), LocalDate.now());
    }

    @Test
    public void deve_FiltrarOperacoesDeUmVeiculo_QuandoRecebVeiculoPlaca_() {
        doReturn(operacoes).when(service).buscaOperacoesDoVeiculo(any());

        List<OperacaoDTO> operacoes = controller.buscaPorPlacaVeiculo("TES1234");

        verify(service).buscaOperacoesDoVeiculo("TES1234");
        assertThat(operacoes, hasSize(2));
    }

    @Test
    public void deve_CaucularValorDoPeriodoEstacionado_QuandoRecebeOperacaoId() {
        doReturn(new HashMap<>()).when(service).calculaValorEstacionamento(any());

        ResponseEntity<?> response = controller.valorEstacionamento(1L);

        verify(service).calculaValorEstacionamento(1L);
        assertNotNull(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void deve_RetornarOperacaoDeEntradaDoVeiculo_QuandoRecebeVeiculoPlaca() {
        doReturn(Optional.of(new Operacao(LocalDateTime.now(), new Veiculo("TES1234", ModeloVeiculo.CAMINHAO))))
                .when(service).buscaOperacaoEmAbertoDoVeiculo(any());

        ResponseEntity<?> response = controller.bustaOperacaoDeEntradoDoVeiculo("TES1234");

        verify(service).buscaOperacaoEmAbertoDoVeiculo("TES1234");
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void deve_RealizarEntradaDeVeiculo_QuandoRecebeVeiculo() {
        doReturn(new OperacaoDTO()).when(service).realizaEntrada(any());

        ResponseEntity<?> response = controller.entrada(new Veiculo());

        verify(service).realizaEntrada(any());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }

    @Test
    public void deve_RealizarSaida_quandoOperacaoId() {
        ResponseEntity<?> response = controller.saida(1L);

        verify(service).relizaSaida(1L);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}