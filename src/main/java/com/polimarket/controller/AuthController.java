package com.polimarket.controller;

import com.polimarket.dto.LoginRequestDTO;
import com.polimarket.dto.LoginResponseDTO;
import com.polimarket.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para autenticación
 * Proporciona endpoints para login y gestión de sesiones
 *
 * Base path: /auth
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * POST /auth/login
     * Autentica un empleado con sus credenciales
     * La contraseña debe venir cifrada desde el cliente
     *
     * @param loginRequest DTO con usuario y contraseña cifrada
     * @return ResponseEntity con LoginResponseDTO (token + perfil) y código 200 (OK)
     *
     * Ejemplo de solicitud:
     * {
     *     "usuario": "jperez",
     *     "password": "contraseña_cifrada_en_base64"
     * }
     *
     * Ejemplo de respuesta:
     * {
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *     "expiracion": "2025-12-07T22:30:00",
     *     "perfil": {
     *         "identificacion": 1234567890,
     *         "tipoIdentificacion": "CEDULA",
     *         "nombre": "Juan",
     *         "apellido": "Pérez",
     *         "usuario": "jperez",
     *         "estado": "ACTIVO",
     *         "roles": ["ADMIN", "RRHH"],
     *         "fechaCreacion": "2025-12-06T19:30:00",
     *         "fechaActualizacion": "2025-12-06T19:30:00"
     *     }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = authService.autenticarEmpleado(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /auth/logout
     * Revoca un token (cierra sesión)
     *
     * @param token token JWT a revocar
     * @return ResponseEntity con código 200 (OK)
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        // Remover el prefijo "Bearer " si existe
        String jwtToken = token.replace("Bearer ", "");
        authService.revocarToken(jwtToken);
        return ResponseEntity.ok().build();
    }

    /**
     * GET /auth/validate
     * Valida un token JWT
     *
     * @param token token JWT a validar
     * @return ResponseEntity con boolean indicando si el token es válido
     */
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        boolean isValid = authService.validarToken(jwtToken);
        return ResponseEntity.ok(isValid);
    }

    /**
     * GET /auth/health
     * Health check del servicio de autenticación
     *
     * @return ResponseEntity con mensaje de estado
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de Autenticación disponible");
    }
}
