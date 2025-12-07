package com.polimarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad Token - Representa un token JWT en el sistema
 * Almacena información sobre tokens generados para autenticación
 */
@Entity
@Table(name = "token")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    /**
     * ID autogenerado del token
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Valor del token JWT
     */
    @Column(nullable = false, length = 1000)
    private String valor;

    /**
     * Fecha y hora de expiración del token
     */
    @Column(nullable = false)
    private LocalDateTime expiracion;

    /**
     * Relación con el empleado propietario del token
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    /**
     * Fecha de creación del token
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Indica si el token está activo o ha sido revocado
     */
    @Column(nullable = false)
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
