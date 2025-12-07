package com.polimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de error de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDTO {

    private int codigo;
    private String mensaje;
    private String detalles;
    private long timestamp;
}
