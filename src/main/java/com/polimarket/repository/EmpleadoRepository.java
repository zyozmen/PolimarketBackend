package com.polimarket.repository;

import com.polimarket.model.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad Empleado
 * Proporciona métodos para operaciones CRUD en la base de datos
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    /**
     * Busca un empleado por su nombre de usuario
     * @param usuario nombre de usuario del empleado
     * @return Optional con el empleado si existe
     */
    Optional<Empleado> findByUsuario(String usuario);

    /**
     * Verifica si existe un empleado con el usuario especificado
     * @param usuario nombre de usuario
     * @return true si existe, false en caso contrario
     */
    boolean existsByUsuario(String usuario);
}
