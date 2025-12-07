package com.polimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para transferir información de Empleado en respuestas de la API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoDTO {

    private Long identificacion;
    private String tipoIdentificacion;
    private String nombre;
    private String apellido;
    private String usuario;
    private String estado;
    private Set<String> roles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
