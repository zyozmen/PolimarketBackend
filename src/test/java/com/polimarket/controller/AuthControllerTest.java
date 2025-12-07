package com.polimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimarket.dto.LoginRequestDTO;
import com.polimarket.dto.LoginResponseDTO;
import com.polimarket.dto.LoginResponseDTO.EmpleadoPerfilDTO;
import com.polimarket.exception.CredencialesInvalidasException;
import com.polimarket.exception.DatosNoValidosException;
import com.polimarket.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para AuthController
 * Cobertura completa de todos los endpoints
 */
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO loginRequest;
    private LoginResponseDTO loginResponse;
    private String jwtToken;

    @BeforeEach
    void setUp() {
        // Configurar request de login
        loginRequest = new LoginRequestDTO();
        loginRequest.setUsuario("jperez");
        loginRequest.setPassword("password_cifrada");

        // Configurar token JWT
        jwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        // Configurar perfil del empleado
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("RRHH");

        EmpleadoPerfilDTO perfil = new EmpleadoPerfilDTO(
                1234567890L,
                "CEDULA",
                "Juan",
                "Pérez",
                "jperez",
                "ACTIVO",
                roles,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Configurar response de login
        loginResponse = new LoginResponseDTO(
                jwtToken,
                LocalDateTime.now().plusDays(1),
                perfil
        );
    }

    @Test
    void testLogin_Exitoso() throws Exception {
        // Arrange
        when(authService.autenticarEmpleado(any(LoginRequestDTO.class)))
                .thenReturn(loginResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken))
                .andExpect(jsonPath("$.perfil.usuario").value("jperez"))
                .andExpect(jsonPath("$.perfil.nombre").value("Juan"))
                .andExpect(jsonPath("$.perfil.apellido").value("Pérez"))
                .andExpect(jsonPath("$.perfil.identificacion").value(1234567890))
                .andExpect(jsonPath("$.perfil.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.perfil.roles").isArray())
                .andExpect(jsonPath("$.perfil.roles[*]").exists());

        verify(authService, times(1)).autenticarEmpleado(any(LoginRequestDTO.class));
    }

    @Test
    void testLogin_CredencialesInvalidas() throws Exception {
        // Arrange
        when(authService.autenticarEmpleado(any(LoginRequestDTO.class)))
                .thenThrow(new CredencialesInvalidasException("Usuario o contraseña incorrectos"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value(401))
                .andExpect(jsonPath("$.mensaje").value("Credenciales Inválidas"))
                .andExpect(jsonPath("$.detalles").value("Usuario o contraseña incorrectos"));

        verify(authService, times(1)).autenticarEmpleado(any(LoginRequestDTO.class));
    }

    @Test
    void testLogin_DatosNoValidos() throws Exception {
        // Arrange
        when(authService.autenticarEmpleado(any(LoginRequestDTO.class)))
                .thenThrow(new DatosNoValidosException("El usuario es obligatorio"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(400))
                .andExpect(jsonPath("$.mensaje").value("Datos No Válidos"))
                .andExpect(jsonPath("$.detalles").value("El usuario es obligatorio"));

        verify(authService, times(1)).autenticarEmpleado(any(LoginRequestDTO.class));
    }

    @Test
    void testLogin_UsuarioVacio() throws Exception {
        // Arrange
        loginRequest.setUsuario("");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_PasswordVacio() throws Exception {
        // Arrange
        loginRequest.setPassword("");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogout_Exitoso() throws Exception {
        // Arrange
        doNothing().when(authService).revocarToken(anyString());

        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(authService, times(1)).revocarToken(jwtToken);
    }

    @Test
    void testLogout_SinBearer() throws Exception {
        // Arrange
        doNothing().when(authService).revocarToken(anyString());

        // Act & Assert
        mockMvc.perform(post("/auth/logout")
                .header("Authorization", jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(authService, times(1)).revocarToken(jwtToken);
    }

    @Test
    void testValidateToken_TokenValido() throws Exception {
        // Arrange
        when(authService.validarToken(anyString())).thenReturn(true);

        // Act & Assert
        mockMvc.perform(get("/auth/validate")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authService, times(1)).validarToken(jwtToken);
    }

    @Test
    void testValidateToken_TokenInvalido() throws Exception {
        // Arrange
        when(authService.validarToken(anyString())).thenReturn(false);

        // Act & Assert
        mockMvc.perform(get("/auth/validate")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));

        verify(authService, times(1)).validarToken(jwtToken);
    }

    @Test
    void testHealth() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/auth/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Servicio de Autenticación disponible"));
    }

    @Test
    void testLogin_EmpleadoConMultiplesRoles() throws Exception {
        // Arrange
        Set<String> roles = new HashSet<>();
        roles.add("ADMIN");
        roles.add("RRHH");
        roles.add("VENTAS");
        roles.add("BODEGA");

        EmpleadoPerfilDTO perfil = new EmpleadoPerfilDTO(
                1234567890L,
                "CEDULA",
                "Juan",
                "Pérez",
                "jperez",
                "ACTIVO",
                roles,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        LoginResponseDTO response = new LoginResponseDTO(
                jwtToken,
                LocalDateTime.now().plusDays(1),
                perfil
        );

        when(authService.autenticarEmpleado(any(LoginRequestDTO.class)))
                .thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.perfil.roles").isArray())
                .andExpect(jsonPath("$.perfil.roles.length()").value(4));

        verify(authService, times(1)).autenticarEmpleado(any(LoginRequestDTO.class));
    }

    @Test
    void testLogin_ErrorInterno() throws Exception {
        // Arrange
        when(authService.autenticarEmpleado(any(LoginRequestDTO.class)))
                .thenThrow(new RuntimeException("Error interno del servidor"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.codigo").value(500))
                .andExpect(jsonPath("$.mensaje").value("Error Interno del Servidor"));

        verify(authService, times(1)).autenticarEmpleado(any(LoginRequestDTO.class));
    }
}
