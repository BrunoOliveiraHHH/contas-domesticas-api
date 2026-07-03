package br.com.contasdomesticas.api.exception;

/**
 * Lancada quando um recurso solicitado nao existe.
 */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
