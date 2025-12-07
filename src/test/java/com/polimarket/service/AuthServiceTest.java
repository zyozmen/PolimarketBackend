package com.polimarket.service;

import com.polimarket.dto.LoginRequestDTO;
import com.polimarket.dto.LoginResponseDTO;
import com.polimarket.exception.CredencialesInvalidasException;
import com.polimarket.exception.DatosNoValidosException;
import com.polimarket.model.Empleado;
import com.polimarket.model.EstadoEmpleado;
import com.polimarket.model.Rol;
import com.polimarket.model.TipoRol;
import com.polimarket.model.Token;
import com.polimarket.repository.EmpleadoRepository;
import com.polimarket.repository.TokenRepository;
import com.polimarket.util.JwtUtil;
import com.polimarket.util.PasswordEncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para AuthService
 * Cobertura completa de todos los métodos y escenarios
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private TokenRepository tokenRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncryptionUtil passwordUtil;

    @InjectMocks
    private AuthService authService;

    private Empleado empleado;
    private LoginRequestDTO loginRequest;
    private String passwordCifrada;
    private String jwtToken;
    private Set<Rol> roles;

    @BeforeEach
    void setUp() throws Exception {
        // Configurar empleado de prueba
        empleado = new Empleado();
        empleado.setIdentificacion(1234567890L);
        empleado.setTipoIdentificacion("CEDULA");
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setUsuario("jperez");
        empleado.setEstado(EstadoEmpleado.ACTIVO);
        empleado.setFechaCreacion(LocalDateTime.now());
        empleado.setFechaActualizacion(LocalDateTime.now());

        // Configurar roles
        Rol rolAdmin = new Rol();
        rolAdmin.setRolId(1);
        rolAdmin.setNombre(TipoRol.ADMIN);

        Rol rolRRHH = new Rol();
        rolRRHH.setRolId(2);
        rolRRHH.setNombre(TipoRol.RRHH);

        roles = new HashSet<>();
        roles.add(rolAdmin);
        roles.add(rolRRHH);
        empleado.setRolList(roles);

        // Configurar contraseña cifrada
        passwordCifrada = "password_cifrada_base64";
        empleado.setPassword(passwordCifrada);

        // Configurar request de login
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsuario("jperez");
        loginRequest.setPassword(passwordCifrada);

        // Configurar JWT token
        jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";
    }

    @Test
    void testAutenticarEmpleado_Exitoso() throws Exception {
        // Arrange
        when(empleadoRepository.findByUsuario("jperez")).thenReturn(Optional.of(empleado));
        when(passwordUtil.decrypt(anyString())).thenReturn("password123");
        when(jwtUtil.generateTokenWithClaims(anyString(), anyLong(), any())).thenReturn(jwtToken);
        when(jwtUtil.extractExpiration(jwtToken)).thenReturn(LocalDateTime.now().plusDays(1));
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        LoginResponseDTO response = authService.autenticarEmpleado(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        assertNotNull(response.getPerfil());
        assertEquals("jperez", response.getPerfil().getUsuario());
        assertEquals("Juan", response.getPerfil().getNombre());
        assertEquals("Pérez", response.getPerfil().getApellido());
        assertEquals(2, response.getPerfil().getRoles().size());
        assertTrue(response.getPerfil().getRoles().contains("ADMIN"));
        assertTrue(response.getPerfil().getRoles().contains("RRHH"));

        verify(empleadoRepository, times(1)).findByUsuario("jperez");
        verify(tokenRepository, times(1)).desactivarTokensDeEmpleado(empleado.getIdentificacion());
        verify(tokenRepository, times(1)).save(any(Token.class));
        verify(jwtUtil, times(1)).generateTokenWithClaims(anyString(), anyLong(), any());
    }

    @Test
    void testAutenticarEmpleado_UsuarioNoEncontrado() {
        // Arrange
        when(empleadoRepository.findByUsuario("jperez")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, times(1)).findByUsuario("jperez");
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testAutenticarEmpleado_EmpleadoInactivo() {
        // Arrange
        empleado.setEstado(EstadoEmpleado.INACTIVO);
        when(empleadoRepository.findByUsuario("jperez")).thenReturn(Optional.of(empleado));

        // Act & Assert
        CredencialesInvalidasException exception = assertThrows(
            CredencialesInvalidasException.class,
            () -> authService.autenticarEmpleado(loginRequest)
        );

        assertTrue(exception.getMessage().contains("no está activo"));
        verify(empleadoRepository, times(1)).findByUsuario("jperez");
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testAutenticarEmpleado_PasswordIncorrecto() throws Exception {
        // Arrange
        when(empleadoRepository.findByUsuario("jperez")).thenReturn(Optional.of(empleado));
        when(passwordUtil.decrypt(passwordCifrada)).thenReturn("password123");
        when(passwordUtil.decrypt(empleado.getPassword())).thenReturn("password456");

        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, times(1)).findByUsuario("jperez");
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testAutenticarEmpleado_UsuarioVacio() {
        // Arrange
        loginRequest.setUsuario("");

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, never()).findByUsuario(anyString());
    }

    @Test
    void testAutenticarEmpleado_UsuarioNull() {
        // Arrange
        loginRequest.setUsuario(null);

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, never()).findByUsuario(anyString());
    }

    @Test
    void testAutenticarEmpleado_PasswordVacio() {
        // Arrange
        loginRequest.setPassword("");

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, never()).findByUsuario(anyString());
    }

    @Test
    void testAutenticarEmpleado_PasswordNull() {
        // Arrange
        loginRequest.setPassword(null);

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, never()).findByUsuario(anyString());
    }

    @Test
    void testValidarToken_TokenValido() {
        // Arrange
        Token token = new Token();
        token.setValor(jwtToken);
        token.setActivo(true);
        token.setEmpleado(empleado);

        when(tokenRepository.findByValorAndActivoTrue(jwtToken)).thenReturn(Optional.of(token));
        when(jwtUtil.extractUsername(jwtToken)).thenReturn("jperez");
        when(jwtUtil.validateToken(jwtToken, "jperez")).thenReturn(true);

        // Act
        boolean isValid = authService.validarToken(jwtToken);

        // Assert
        assertTrue(isValid);
        verify(tokenRepository, times(1)).findByValorAndActivoTrue(jwtToken);
        verify(jwtUtil, times(1)).validateToken(jwtToken, "jperez");
    }

    @Test
    void testValidarToken_TokenInvalido() {
        // Arrange
        when(tokenRepository.findByValorAndActivoTrue(jwtToken)).thenReturn(Optional.empty());

        // Act
        boolean isValid = authService.validarToken(jwtToken);

        // Assert
        assertFalse(isValid);
        verify(tokenRepository, times(1)).findByValorAndActivoTrue(jwtToken);
        verify(jwtUtil, never()).validateToken(anyString(), anyString());
    }

    @Test
    void testValidarToken_ExcepcionEnValidacion() {
        // Arrange
        Token token = new Token();
        token.setValor(jwtToken);
        token.setActivo(true);

        when(tokenRepository.findByValorAndActivoTrue(jwtToken)).thenReturn(Optional.of(token));
        when(jwtUtil.extractUsername(jwtToken)).thenThrow(new RuntimeException("Token malformado"));

        // Act
        boolean isValid = authService.validarToken(jwtToken);

        // Assert
        assertFalse(isValid);
        verify(tokenRepository, times(1)).findByValorAndActivoTrue(jwtToken);
    }

    @Test
    void testRevocarToken_Exitoso() {
        // Arrange
        Token token = new Token();
        token.setValor(jwtToken);
        token.setActivo(true);
        token.setEmpleado(empleado);

        when(tokenRepository.findByValorAndActivoTrue(jwtToken)).thenReturn(Optional.of(token));
        when(tokenRepository.save(any(Token.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        authService.revocarToken(jwtToken);

        // Assert
        assertFalse(token.getActivo());
        verify(tokenRepository, times(1)).findByValorAndActivoTrue(jwtToken);
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void testRevocarToken_TokenNoExiste() {
        // Arrange
        when(tokenRepository.findByValorAndActivoTrue(jwtToken)).thenReturn(Optional.empty());

        // Act
        authService.revocarToken(jwtToken);

        // Assert
        verify(tokenRepository, times(1)).findByValorAndActivoTrue(jwtToken);
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testAutenticarEmpleado_ErrorEnDescifradoPassword() throws Exception {
        // Arrange
        when(empleadoRepository.findByUsuario("jperez")).thenReturn(Optional.of(empleado));
        when(passwordUtil.decrypt(anyString())).thenThrow(new Exception("Error en descifrado"));

        // Act & Assert
        assertThrows(CredencialesInvalidasException.class, () -> 
            authService.autenticarEmpleado(loginRequest)
        );

        verify(empleadoRepository, times(1)).findByUsuario("jperez");
        verify(tokenRepository, never()).save(any(Token.class));
    }
}
