package com.indraparkapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class OperacaoNaoEncontradaException extends RuntimeException {
    public OperacaoNaoEncontradaException(Long id) {
        super(String.format("Não foi encontrada operação com o id: %d", id));
    }
}
