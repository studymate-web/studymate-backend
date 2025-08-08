package com.studymate.repository;

import com.studymate.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    // Buscar todas las tareas de un usuario
    List<Tarea> findByUsuarioId(Long usuarioId);

    // Buscar tareas de una materia específica
    List<Tarea> findByMateriaIdAndUsuarioId(Long materiaId, Long usuarioId);

    // Buscar tareas generales (sin materia)
    List<Tarea> findByUsuarioIdAndMateriaIsNull(Long usuarioId);

    // Buscar tareas pendientes
    List<Tarea> findByUsuarioIdAndCompletadaFalse(Long usuarioId);

    // Buscar tareas por fecha límite
    List<Tarea> findByUsuarioIdAndFechaLimiteBetween(Long usuarioId, LocalDateTime inicio, LocalDateTime fin);

    // Buscar tareas urgentes (no completadas y fecha límite próxima)
    @Query("SELECT t FROM Tarea t WHERE t.usuario.id = :usuarioId AND t.completada = false AND t.fechaLimite <= :fecha")
    List<Tarea> findTareasUrgentes(@Param("usuarioId") Long usuarioId, @Param("fecha") LocalDateTime fecha);
}