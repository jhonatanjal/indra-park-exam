package com.indraparkapi.resources;

import com.indraparkapi.dto.OperacaoDTO;
import com.indraparkapi.dto.VeiculoDTO;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.EstadoOperacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.repository.OperacaoRepository;
import com.indraparkapi.repository.VeiculoRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operacoes")
public class OperacaoResource {

    private VeiculoRepository veiculoRepository;
    private OperacaoRepository operacaoRepository;

    public OperacaoResource(VeiculoRepository veiculoRepository, OperacaoRepository operacaoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.operacaoRepository = operacaoRepository;
    }

    @GetMapping
    public List<OperacaoDTO> operacoes() {
        LocalDateTime hoje = LocalDateTime.now().with(LocalTime.of(0, 0));
        LocalDateTime amanha = hoje.plusDays(1);
        List<Operacao> operacoes = operacaoRepository
                .findByDataHoraEntradaIsBetweenOrEstadoIs(hoje, amanha, EstadoOperacao.ENTRADA);

        return operacoes.stream()
                .map(OperacaoDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping(params = {"dataInicial", "dataFinal"})
    public List<OperacaoDTO> operacoesEntre(@RequestParam
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
                                            @RequestParam
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        List<Operacao> operacoes = operacaoRepository.findByDataHoraEntradaIsBetween(
                dataInicial.atTime(LocalTime.of(0, 0)),
                dataFinal.atTime(LocalTime.of(23, 59)));

        return operacoes.stream()
                .map(OperacaoDTO::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{placa}")
    public List<OperacaoDTO> buscaPor(@PathVariable String placa) {
        Optional<Veiculo> veiculoOpt = veiculoRepository.findById(placa);
        List<OperacaoDTO> operacoes = new ArrayList<>();

        veiculoOpt.ifPresent(veiculo ->
                veiculo.getOperacoes().forEach(operacao ->
                        operacoes.add(new OperacaoDTO(operacao))));

        return operacoes;
    }

    @PostMapping
    public ResponseEntity<?> entrada(@RequestBody VeiculoDTO veiculoDTO) {
        Operacao operacao = new Operacao(LocalDateTime.now());
        Veiculo veiculo = new Veiculo(veiculoDTO.getPlaca(), veiculoDTO.getModelo(), operacao);
        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);

        return new ResponseEntity<>(
                new OperacaoDTO(veiculoSalvo.getOperacaoEmAberto().get()),
                HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> saida(@RequestParam String placa) {
        Optional<Veiculo> veiculo = veiculoRepository.findById(placa);
        if (!veiculo.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Optional<Operacao> operacaoOpt = veiculo.get().getOperacaoEmAberto();
        if (!operacaoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Operacao operacao = operacaoOpt.get();
        BigDecimal valorCobrado = operacao.finalizaOperacao();
        operacaoRepository.save(operacao);

        HashMap<String, Object> resposta = new HashMap<>();
        resposta.put("operacao", new OperacaoDTO(operacao));
        resposta.put("cobranca", valorCobrado);

        return new ResponseEntity<>(resposta, HttpStatus.OK);
    }
}
