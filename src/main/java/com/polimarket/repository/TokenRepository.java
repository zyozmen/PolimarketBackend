package com.polimarket.repository;

import com.polimarket.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositorio para la entidad Token
 * Proporciona métodos de acceso a datos para tokens JWT
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    /**
     * Busca un token activo por su valor
     *
     * @param valor valor del token JWT
     * @return Optional con el token si existe y está activo
     */
    Optional<Token> findByValorAndActivoTrue(String valor);

    /**
     * Busca tokens expirados
     *
     * @param fechaActual fecha y hora actual para comparar
     * @return lista de tokens expirados
     */
    java.util.List<Token> findByExpiracionBeforeAndActivoTrue(LocalDateTime fechaActual);

    /**
     * Desactiva todos los tokens de un empleado
     *
     * @param empleadoId ID del empleado
     */
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Token t SET t.activo = false WHERE t.empleado.identificacion = :empleadoId")
    void desactivarTokensDeEmpleado(Long empleadoId);
}
