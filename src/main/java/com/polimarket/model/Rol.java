package com.polimarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Rol - Define los roles disponibles en el sistema
 * Valores posibles: ADMIN, RRHH, VENTAS, BODEGA, PROVEEDORES, ENTREGAS
 */
@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    /**
     * Identificador único del rol
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;

    /**
     * Nombre del rol
     */
    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoRol nombre;

    /**
     * Descripción del rol
     */
    @Column(length = 500)
    private String descripcion;

    /**
     * Constructor conveniente para crear un rol con nombre
     */
    public Rol(TipoRol nombre) {
        this.nombre = nombre;
    }
}
