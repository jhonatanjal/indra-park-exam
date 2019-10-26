package com.indraparkapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Veiculo {

    @Id
    @NotBlank(message = "O campo placa é obrigatório")
    @Pattern(message = "Formato inválido, a placa deve ter 7 dígitos compostos somente de letras e números",
            regexp = "[a-zA-Z0-9]{7}")
    private String placa;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "O campo modelo é obrigatório")
    private ModeloVeiculo modelo;

}
