package com.polimarket.service;

import com.polimarket.dto.CrearActualizarEmpleadoDTO;
import com.polimarket.dto.EmpleadoDTO;
import com.polimarket.exception.DatosNoValidosException;
import com.polimarket.exception.RecursoNoEncontradoException;
import com.polimarket.model.Empleado;
import com.polimarket.model.EstadoEmpleado;
import com.polimarket.model.Rol;
import com.polimarket.model.TipoRol;
import com.polimarket.repository.EmpleadoRepository;
import com.polimarket.repository.RolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para EmpleadoService
 */
@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private CrearActualizarEmpleadoDTO dto;
    private Empleado empleado;
    private Rol rol;

    @BeforeEach
    void setUp() {
        // Inicializar DTO
        dto = new CrearActualizarEmpleadoDTO();
        dto.setIdentificacion(1234567890L);
        dto.setTipoIdentificacion("CEDULA");
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setUsuario("jperez");
        dto.setPassword("password123");
        dto.setEstado("ACTIVO");
        dto.setRoles(new HashSet<>(Arrays.asList("ADMIN", "RRHH")));

        // Inicializar Empleado
        empleado = new Empleado();
        empleado.setIdentificacion(1234567890L);
        empleado.setTipoIdentificacion("CEDULA");
        empleado.setNombre("Juan");
        empleado.setApellido("Pérez");
        empleado.setUsuario("jperez");
        empleado.setPassword("password123");
        empleado.setEstado(EstadoEmpleado.ACTIVO);

        // Inicializar Rol
        rol = new Rol();
        rol.setRolId(1);
        rol.setNombre(TipoRol.ADMIN);
    }

    @Test
    void testCrearEmpleadoExitosamente() {
        // Arrange
        when(empleadoRepository.existsByUsuario("jperez")).thenReturn(false);
        when(rolRepository.findByNombre(TipoRol.ADMIN)).thenReturn(Optional.of(rol));
        when(rolRepository.findByNombre(TipoRol.RRHH)).thenReturn(Optional.empty());
        when(rolRepository.save(any(Rol.class))).thenReturn(new Rol(TipoRol.RRHH));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        EmpleadoDTO resultado = empleadoService.crearEmpleado(dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        assertEquals("jperez", resultado.getUsuario());
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    void testCrearEmpleadoConUsuarioDuplicado() {
        // Arrange
        when(empleadoRepository.existsByUsuario("jperez")).thenReturn(true);

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> {
            empleadoService.crearEmpleado(dto);
        });
    }

    @Test
    void testCrearEmpleadoConDatosInvalidos() {
        // Arrange
        dto.setNombre(""); // Nombre vacío

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> {
            empleadoService.crearEmpleado(dto);
        });
    }

    @Test
    void testCrearEmpleadoConIdentificacionInvalida() {
        // Arrange
        dto.setIdentificacion(-5L); // Identificación negativa

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> {
            empleadoService.crearEmpleado(dto);
        });
    }

    @Test
    void testCrearEmpleadoConPasswordCorta() {
        // Arrange
        dto.setPassword("123"); // Contraseña muy corta

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> {
            empleadoService.crearEmpleado(dto);
        });
    }

    @Test
    void testCrearEmpleadoConEstadoInvalido() {
        // Arrange
        dto.setEstado("ESTADO_INVALIDO");

        // Act & Assert
        assertThrows(DatosNoValidosException.class, () -> {
            empleadoService.crearEmpleado(dto);
        });
    }

    @Test
    void testObtenerTodosLosEmpleados() {
        // Arrange
        List<Empleado> empleados = Arrays.asList(empleado);
        when(empleadoRepository.findAll()).thenReturn(empleados);

        // Act
        List<EmpleadoDTO> resultado = empleadoService.obtenerTodosLosEmpleados();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    void testObtenerEmpleadoPorIdExitosamente() {
        // Arrange
        when(empleadoRepository.findById(1234567890L)).thenReturn(Optional.of(empleado));

        // Act
        EmpleadoDTO resultado = empleadoService.obtenerEmpleadoPorId(1234567890L);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(empleadoRepository, times(1)).findById(1234567890L);
    }

    @Test
    void testObtenerEmpleadoPorIdNoEncontrado() {
        // Arrange
        when(empleadoRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            empleadoService.obtenerEmpleadoPorId(9999L);
        });
    }

    @Test
    void testActualizarEmpleadoExitosamente() {
        // Arrange
        when(empleadoRepository.findByUsuario("jperez")).thenReturn(Optional.of(empleado));
        when(rolRepository.findByNombre(TipoRol.ADMIN)).thenReturn(Optional.of(rol));
        when(rolRepository.findByNombre(TipoRol.RRHH)).thenReturn(Optional.empty());
        when(rolRepository.save(any(Rol.class))).thenReturn(new Rol(TipoRol.RRHH));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        // Act
        EmpleadoDTO resultado = empleadoService.actualizarEmpleado("jperez", dto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(empleadoRepository, times(1)).save(any(Empleado.class));
    }

    @Test
    void testActualizarEmpleadoNoEncontrado() {
        // Arrange
        when(empleadoRepository.findByUsuario("usuarioNoExiste")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            empleadoService.actualizarEmpleado("usuarioNoExiste", dto);
        });
    }

    @Test
    void testEliminarEmpleadoExitosamente() {
        // Arrange
        when(empleadoRepository.findById(1234567890L)).thenReturn(Optional.of(empleado));

        // Act
        empleadoService.eliminarEmpleado(1234567890L);

        // Assert
        verify(empleadoRepository, times(1)).delete(empleado);
    }

    @Test
    void testEliminarEmpleadoNoEncontrado() {
        // Arrange
        when(empleadoRepository.findById(9999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> {
            empleadoService.eliminarEmpleado(9999L);
        });
    }
}
