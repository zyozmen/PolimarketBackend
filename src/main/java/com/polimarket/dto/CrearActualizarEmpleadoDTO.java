package com.polimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * DTO para recibir datos en la creación/actualización de Empleado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearActualizarEmpleadoDTO {

    private Long identificacion;
    private String tipoIdentificacion;
    private String nombre;
    private String apellido;
    private String usuario;
    private String password;
    private String estado;
    private Set<String> roles;
}
