package com.polimarket.model;

/**
 * Enumeración de tipos de roles disponibles en el sistema
 * Basados en el diagrama de clases proporcionado
 */
public enum TipoRol {
    ADMIN("Administrador"),
    RRHH("Recursos Humanos"),
    VENTAS("Ventas"),
    BODEGA("Bodega"),
    PROVEEDORES("Proveedores"),
    ENTREGAS("Entregas");

    private final String descripcion;

    TipoRol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
