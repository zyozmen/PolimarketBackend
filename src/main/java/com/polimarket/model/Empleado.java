package com.polimarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad Empleado - Representa un empleado del sistema
 * Hereda de Persona y añade información específica de empleo
 */
@Entity
@DiscriminatorValue("EMPLEADO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado extends Persona {

    /**
     * Nombre de usuario único para login
     */
    @Column(unique = true, nullable = false)
    private String usuario;

    /**
     * Contraseña del empleado (en producción debe estar hasheada)
     */
    @Column(nullable = false)
    private String password;

    /**
     * Estado del empleado
     * Valores posibles: ACTIVO, INACTIVO, SUSPENDIDO, etc.
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoEmpleado estado;

    /**
     * Lista de roles asignados al empleado
     * Relación many-to-many con la entidad Rol
     */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "empleado_rol",
            joinColumns = @JoinColumn(name = "empleado_id"),
            inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private java.util.Set<Rol> rolList = new java.util.HashSet<>();

    /**
     * Fecha de creación del registro del empleado
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de última actualización del registro
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
