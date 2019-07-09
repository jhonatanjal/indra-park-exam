package com.indraparkapi.controller;

import com.indraparkapi.dto.OperacaoDTO;
import com.indraparkapi.dto.VeiculoDTO;
import com.indraparkapi.model.EstadoOperacao;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.repository.OperacaoRepository;
import com.indraparkapi.repository.VeiculoRepository;
import com.indraparkapi.util.EstatisticasUtil;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/operacoes")
public class OperacaoController {

    private VeiculoRepository veiculoRepository;
    private OperacaoRepository operacaoRepository;

    public OperacaoController(VeiculoRepository veiculoRepository, OperacaoRepository operacaoRepository) {
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
    public ResponseEntity<?> operacoesEntre(@RequestParam
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
                                            @RequestParam
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {
        if (dataFinal.isBefore(dataInicial)) {
            return ResponseEntity.badRequest().body("A data final é anterior a inicial.");
        }

        List<Operacao> operacoes = operacaoRepository.findByDataHoraEntradaIsBetween(
                dataInicial.atTime(LocalTime.of(0, 0)),
                dataFinal.atTime(LocalTime.of(23, 59)));

        List<OperacaoDTO> operacoesDTO = operacoes.stream()
                .map(OperacaoDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(operacoesDTO);
    }

    @GetMapping(params = {"veiculoPlaca"})
    public ResponseEntity<?> buscaPor(@RequestParam String veiculoPlaca) {
        Optional<Veiculo> veiculo = veiculoRepository.findById(veiculoPlaca);

        List<OperacaoDTO> operacoes;
        if (veiculo.isPresent()) {
            operacoes = veiculo.get().getOperacoes()
                    .stream()
                    .map(OperacaoDTO::new)
                    .collect(Collectors.toList());
        } else {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(operacoes);
    }

    @GetMapping("/valorCobrado")
    public ResponseEntity<?> valorEstacionamento(@RequestParam Long idOperacao) {
        Optional<Operacao> operacaoOpt = operacaoRepository.findById(idOperacao);

        if (!operacaoOpt.isPresent()) {
            return new ResponseEntity<>("id invalido", HttpStatus.NOT_FOUND);
        }

        Operacao operacao = operacaoOpt.get();
        BigDecimal valorDoPeriodo = operacao.calculaValorDoPeriodo();
        operacaoRepository.save(operacao);

        HashMap<String, BigDecimal> resposta = new HashMap<>();
        resposta.put("valorDoPeriodo", valorDoPeriodo);

        return ResponseEntity.ok(resposta);

    }

    @GetMapping("/estatisticas")
    public HashMap<String, Map<ModeloVeiculo, Integer>> estatisticasDaSemana() {
        LocalDate dataDeHoje = LocalDate.now();

        LocalDateTime hoje = dataDeHoje.atTime(23, 59);
        LocalDateTime seteDiasAtras = dataDeHoje.minusDays(7).atTime(0, 0);


        List<Operacao> operacoes = operacaoRepository.findByDataHoraEntradaIsBetween(seteDiasAtras, hoje);

        return EstatisticasUtil.veiculosPorDia(operacoes);
    }

    @GetMapping("/entrada")
    public ResponseEntity<?> bustaOperacaoDeEntradoDoVeiculo(@RequestParam String veiculoPlaca) {
        Optional<Operacao> operacao = buscaOperacaoEmAbertoPela(veiculoPlaca.toUpperCase());
        if (!operacao.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OperacaoDTO(operacao.get()));
    }

    private Optional<Operacao> buscaOperacaoEmAbertoPela(String placa) {
        Optional<Veiculo> veiculo = veiculoRepository.findById(placa);
        if (!veiculo.isPresent()) {
            return Optional.empty();
        }
        return veiculo.get().getOperacaoEmAberto();

    }

    @PostMapping
    public ResponseEntity<?> entrada(@Valid @RequestBody VeiculoDTO veiculoDTO) {
        Operacao operacao = new Operacao(LocalDateTime.now());
        Veiculo veiculo = new Veiculo(veiculoDTO.getPlaca().toUpperCase(),
                veiculoDTO.getModelo(),
                operacao);
        Veiculo veiculoSalvo = veiculoRepository.save(veiculo);

        return new ResponseEntity<>(
                new OperacaoDTO(veiculoSalvo.getOperacaoEmAberto().get()),
                HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String nomeAtributo = ((FieldError) error).getField();
            String mensagem = error.getDefaultMessage();
            erros.put(nomeAtributo, mensagem);
        });

        return erros;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> saida(@PathVariable(name = "id") Long idOperacao) {
        Optional<Operacao> operacaoOpt = operacaoRepository.findById(idOperacao);
        if (!operacaoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Operacao operacao = operacaoOpt.get();
        operacao.finalizaOperacao();
        operacaoRepository.save(operacao);

        return new ResponseEntity<>(new OperacaoDTO(operacao), HttpStatus.OK);
    }
}
