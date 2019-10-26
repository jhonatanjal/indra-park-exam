package com.indraparkapi.controller;

import com.indraparkapi.dto.OperacaoDTO;
import com.indraparkapi.model.ModeloVeiculo;
import com.indraparkapi.model.Operacao;
import com.indraparkapi.model.Veiculo;
import com.indraparkapi.service.OperacaoService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/operacoes")
@AllArgsConstructor
public class OperacaoController {

    private OperacaoService service;

    @GetMapping
    public List<OperacaoDTO> operacoes() {
       return service.buscaOperacoesDoDia();
    }

    @GetMapping(params = {"dataInicial", "dataFinal"})
    public ResponseEntity<?> operacoesEntre(@RequestParam
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
                                            @RequestParam
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal) {

        return ResponseEntity.ok(service.filtraOperacoesComDataHoraEntradaEntre(dataInicial, dataFinal));
    }

    @GetMapping(params = {"veiculoPlaca"})
    public List<OperacaoDTO> buscaPorPlacaVeiculo(@RequestParam String veiculoPlaca) {
        return service.buscaOperacoesDoVeiculo(veiculoPlaca);
    }

    @GetMapping("/valorCobrado")
    public ResponseEntity<?> valorEstacionamento(@RequestParam Long operacaoId) {
        return ResponseEntity.ok(service.calculaValorEstacionamento(operacaoId));
    }

    @GetMapping("/estatisticas")
    public HashMap<String, Map<ModeloVeiculo, Integer>> estatisticasDaSemana() {
        return service.geraEstatisticasDaSemana();
    }

    @GetMapping("/entrada")
    public ResponseEntity<?> bustaOperacaoDeEntradoDoVeiculo(@RequestParam String veiculoPlaca) {
        Optional<Operacao> operacao = service.buscaOperacaoEmAbertoDoVeiculo(veiculoPlaca);
        if (!operacao.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new OperacaoDTO(operacao.get()));
    }

    @PostMapping
    public ResponseEntity<?> entrada(@Valid @RequestBody Veiculo veiculo) {
        return new ResponseEntity<>(
                service.realizaEntrada(veiculo),
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
    public ResponseEntity<?> saida(@PathVariable(name = "id") Long operacaoId) {
        return ResponseEntity.ok(service.relizaSaida(operacaoId));
    }
}
