package com.polimarket.exception;

/**
 * Excepción lanzada cuando los datos proporcionados no son válidos
 */
public class DatosNoValidosException extends RuntimeException {

    public DatosNoValidosException(String mensaje) {
        super(mensaje);
    }

    public DatosNoValidosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
