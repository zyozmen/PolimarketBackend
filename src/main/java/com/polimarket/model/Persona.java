package com.polimarket.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Persona - Clase abstracta base para todas las personas en el sistema
 * Contiene información común como identificación, tipo de identificación, nombre y apellido
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_identificacion", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Persona {

    /**
     * Identificación única de la persona (Cédula, RUC, Pasaporte, etc.)
     */
    @Id
    @Column(name = "identificacion")
    private Long identificacion;

    /**
     * Tipo de identificación de la persona
     * Valores posibles: CEDULA, RUC, PASAPORTE, etc.
     */
    @Column(name = "tipo_identificacion", insertable = false, updatable = false)
    private String tipoIdentificacion;

    /**
     * Nombre completo de la persona
     */
    @Column(nullable = false)
    private String nombre;

    /**
     * Apellido de la persona
     */
    @Column(nullable = false)
    private String apellido;
}
