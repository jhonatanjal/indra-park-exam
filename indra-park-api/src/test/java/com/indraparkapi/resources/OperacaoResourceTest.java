package com.indraparkapi.resources;

import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.EstadoOperacao;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    public void operacoesDeveRetonarSomenteOperacoesDoDiaCorrente() throws Exception {

        mockMvc.perform(get("/operacoes")).andExpect(status().isOk());

        verify(operacaoRepository)
                .findByDataHoraEntradaIsBetweenOrEstadoIs(any(LocalDateTime.class),
                        any(LocalDateTime.class),
                        any(EstadoOperacao.class));
    }

    @Test
    public void deveSalvarVeiculoEOperacaoNoBancoQuandoRecebeVeiculo() throws Exception {
        Veiculo veiculo = new Veiculo("ddd3456", ModeloVeiculo.CARRO, new Operacao(LocalDateTime.now()));
        when(veiculoRepository.save(any(Veiculo.class))).thenReturn(veiculo);

        String json = "{\"placa\":\"ddd3456\",\"modelo\":\"CARRO\"}";
        mockMvc.perform(post("/operacoes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print())
                .andExpect(status().isCreated());


        verify(veiculoRepository).save(veiculo);
    }

    @Test
    public void deveRetornarCobrancaEOperacaoQuandoRecebeOperacaoDeSaida() throws Exception {
        Operacao operacao = new Operacao(LocalDateTime.now());
        Veiculo veiculo = new Veiculo("ddd3456", ModeloVeiculo.CARRO, operacao);

        when(operacaoRepository.save(any(Operacao.class))).thenReturn(operacao);
        when(veiculoRepository.findById(any(String.class))).thenReturn(Optional.of(veiculo));

        mockMvc.perform(put("/operacoes?placa=ddd3456"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(veiculoRepository).findById("ddd3456");
        verify(operacaoRepository).save(operacao);
    }
}