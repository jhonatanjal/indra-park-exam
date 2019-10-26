package com.indraparkapi.dto;

import com.indraparkapi.model.ModeloVeiculo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class VeiculoDTO {

    @NotBlank(message = "O campo placa é obrigatório")
    @Pattern(message = "Formato inválido, a placa deve ter 7 dígitos compostos somente de letras e números",
            regexp = "[a-zA-Z0-9]{7}")
    private final String placa;

    @NotNull(message = "O campo modelo é obrigatório")
    private final ModeloVeiculo modelo;
}
