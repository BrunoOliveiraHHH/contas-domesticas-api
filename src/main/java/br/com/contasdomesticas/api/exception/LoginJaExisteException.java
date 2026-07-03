package br.com.contasdomesticas.api.exception;

/**
 * Lancada ao tentar criar um usuario com login ja existente.
 */
public class LoginJaExisteException extends RuntimeException {

    public LoginJaExisteException(String login) {
        super("Ja existe um usuario com o login: " + login);
    }
}
