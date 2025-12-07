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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar operaciones de Empleado
 * Implementa la lógica de negocio para las operaciones CRUD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;

    /**
     * Crea un nuevo empleado en el sistema
     *
     * @param dto DTO con los datos del empleado a crear
     * @return DTO del empleado creado
     * @throws DatosNoValidosException si los datos no son válidos
     */
    public EmpleadoDTO crearEmpleado(CrearActualizarEmpleadoDTO dto) {
        validarDatosEmpleado(dto);

        // Verificar que el usuario no exista
        if (empleadoRepository.existsByUsuario(dto.getUsuario())) {
            throw new DatosNoValidosException(
                    "El usuario '" + dto.getUsuario() + "' ya existe en el sistema"
            );
        }

        Empleado empleado = new Empleado();
        empleado.setIdentificacion(dto.getIdentificacion());
        empleado.setTipoIdentificacion(dto.getTipoIdentificacion());
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setUsuario(dto.getUsuario());
        empleado.setPassword(dto.getPassword()); // En producción, hashear la contraseña
        empleado.setEstado(EstadoEmpleado.valueOf(dto.getEstado()));

        // Asignar roles
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            Set<Rol> roles = asignarRoles(dto.getRoles());
            empleado.setRolList(roles);
        }

        Empleado empleadoGuardado = empleadoRepository.save(empleado);
        return convertirADTO(empleadoGuardado);
    }

    /**
     * Obtiene todos los empleados del sistema
     *
     * @return Lista de DTOs de empleados
     */
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerTodosLosEmpleados() {
        return empleadoRepository.findAll()
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene un empleado por su identificación
     *
     * @param id identificación del empleado
     * @return DTO del empleado
     * @throws RecursoNoEncontradoException si el empleado no existe
     */
    @Transactional(readOnly = true)
    public EmpleadoDTO obtenerEmpleadoPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Empleado con identificación " + id + " no encontrado"
                ));
        return convertirADTO(empleado);
    }

    /**
     * Actualiza un empleado existente
     *
     * @param usuario usuario del empleado a actualizar
     * @param dto DTO con los datos actualizados
     * @return DTO del empleado actualizado
     * @throws RecursoNoEncontradoException si el empleado no existe
     * @throws DatosNoValidosException si los datos no son válidos
     */
    public EmpleadoDTO actualizarEmpleado(String usuario, CrearActualizarEmpleadoDTO dto) {
        validarDatosEmpleado(dto);

        Empleado empleado = empleadoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Empleado con usuario '" + usuario + "' no encontrado"
                ));

        // Actualizar datos personales
        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setPassword(dto.getPassword()); // En producción, hashear la contraseña
        empleado.setEstado(EstadoEmpleado.valueOf(dto.getEstado()));

        // Actualizar roles
        if (dto.getRoles() != null) {
            Set<Rol> roles = asignarRoles(dto.getRoles());
            empleado.setRolList(roles);
        }

        Empleado empleadoActualizado = empleadoRepository.save(empleado);
        return convertirADTO(empleadoActualizado);
    }

    /**
     * Elimina un empleado del sistema
     *
     * @param id identificación del empleado a eliminar
     * @throws RecursoNoEncontradoException si el empleado no existe
     */
    public void eliminarEmpleado(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Empleado con identificación " + id + " no encontrado"
                ));
        empleadoRepository.delete(empleado);
    }

    /**
     * Valida que los datos del empleado sean correctos
     *
     * @param dto DTO a validar
     * @throws DatosNoValidosException si algún campo es inválido
     */
    private void validarDatosEmpleado(CrearActualizarEmpleadoDTO dto) {
        if (dto.getIdentificacion() == null || dto.getIdentificacion() <= 0) {
            throw new DatosNoValidosException("La identificación debe ser un número positivo");
        }

        if (dto.getTipoIdentificacion() == null || dto.getTipoIdentificacion().isBlank()) {
            throw new DatosNoValidosException("El tipo de identificación no puede estar vacío");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new DatosNoValidosException("El nombre no puede estar vacío");
        }

        if (dto.getApellido() == null || dto.getApellido().isBlank()) {
            throw new DatosNoValidosException("El apellido no puede estar vacío");
        }

        if (dto.getUsuario() == null || dto.getUsuario().isBlank()) {
            throw new DatosNoValidosException("El usuario no puede estar vacío");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new DatosNoValidosException("La contraseña no puede estar vacía");
        }

        if (dto.getPassword().length() < 6) {
            throw new DatosNoValidosException("La contraseña debe tener al menos 6 caracteres");
        }

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new DatosNoValidosException("El estado no puede estar vacío");
        }

        // Validar que el estado es válido
        try {
            EstadoEmpleado.valueOf(dto.getEstado());
        } catch (IllegalArgumentException e) {
            throw new DatosNoValidosException(
                    "Estado inválido. Estados válidos: ACTIVO, INACTIVO, SUSPENDIDO, RETIRADO"
            );
        }
    }

    /**
     * Asigna roles al empleado basado en los nombres proporcionados
     *
     * @param rolesNombres conjunto de nombres de roles
     * @return conjunto de entidades Rol
     */
    private Set<Rol> asignarRoles(Set<String> rolesNombres) {
        Set<Rol> roles = new HashSet<>();

        for (String nombreRol : rolesNombres) {
            try {
                TipoRol tipoRol = TipoRol.valueOf(nombreRol.toUpperCase());
                Rol rol = rolRepository.findByNombre(tipoRol)
                        .orElseGet(() -> {
                            Rol nuevoRol = new Rol(tipoRol);
                            return rolRepository.save(nuevoRol);
                        });
                roles.add(rol);
            } catch (IllegalArgumentException e) {
                throw new DatosNoValidosException(
                        "Rol inválido: '" + nombreRol + "'. Roles válidos: ADMIN, RRHH, VENTAS, BODEGA, PROVEEDORES, ENTREGAS"
                );
            }
        }

        return roles;
    }

    /**
     * Convierte una entidad Empleado a su DTO
     *
     * @param empleado entidad a convertir
     * @return DTO del empleado
     */
    private EmpleadoDTO convertirADTO(Empleado empleado) {
        Set<String> rolesNombres = empleado.getRolList()
                .stream()
                .map(rol -> rol.getNombre().name())
                .collect(Collectors.toSet());

        return new EmpleadoDTO(
                empleado.getIdentificacion(),
                empleado.getTipoIdentificacion(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getUsuario(),
                empleado.getEstado().name(),
                rolesNombres,
                empleado.getFechaCreacion(),
                empleado.getFechaActualizacion()
        );
    }
}
