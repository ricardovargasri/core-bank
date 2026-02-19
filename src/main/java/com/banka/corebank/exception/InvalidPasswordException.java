package com.banka.corebank.exception;

/**
 * Excepción lanzada cuando una contraseña no cumple con los requisitos de seguridad.
 */
public class InvalidPasswordException extends BusinessException {
    private static final long serialVersionUID = 1L;

    /**
     * Crea una nueva instancia con un mensaje de error.
     *
     * @param message el mensaje de error
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * Crea una nueva instancia con un mensaje de error y una causa.
     *
     * @param message el mensaje de error
     * @param cause la causa de la excepción
     */
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}