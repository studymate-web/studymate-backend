package com.studymate.service;

import com.studymate.model.Tarea;
import com.studymate.model.Usuario;
import com.studymate.model.Materia;
import com.studymate.repository.TareaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar las operaciones relacionadas con las tareas
 */
@Service
@RequiredArgsConstructor
public class TareaService {

    private final TareaRepository tareaRepository;
    private final UsuarioService usuarioService;
    private final MateriaService materiaService;

    /**
     * Crea una nueva tarea para un usuario
     * @param tarea Datos de la tarea
     * @param usuarioId ID del usuario propietario
     * @return Tarea creada
     */
    public Tarea crearTarea(Tarea tarea, Long usuarioId) {
        configurarTarea(tarea, usuarioId);
        return tareaRepository.save(tarea);
    }

    /**
     * Busca una tarea por su ID
     * @param id ID de la tarea
     * @return Optional con la tarea si existe
     */
    public Optional<Tarea> buscarPorId(Long id) {
        return tareaRepository.findById(id);
    }

    /**
     * Busca todas las tareas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de tareas del usuario
     */
    public List<Tarea> buscarPorUsuario(Long usuarioId) {
        return tareaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Busca tareas de una materia específica
     * @param materiaId ID de la materia
     * @param usuarioId ID del usuario
     * @return Lista de tareas de la materia
     */
    public List<Tarea> buscarPorMateria(Long materiaId, Long usuarioId) {
        return tareaRepository.findByMateriaIdAndUsuarioId(materiaId, usuarioId);
    }

    /**
     * Busca tareas generales (sin materia)
     * @param usuarioId ID del usuario
     * @return Lista de tareas generales
     */
    public List<Tarea> buscarTareasGenerales(Long usuarioId) {
        return tareaRepository.findByUsuarioIdAndMateriaIsNull(usuarioId);
    }

    /**
     * Busca tareas pendientes
     * @param usuarioId ID del usuario
     * @return Lista de tareas pendientes
     */
    public List<Tarea> buscarTareasPendientes(Long usuarioId) {
        return tareaRepository.findByUsuarioIdAndCompletadaFalse(usuarioId);
    }

    /**
     * Busca tareas urgentes (próximas a vencer)
     * @param usuarioId ID del usuario
     * @return Lista de tareas urgentes
     */
    public List<Tarea> buscarTareasUrgentes(Long usuarioId) {
        LocalDateTime fechaLimite = LocalDateTime.now().plusDays(3); // 3 días
        return tareaRepository.findTareasUrgentes(usuarioId, fechaLimite);
    }

    /**
     * Marca una tarea como completada
     * @param id ID de la tarea
     * @param usuarioId ID del usuario propietario
     * @return Tarea actualizada
     */
    public Tarea marcarCompletada(Long id, Long usuarioId) {
        Tarea tarea = obtenerTareaConPermisos(id, usuarioId);
        tarea.setCompletada(true);
        return tareaRepository.save(tarea);
    }

    /**
     * Actualiza una tarea existente
     * @param tarea Datos actualizados de la tarea
     * @param usuarioId ID del usuario propietario
     * @return Tarea actualizada
     */
    public Tarea actualizarTarea(Tarea tarea, Long usuarioId) {
        obtenerTareaConPermisos(tarea.getId(), usuarioId);
        return tareaRepository.save(tarea);
    }

    /**
     * Elimina una tarea
     * @param id ID de la tarea a eliminar
     * @param usuarioId ID del usuario propietario
     */
    public void eliminarTarea(Long id, Long usuarioId) {
        Tarea tarea = obtenerTareaConPermisos(id, usuarioId);
        tareaRepository.delete(tarea);
    }

    /**
     * Configura una tarea con el usuario y materia correspondientes
     * @param tarea Tarea a configurar
     * @param usuarioId ID del usuario propietario
     */
    private void configurarTarea(Tarea tarea, Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        tarea.setUsuario(usuario);

        // Si tiene materia, verificar que existe y pertenece al usuario
        if (tarea.getMateria() != null && tarea.getMateria().getId() != null) {
            Materia materia = materiaService.buscarPorId(tarea.getMateria().getId())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            if (!materia.getUsuario().getId().equals(usuarioId)) {
                throw new RuntimeException("La materia no pertenece al usuario");
            }

            tarea.setMateria(materia);
        }
    }

    /**
     * Verifica que una tarea pertenece al usuario y la retorna
     * @param tareaId ID de la tarea
     * @param usuarioId ID del usuario
     * @return Tarea si pertenece al usuario
     * @throws RuntimeException si no tiene permisos
     */
    private Tarea obtenerTareaConPermisos(Long tareaId, Long usuarioId) {
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (!tarea.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para esta tarea");
        }

        return tarea;
    }
}