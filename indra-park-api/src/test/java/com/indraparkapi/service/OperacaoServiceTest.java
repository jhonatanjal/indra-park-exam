package com.indraparkapi.service;

import com.indraparkapi.dto.OperacaoDTO;
import com.indraparkapi.exception.DataFinalMenorQueDataInicialException;
import com.indraparkapi.model.EstadoOperacao;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.repository.OperacaoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class OperacaoServiceTest {

    @InjectMocks
    private OperacaoService service;

    @Mock
    private OperacaoRepository repository;

    @Test
    public void deve_RetornarOperacoesDoDia() {
        service.buscaOperacoesDoDia();

        LocalDateTime hoje = LocalDateTime.now().with(LocalTime.of(0, 0));
        LocalDateTime amanha = hoje.plusDays(1);

        verify(repository).findByDataHoraEntradaIsBetweenOrEstadoIs(
                hoje,
                amanha,
                EstadoOperacao.ENTRADA);
    }

    @Test
    public void deve_FiltrarOperacoesEntreDataInicialEhDataFinal_QuandoRecebeDataFinalEhDataInicial(){
        LocalDate dataInicial = LocalDate.now();
        LocalDate dataFinal = dataInicial.plusDays(1);

        service.filtraOperacoesComDataHoraEntradaEntre(dataInicial, dataFinal);

        verify(repository).findByDataHoraEntradaIsBetween(
                dataInicial.atTime(LocalTime.of(0, 0)),
                dataFinal.atTime(LocalTime.of(23, 59))
        );
    }

    @Test(expected = DataFinalMenorQueDataInicialException.class)
    public void deve_LancarDataFinalMenorQueDataInicialException_QuandoRecebeDataFinalMenorQueInicial() {
        LocalDate dataInicial = LocalDate.now();
        LocalDate dataFinal = dataInicial.minusDays(1);

        service.filtraOperacoesComDataHoraEntradaEntre(dataInicial, dataFinal);
    }

    @Test
    public void deve_RalizarSaida_QuandoRecebeIdDaOperacao() {
        Operacao operacao = new Operacao(LocalDateTime.now(), new Veiculo("te453", ModeloVeiculo.CARRO));

        doReturn(Optional.of(operacao)).when(repository).findById(any(Long.class));
        doReturn(operacao).when(repository).save(any(Operacao.class));

        service.relizaSaida(1L);

        verify(repository).save(operacao);
    }

    @Test
    public void deve_RetornarValorDoPeriodoEstacionado_QuandoRecebeIdDaOperacao() {
        Operacao operacao = new Operacao(LocalDateTime.now(), new Veiculo("ffff3344", ModeloVeiculo.CARRO));

        doReturn(Optional.of(operacao)).when(repository).findById(any(Long.class));

        HashMap<String, BigDecimal> resultado = service.calculaValorEstacionamento(1L);

        assertNotNull(resultado);
        assertNotNull(resultado.get("valorDoPeriodo"));
    }

    @Test
    public void deve_RealizarEntradaDeVeiculo_QuandoRecebeVeiculoDTO() {
        Veiculo veiculo = new Veiculo("TES4455", ModeloVeiculo.MOTO);
        doReturn(new Operacao(LocalDateTime.now(), veiculo)).when(repository).save(any(Operacao.class));

        OperacaoDTO operacaoDTO = service.realizaEntrada(veiculo);

        verify(repository).save(any(Operacao.class));
        assertTrue(operacaoDTO.getEstado().isEntrada());
        assertThat(operacaoDTO.getPlaca(), is(veiculo.getPlaca()));
    }

    @Test
    public void deve_BuscarOperacoesDoVeiculo_QuandoRecebePlacaDoVeiculo() {
        service.buscaOperacoesDoVeiculo("RR456");

        verify(repository).findByVeiculoPlacaIgnoreCase("RR456");
    }

    @Test
    public void deve_BuscarOperacaoEmAbertoDoVeiculo_QuandoRecebePlacaDoVeiculo() {
        service.buscaOperacaoEmAbertoDoVeiculo("RR456");

        verify(repository).findByVeiculoPlacaIgnoreCaseAndEstado("RR456", EstadoOperacao.ENTRADA);
    }
}