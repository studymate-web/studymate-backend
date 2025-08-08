package com.studymate.service;

import com.studymate.model.Nota;
import com.studymate.model.Usuario;
import com.studymate.model.Materia;
import com.studymate.repository.NotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar las operaciones relacionadas con las notas
 */
@Service
@RequiredArgsConstructor
public class NotaService {

    private final NotaRepository notaRepository;
    private final UsuarioService usuarioService;
    private final MateriaService materiaService;

    /**
     * Crea una nueva nota para un usuario
     * @param nota Datos de la nota
     * @param usuarioId ID del usuario propietario
     * @return Nota creada
     */
    public Nota crearNota(Nota nota, Long usuarioId) {
        configurarNota(nota, usuarioId);
        return notaRepository.save(nota);
    }

    /**
     * Busca una nota por su ID
     * @param id ID de la nota
     * @return Optional con la nota si existe
     */
    public Optional<Nota> buscarPorId(Long id) {
        return notaRepository.findById(id);
    }

    /**
     * Busca todas las notas de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de notas del usuario
     */
    public List<Nota> buscarPorUsuario(Long usuarioId) {
        return notaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Busca notas de una materia específica
     * @param materiaId ID de la materia
     * @param usuarioId ID del usuario
     * @return Lista de notas de la materia
     */
    public List<Nota> buscarPorMateria(Long materiaId, Long usuarioId) {
        return notaRepository.findByMateriaIdAndUsuarioId(materiaId, usuarioId);
    }

    /**
     * Busca notas generales (sin materia)
     * @param usuarioId ID del usuario
     * @return Lista de notas generales
     */
    public List<Nota> buscarNotasGenerales(Long usuarioId) {
        return notaRepository.findByUsuarioIdAndMateriaIsNull(usuarioId);
    }

    /**
     * Busca notas por título
     * @param usuarioId ID del usuario
     * @param titulo Título a buscar
     * @return Lista de notas que coinciden
     */
    public List<Nota> buscarPorTitulo(Long usuarioId, String titulo) {
        return notaRepository.findByUsuarioIdAndTituloContainingIgnoreCase(usuarioId, titulo);
    }

    /**
     * Actualiza una nota existente
     * @param nota Datos actualizados de la nota
     * @param usuarioId ID del usuario propietario
     * @return Nota actualizada
     */
    public Nota actualizarNota(Nota nota, Long usuarioId) {
        Nota notaExistente = notaRepository.findById(nota.getId())
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        // Verificar que la nota pertenece al usuario
        if (!notaExistente.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para editar esta nota");
        }

        return notaRepository.save(nota);
    }

    /**
     * Elimina una nota
     * @param id ID de la nota a eliminar
     * @param usuarioId ID del usuario propietario
     */
    public void eliminarNota(Long id, Long usuarioId) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nota no encontrada"));

        // Verificar que la nota pertenece al usuario
        if (!nota.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para eliminar esta nota");
        }

        notaRepository.delete(nota);
    }

    /**
     * Configura una nota con el usuario y materia correspondientes
     * @param nota Nota a configurar
     * @param usuarioId ID del usuario propietario
     */
    private void configurarNota(Nota nota, Long usuarioId) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        nota.setUsuario(usuario);

        // Si tiene materia, verificar que existe y pertenece al usuario
        if (nota.getMateria() != null && nota.getMateria().getId() != null) {
            Materia materia = materiaService.buscarPorId(nota.getMateria().getId())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            if (!materia.getUsuario().getId().equals(usuarioId)) {
                throw new RuntimeException("La materia no pertenece al usuario");
            }

            nota.setMateria(materia);
        }
    }
}