package com.polimarket.service;

import com.polimarket.dto.LoginRequestDTO;
import com.polimarket.dto.LoginResponseDTO;
import com.polimarket.dto.LoginResponseDTO.EmpleadoPerfilDTO;
import com.polimarket.exception.CredencialesInvalidasException;
import com.polimarket.exception.DatosNoValidosException;
import com.polimarket.model.Empleado;
import com.polimarket.model.EstadoEmpleado;
import com.polimarket.model.Token;
import com.polimarket.repository.EmpleadoRepository;
import com.polimarket.repository.TokenRepository;
import com.polimarket.util.JwtUtil;
import com.polimarket.util.PasswordEncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar la autenticación de empleados
 * Implementa la lógica de negocio para login, validación de credenciales y generación de tokens JWT
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final EmpleadoRepository empleadoRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncryptionUtil passwordUtil;

    /**
     * Autentica un empleado con sus credenciales
     * Valida las credenciales, genera un token JWT y retorna el perfil completo del empleado
     *
     * @param loginRequest DTO con usuario y contraseña cifrada
     * @return LoginResponseDTO con token y perfil del empleado
     * @throws CredencialesInvalidasException si las credenciales son inválidas
     * @throws DatosNoValidosException si los datos de entrada son inválidos
     */
    public LoginResponseDTO autenticarEmpleado(LoginRequestDTO loginRequest) {
        log.info("Iniciando autenticación para usuario: {}", loginRequest.getUsuario());

        // Validar datos de entrada
        validarDatosLogin(loginRequest);

        // Buscar empleado por usuario
        Empleado empleado = empleadoRepository.findByUsuario(loginRequest.getUsuario())
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado: {}", loginRequest.getUsuario());
                    return new CredencialesInvalidasException(
                            "Usuario o contraseña incorrectos"
                    );
                });

        // Verificar que el empleado esté activo
        if (!EstadoEmpleado.ACTIVO.equals(empleado.getEstado())) {
            log.warn("Intento de login con empleado inactivo: {}", loginRequest.getUsuario());
            throw new CredencialesInvalidasException(
                    "El empleado no está activo en el sistema"
            );
        }

        // Descifrar y validar contraseña
        boolean passwordMatch = validarPassword(
                loginRequest.getPassword(),
                empleado.getPassword()
        );

        if (!passwordMatch) {
            log.warn("Contraseña incorrecta para usuario: {}", loginRequest.getUsuario());
            throw new CredencialesInvalidasException(
                    "Usuario o contraseña incorrectos"
            );
        }

        // Desactivar tokens anteriores del empleado
        tokenRepository.desactivarTokensDeEmpleado(empleado.getIdentificacion());

        // Extraer roles
        Set<String> roles = empleado.getRolList().stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toSet());

        // Generar token JWT
        String jwtToken = jwtUtil.generateTokenWithClaims(
                empleado.getUsuario(),
                empleado.getIdentificacion(),
                roles
        );

        LocalDateTime expiracion = jwtUtil.extractExpiration(jwtToken);

        // Guardar token en la base de datos
        Token token = new Token();
        token.setValor(jwtToken);
        token.setExpiracion(expiracion);
        token.setEmpleado(empleado);
        token.setActivo(true);
        tokenRepository.save(token);

        log.info("Autenticación exitosa para usuario: {}", loginRequest.getUsuario());

        // Construir perfil del empleado
        EmpleadoPerfilDTO perfil = new EmpleadoPerfilDTO(
                empleado.getIdentificacion(),
                empleado.getTipoIdentificacion(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getUsuario(),
                empleado.getEstado().name(),
                roles,
                empleado.getFechaCreacion(),
                empleado.getFechaActualizacion()
        );

        // Retornar respuesta con token y perfil
        return new LoginResponseDTO(jwtToken, expiracion, perfil);
    }

    /**
     * Valida los datos de entrada del login
     *
     * @param loginRequest datos de login
     * @throws DatosNoValidosException si los datos son inválidos
     */
    private void validarDatosLogin(LoginRequestDTO loginRequest) {
        if (loginRequest.getUsuario() == null || loginRequest.getUsuario().trim().isEmpty()) {
            throw new DatosNoValidosException("El usuario es obligatorio");
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            throw new DatosNoValidosException("La contraseña es obligatoria");
        }
    }

    /**
     * Valida la contraseña comparando la contraseña cifrada recibida
     * con la contraseña almacenada en la base de datos
     *
     * @param passwordCifrada contraseña cifrada recibida
     * @param passwordAlmacenada contraseña almacenada en BD
     * @return true si las contraseñas coinciden
     */
    private boolean validarPassword(String passwordCifrada, String passwordAlmacenada) {
        try {
            // Descifrar la contraseña recibida
            String passwordDescifrada = passwordUtil.decrypt(passwordCifrada);
            
            // Descifrar la contraseña almacenada
            String passwordAlmacenadaDescifrada = passwordUtil.decrypt(passwordAlmacenada);
            
            // Comparar en texto plano
            return passwordDescifrada.equals(passwordAlmacenadaDescifrada);
        } catch (Exception e) {
            log.error("Error al validar contraseña", e);
            return false;
        }
    }

    /**
     * Valida un token JWT
     *
     * @param token token JWT a validar
     * @return true si el token es válido y está activo
     */
    @Transactional(readOnly = true)
    public boolean validarToken(String token) {
        try {
            // Verificar si el token existe y está activo en la BD
            Token tokenEntity = tokenRepository.findByValorAndActivoTrue(token)
                    .orElse(null);

            if (tokenEntity == null) {
                return false;
            }

            // Validar el token JWT
            String usuario = jwtUtil.extractUsername(token);
            return jwtUtil.validateToken(token, usuario);
        } catch (Exception e) {
            log.error("Error al validar token", e);
            return false;
        }
    }

    /**
     * Revoca un token (logout)
     *
     * @param token token a revocar
     */
    public void revocarToken(String token) {
        tokenRepository.findByValorAndActivoTrue(token)
                .ifPresent(t -> {
                    t.setActivo(false);
                    tokenRepository.save(t);
                    log.info("Token revocado para usuario: {}", t.getEmpleado().getUsuario());
                });
    }
}
