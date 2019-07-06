package com.indraparkapi.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class OperacaoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VeiculoRepository veiculoRepository;

    @MockBean
    private OperacaoRepository operacaoRepository;

    @Test
    public void operacoesDeveRetonarOperacoesDoDiaCorrentePorPadrao() throws Exception {

        mockMvc.perform(get("/operacoes")).andExpect(status().isOk());

        verify(operacaoRepository)
                .findByDataHoraEntradaIsBetweenOrEstadoIs(any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(EstadoOperacao.class));
    }

    @Test
    public void deveFiltrarOperacoesEntrePeriodoDeDatas() throws Exception {
        LocalDateTime dataInicial = LocalDateTime.of(2019, 7, 3, 0, 0);
        LocalDateTime dataFinal = LocalDateTime.of(2019, 7, 5, 23, 59);

        mockMvc.perform(get("/operacoes?dataInicial=2019-07-03&dataFinal=2019-07-05"))
                .andExpect(status().isOk());

        verify(operacaoRepository)
                .findByDataHoraEntradaIsBetween(dataInicial, dataFinal);

    }

    @Test
    public void deveFiltrarOperacoesDeUmVeiculoPelaPlaca() throws Exception {
        String placa = "eeee3344";

        mockMvc.perform(get("/operacoes/eeee3344"))
                .andExpect(status().isOk());

        verify(veiculoRepository).findById(placa);

    }

    @Test
    public void deveCaucularOValorDoPeriodoEstacionado() throws Exception {
        Operacao operacao = new Operacao(LocalDateTime.now());
        Veiculo veiculo = new Veiculo("ddd3456", ModeloVeiculo.CARRO, operacao);
        //when(veiculoRepository.findById("ddd3456")).thenReturn(Optional.of(veiculo));
        when(operacaoRepository.findById(1L)).thenReturn(Optional.of(operacao));

        mockMvc.perform(get("/operacoes/1/valorCobrado"))
                .andExpect(status().isOk());

        verify(operacaoRepository).save(operacao);
    }

    @Test
    public void deveSalvarVeiculoEOperacaoNoBancoQuandoRecebeVeiculo() throws Exception {
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
    public void deveAtualizarEstadoDaOperacaoParaSaida() throws Exception {
        LocalDateTime dataHoraEntrada = LocalDateTime.now();
        LocalDateTime dataHoraSaida = dataHoraEntrada.plusHours(1);

        Operacao operacao = new Operacao(dataHoraEntrada);
        Veiculo veiculo = new Veiculo("ddd3456", ModeloVeiculo.CARRO, operacao);

        when(operacaoRepository.save(any(Operacao.class))).thenReturn(operacao);
        when(veiculoRepository.findById(any(String.class))).thenReturn(Optional.of(veiculo));

        MvcResult mvcResult = mockMvc.perform(put("/operacoes/ddd3456"))
                .andExpect(status().isOk())
                .andReturn();

        String json = mvcResult.getResponse().getContentAsString();
        String estado = new ObjectMapper()
                .readValue(json, JsonNode.class)
                .get("estado").asText();

        assertThat(estado, is("SAIDA"));

        verify(veiculoRepository).findById("ddd3456");
        verify(operacaoRepository).save(operacao);

    }

    @Test
    public void deveAtualizarEstadoOperacaoNoBanco() throws Exception {
        LocalDateTime dataHoraEntrada = LocalDateTime.now();

        Operacao operacao = new Operacao(dataHoraEntrada);
        Veiculo veiculo = new Veiculo("ddd3456", ModeloVeiculo.CARRO, operacao);

        when(operacaoRepository.save(any(Operacao.class))).thenReturn(operacao);
        when(veiculoRepository.findById("ddd3456")).thenReturn(Optional.of(veiculo));

        mockMvc.perform(put("/operacoes/ddd3456"))
                .andExpect(status().isOk());

        verify(veiculoRepository).findById("ddd3456");
        verify(operacaoRepository).save(operacao);
    }

}