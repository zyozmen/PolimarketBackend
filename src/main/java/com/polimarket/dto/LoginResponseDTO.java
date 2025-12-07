package com.polimarket.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO para respuesta de autenticación
 * Contiene el token JWT y el perfil completo del empleado autenticado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    /**
     * Token JWT generado
     */
    private String token;

    /**
     * Fecha de expiración del token
     */
    private LocalDateTime expiracion;

    /**
     * Perfil completo del empleado autenticado
     */
    private EmpleadoPerfilDTO perfil;

    /**
     * DTO con información completa del perfil del empleado
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmpleadoPerfilDTO {
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
}
