package com.indraparkapi.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Entity
public class Veiculo {
    @Id
    private String placa;

    @Enumerated(EnumType.STRING)
    private ModeloVeiculo modelo;

    @OneToMany(mappedBy = "veiculo", cascade = CascadeType.ALL)
    private List<Operacao> operacoes = new ArrayList<>();

    @Deprecated
    public Veiculo() {
    }

    public Veiculo(@NotNull String placa, ModeloVeiculo modelo, Operacao operacao) {
        this.placa = placa;
        this.modelo = modelo;
        adicionaOperacao(operacao);
    }

    public void adicionaOperacao(Operacao operacao) {
        if (veiculoTemOperacaoEmAberto()) {
            throw new RuntimeException("O Veiculo ainda tem operações em aberto.");
        }

        operacao.setVeiculo(this);
        operacoes.add(operacao);
    }

    private boolean veiculoTemOperacaoEmAberto() {
        Optional<Operacao> operacaoEmAberto = getOperacaoEmAberto();
        return operacaoEmAberto.map(value -> value.getEstado().equals(EstadoOperacao.ENTRADA))
                .orElse(false);
    }

    public Optional<Operacao> getOperacaoEmAberto() {
        if (operacoes.isEmpty()) {
            return Optional.empty();
        }
        return operacoes.stream()
                .filter(operacao -> operacao.getEstado().equals(EstadoOperacao.ENTRADA))
                .findFirst();
    }

    public String getPlaca() {
        return placa;
    }

    public ModeloVeiculo getModelo() {
        return modelo;
    }

    public List<Operacao> getOperacoes() {
        return Collections.unmodifiableList(operacoes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Veiculo veiculo = (Veiculo) o;

        return placa.equals(veiculo.placa);
    }

    @Override
    public int hashCode() {
        return placa.hashCode();
    }
}
