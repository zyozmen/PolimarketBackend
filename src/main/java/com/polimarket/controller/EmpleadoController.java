package com.polimarket.controller;

import com.polimarket.dto.CrearActualizarEmpleadoDTO;
import com.polimarket.dto.EmpleadoDTO;
import com.polimarket.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de Empleados
 * Proporciona endpoints para operaciones CRUD
 *
 * Base path: /api/rrhh
 */
@RestController
@RequestMapping("/api/rrhh")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    /**
     * POST /api/rrhh
     * Crea un nuevo empleado en el sistema
     *
     * @param dto DTO con los datos del empleado a crear
     * @return ResponseEntity con el empleado creado y código 201 (CREATED)
     *
     * Ejemplo de solicitud:
     * {
     *     "identificacion": 1234567890,
     *     "tipoIdentificacion": "CEDULA",
     *     "nombre": "Juan",
     *     "apellido": "Pérez",
     *     "usuario": "jperez",
     *     "password": "password123",
     *     "estado": "ACTIVO",
     *     "roles": ["RRHH", "VENTAS"]
     * }
     */
    @PostMapping
    public ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody CrearActualizarEmpleadoDTO dto) {
        EmpleadoDTO empleadoCreado = empleadoService.crearEmpleado(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoCreado);
    }

    /**
     * GET /api/rrhh
     * Obtiene la lista de todos los empleados del sistema
     *
     * @return ResponseEntity con la lista de empleados y código 200 (OK)
     */
    @GetMapping
    public ResponseEntity<List<EmpleadoDTO>> obtenerTodosLosEmpleados() {
        List<EmpleadoDTO> empleados = empleadoService.obtenerTodosLosEmpleados();
        return ResponseEntity.ok(empleados);
    }

    /**
     * GET /api/rrhh/:id
     * Obtiene un empleado específico por su identificación
     *
     * @param id identificación del empleado
     * @return ResponseEntity con el empleado encontrado y código 200 (OK)
     * @throws RecursoNoEncontradoException si el empleado no existe (404)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@PathVariable Long id) {
        EmpleadoDTO empleado = empleadoService.obtenerEmpleadoPorId(id);
        return ResponseEntity.ok(empleado);
    }

    /**
     * PUT /api/rrhh/:username
     * Actualiza un empleado existente
     *
     * @param usuario usuario del empleado a actualizar
     * @param dto DTO con los datos actualizados
     * @return ResponseEntity con el empleado actualizado y código 200 (OK)
     * @throws RecursoNoEncontradoException si el empleado no existe (404)
     * @throws DatosNoValidosException si los datos no son válidos (400)
     *
     * Ejemplo de solicitud:
     * {
     *     "identificacion": 1234567890,
     *     "tipoIdentificacion": "CEDULA",
     *     "nombre": "Juan Carlos",
     *     "apellido": "Pérez López",
     *     "usuario": "jperez",
     *     "password": "nuevoPassword123",
     *     "estado": "ACTIVO",
     *     "roles": ["RRHH"]
     * }
     */
    @PutMapping("/{usuario}")
    public ResponseEntity<EmpleadoDTO> actualizarEmpleado(
            @PathVariable String usuario,
            @RequestBody CrearActualizarEmpleadoDTO dto) {
        EmpleadoDTO empleadoActualizado = empleadoService.actualizarEmpleado(usuario, dto);
        return ResponseEntity.ok(empleadoActualizado);
    }

    /**
     * DELETE /api/rrhh/:id
     * Elimina un empleado del sistema
     *
     * @param id identificación del empleado a eliminar
     * @return ResponseEntity con código 204 (NO_CONTENT)
     * @throws RecursoNoEncontradoException si el empleado no existe (404)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        empleadoService.eliminarEmpleado(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/rrhh/health
     * Endpoint de salud para verificar que el servicio está funcionando
     *
     * @return ResponseEntity con un mensaje de estado
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Servicio de Empleados disponible");
    }
}
