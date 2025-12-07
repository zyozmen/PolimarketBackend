package com.polimarket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.polimarket.dto.CrearActualizarEmpleadoDTO;
import com.polimarket.dto.EmpleadoDTO;
import com.polimarket.exception.RecursoNoEncontradoException;
import com.polimarket.service.EmpleadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para EmpleadoController
 */
@WebMvcTest(EmpleadoController.class)
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpleadoService empleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private CrearActualizarEmpleadoDTO dto;
    private EmpleadoDTO empleadoDTO;

    @BeforeEach
    void setUp() {
        // Inicializar DTO de entrada
        dto = new CrearActualizarEmpleadoDTO();
        dto.setIdentificacion(1234567890L);
        dto.setTipoIdentificacion("CEDULA");
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setUsuario("jperez");
        dto.setPassword("password123");
        dto.setEstado("ACTIVO");
        dto.setRoles(new HashSet<>(Arrays.asList("ADMIN", "RRHH")));

        // Inicializar DTO de respuesta
        empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setIdentificacion(1234567890L);
        empleadoDTO.setTipoIdentificacion("CEDULA");
        empleadoDTO.setNombre("Juan");
        empleadoDTO.setApellido("Pérez");
        empleadoDTO.setUsuario("jperez");
        empleadoDTO.setEstado("ACTIVO");
        empleadoDTO.setRoles(new HashSet<>(Arrays.asList("ADMIN", "RRHH")));
    }

    @Test
    @WithMockUser
    void testCrearEmpleado() throws Exception {
        // Arrange
        when(empleadoService.crearEmpleado(any(CrearActualizarEmpleadoDTO.class)))
                .thenReturn(empleadoDTO);

        // Act & Assert
        mockMvc.perform(post("/api/rrhh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario").value("jperez"))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(empleadoService, times(1)).crearEmpleado(any(CrearActualizarEmpleadoDTO.class));
    }

    @Test
    @WithMockUser
    void testObtenerTodosLosEmpleados() throws Exception {
        // Arrange
        List<EmpleadoDTO> empleados = Arrays.asList(empleadoDTO);
        when(empleadoService.obtenerTodosLosEmpleados()).thenReturn(empleados);

        // Act & Assert
        mockMvc.perform(get("/api/rrhh")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuario").value("jperez"))
                .andExpect(jsonPath("$[0].nombre").value("Juan"));

        verify(empleadoService, times(1)).obtenerTodosLosEmpleados();
    }

    @Test
    @WithMockUser
    void testObtenerEmpleadoPorId() throws Exception {
        // Arrange
        when(empleadoService.obtenerEmpleadoPorId(1234567890L))
                .thenReturn(empleadoDTO);

        // Act & Assert
        mockMvc.perform(get("/api/rrhh/1234567890")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("jperez"))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(empleadoService, times(1)).obtenerEmpleadoPorId(1234567890L);
    }

    @Test
    @WithMockUser
    void testObtenerEmpleadoPorIdNoEncontrado() throws Exception {
        // Arrange
        when(empleadoService.obtenerEmpleadoPorId(9999L))
                .thenThrow(new RecursoNoEncontradoException("Empleado no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/api/rrhh/9999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404))
                .andExpect(jsonPath("$.mensaje").value("Recurso No Encontrado"));
    }

    @Test
    @WithMockUser
    void testActualizarEmpleado() throws Exception {
        // Arrange
        when(empleadoService.actualizarEmpleado(anyString(), any(CrearActualizarEmpleadoDTO.class)))
                .thenReturn(empleadoDTO);

        // Act & Assert
        mockMvc.perform(put("/api/rrhh/jperez")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario").value("jperez"))
                .andExpect(jsonPath("$.nombre").value("Juan"));

        verify(empleadoService, times(1)).actualizarEmpleado(anyString(), any(CrearActualizarEmpleadoDTO.class));
    }

    @Test
    @WithMockUser
    void testActualizarEmpleadoNoEncontrado() throws Exception {
        // Arrange
        when(empleadoService.actualizarEmpleado(anyString(), any(CrearActualizarEmpleadoDTO.class)))
                .thenThrow(new RecursoNoEncontradoException("Empleado no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/api/rrhh/usuarioNoExiste")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404));
    }

    @Test
    @WithMockUser
    void testEliminarEmpleado() throws Exception {
        // Arrange
        doNothing().when(empleadoService).eliminarEmpleado(1234567890L);

        // Act & Assert
        mockMvc.perform(delete("/api/rrhh/1234567890")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(empleadoService, times(1)).eliminarEmpleado(1234567890L);
    }

    @Test
    @WithMockUser
    void testEliminarEmpleadoNoEncontrado() throws Exception {
        // Arrange
        doThrow(new RecursoNoEncontradoException("Empleado no encontrado"))
                .when(empleadoService).eliminarEmpleado(9999L);

        // Act & Assert
        mockMvc.perform(delete("/api/rrhh/9999")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404));
    }

    @Test
    @WithMockUser
    void testHealthEndpoint() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/rrhh/health")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
