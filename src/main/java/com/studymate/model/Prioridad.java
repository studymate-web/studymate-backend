package com.studymate.model;

/**
 * Enum que representa los niveles de prioridad para las tareas
 */
public enum Prioridad {
    BAJA("Baja prioridad"),
    MEDIA("Prioridad media"),
    ALTA("Alta prioridad"),
    URGENTE("Prioridad urgente");

    private final String descripcion;

    Prioridad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
