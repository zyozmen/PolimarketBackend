package com.polimarket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitud de autenticación
 * Contiene las credenciales del usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    /**
     * Nombre de usuario
     */
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    /**
     * Contraseña cifrada
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
