package com.polimarket.model;

/**
 * Enumeración de los posibles estados de un empleado
 */
public enum EstadoEmpleado {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    SUSPENDIDO("Suspendido"),
    RETIRADO("Retirado");

    private final String descripcion;

    EstadoEmpleado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
