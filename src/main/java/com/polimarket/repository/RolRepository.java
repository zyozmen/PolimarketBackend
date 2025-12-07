package com.polimarket.repository;

import com.polimarket.model.Rol;
import com.polimarket.model.TipoRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad Rol
 * Proporciona métodos para operaciones CRUD en la base de datos
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    /**
     * Busca un rol por su tipo
     * @param nombre tipo de rol
     * @return Optional con el rol si existe
     */
    Optional<Rol> findByNombre(TipoRol nombre);
}
