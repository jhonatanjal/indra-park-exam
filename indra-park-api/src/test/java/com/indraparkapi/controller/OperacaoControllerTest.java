package com.indraparkapi.controller;

import com.indraparkapi.model.EstadoOperacao;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.repository.OperacaoRepository;
import com.indraparkapi.repository.VeiculoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class OperacaoControllerTest {

    private static final String OPERACOES_URI = "/operacoes";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VeiculoRepository veiculoRepository;

    @MockBean
    private OperacaoRepository operacaoRepository;

    @Test
    public void quandoRecebeRequisicaoGetParaOperacoes_deveRetornarOperacoesDoDia() throws Exception {

        mockMvc.perform(get(OPERACOES_URI)).andExpect(status().isOk());

        verify(operacaoRepository)
                .findByDataHoraEntradaIsBetweenOrEstadoIs(any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(EstadoOperacao.class));
    }

    @Test
    public void quandoRecebeRequisicaoGetComParametrosDataInicialDataFinal_deveFiltrarOperacoesEntreDatas()
            throws Exception {
        LocalDateTime dataInicial = LocalDateTime.of(2019, 7, 3, 0, 0);
        LocalDateTime dataFinal = LocalDateTime.of(2019, 7, 5, 23, 59);

        mockMvc.perform(get("/operacoes?dataInicial=2019-07-03&dataFinal=2019-07-05"))
                .andExpect(status().isOk());

        verify(operacaoRepository)
                .findByDataHoraEntradaIsBetween(dataInicial, dataFinal);

    }

    @Test
    public void quandoRecebeRequisicaoGetComParametrosDataInicialDataFinalInvalidos_deveRetornarBadRequest()
            throws Exception {

        mockMvc.perform(get("/operacoes?dataInicial=2019-07-05&dataFinal=2019-07-03"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void quandoRecebeRequisicaoGetComParametroVeiculoPlaca_deveFiltrarOperacoesDeUmVeiculo()
            throws Exception {
        Veiculo veiculo = new Veiculo("AQW1234", ModeloVeiculo.CAMINHONETE, new Operacao(LocalDateTime.now()));
        when(veiculoRepository.findById(veiculo.getPlaca())).thenReturn(Optional.of(veiculo));

        mockMvc.perform(get("/operacoes?veiculoPlaca=" + veiculo.getPlaca()))
                .andExpect(status().isOk());

        verify(veiculoRepository).findById("AQW1234");

    }

    @Test
    public void quandoRecebeRequisicaoGetComParaValorCobradoIdOperacao_deveCaucularValorDoPeriodoEstacionado()
            throws Exception {
        Operacao operacao = new Operacao(LocalDateTime.now());
        new Veiculo("ddd3456", ModeloVeiculo.CARRO, operacao);
        when(operacaoRepository.findById(1L)).thenReturn(Optional.of(operacao));

        mockMvc.perform(get("/operacoes/valorCobrado?idOperacao=1"))
                .andExpect(status().isOk());

        verify(operacaoRepository).save(operacao);
    }

    @Test
    public void quandoRecebeRequisicaoGetParaEntradaComVeiculoPlaca_deveRetornarOperacaoDeEntradaDoVeiculo()
            throws Exception {
        Operacao operacao1 = new Operacao(LocalDateTime.now());
        Veiculo veiculo = new Veiculo("URE5566", ModeloVeiculo.MOTO, operacao1);
        operacao1.finalizaOperacao();
        Operacao operacao2 = new Operacao(LocalDateTime.now().plusDays(1));
        veiculo.adicionaOperacao(operacao2);

        when(veiculoRepository.findById(veiculo.getPlaca())).thenReturn(Optional.of(veiculo));

        mockMvc.perform(get("/operacoes/entrada?veiculoPlaca=URE5566"))
                .andExpect(jsonPath("$.estado", is(EstadoOperacao.ENTRADA.toString())))
                .andExpect(status().isOk());

    }

    @Test
    public void quandoRecebeRequisicaoPost_deveSalvarVeiculoOperacaoNoBancoQuandoRecebeVeiculoValido()
            throws Exception {
        Veiculo veiculo = new Veiculo("ddd3456", ModeloVeiculo.CARRO, new Operacao(LocalDateTime.now()));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        String json = "{\"placa\":\"ddd3456\",\"modelo\":\"CARRO\"}";
        mockMvc.perform(post("/operacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());


        verify(veiculoRepository).save(veiculo);
    }

    @Test
    public void quandoRecevePutComIdOperacaoNoPath_deveAtualizarEstadoDaOperacaoParaSaida() throws Exception {
        LocalDateTime dataHoraEntrada = LocalDateTime.now();

        Operacao operacao = new Operacao(dataHoraEntrada);
        new Veiculo("ddd3456", ModeloVeiculo.CARRO, operacao);

        when(operacaoRepository.save(any(Operacao.class))).thenReturn(operacao);
        when(operacaoRepository.findById(1L)).thenReturn(Optional.of(operacao));

        mockMvc.perform(put("/operacoes/1"))
                .andExpect(jsonPath("$.estado", is(EstadoOperacao.SAIDA.toString())))
                .andExpect(status().isOk())
                .andReturn();

        verify(operacaoRepository).findById(1L);
        verify(operacaoRepository).save(operacao);

    }
}