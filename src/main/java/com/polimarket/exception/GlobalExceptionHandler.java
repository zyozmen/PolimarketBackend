package com.polimarket.exception;

import com.polimarket.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Manejador global de excepciones para la API
 * Centraliza el manejo de errores y proporciona respuestas consistentes
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de recurso no encontrado (404)
     *
     * @param ex excepción lanzada
     * @param request información de la solicitud
     * @return ResponseEntity con ErrorDTO
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorDTO> manejarRecursoNoEncontrado(
            RecursoNoEncontradoException ex,
            WebRequest request) {

        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.NOT_FOUND.value(),
                "Recurso No Encontrado",
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de datos no válidos (400)
     *
     * @param ex excepción lanzada
     * @param request información de la solicitud
     * @return ResponseEntity con ErrorDTO
     */
    @ExceptionHandler(DatosNoValidosException.class)
    public ResponseEntity<ErrorDTO> manejarDatosNoValidos(
            DatosNoValidosException ex,
            WebRequest request) {

        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.BAD_REQUEST.value(),
                "Datos No Válidos",
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de credenciales inválidas (401)
     *
     * @param ex excepción lanzada
     * @param request información de la solicitud
     * @return ResponseEntity con ErrorDTO
     */
    @ExceptionHandler(CredencialesInvalidasException.class)
    public ResponseEntity<ErrorDTO> manejarCredencialesInvalidas(
            CredencialesInvalidasException ex,
            WebRequest request) {

        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "Credenciales Inválidas",
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja todas las demás excepciones no previstas (500)
     *
     * @param ex excepción lanzada
     * @param request información de la solicitud
     * @return ResponseEntity con ErrorDTO
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> manejarExcepcionGlobal(
            Exception ex,
            WebRequest request) {

        ErrorDTO errorDTO = new ErrorDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error Interno del Servidor",
                ex.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
