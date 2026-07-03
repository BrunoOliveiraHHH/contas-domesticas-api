package br.com.contasdomesticas.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Excecao lançada globalmente
 */
@Getter
public class AplicacaoException extends RuntimeException {

    private final String mensagem;
    private final HttpStatus status;

    public AplicacaoException(String mensagem, HttpStatus status) {
        this.mensagem = mensagem;
        this.status = status;
    }
}
