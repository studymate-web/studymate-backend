package com.studymate.service;

import com.studymate.dto.MateriaDTO;
import com.studymate.model.Materia;
import com.studymate.model.Usuario;
import com.studymate.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para manejar las operaciones relacionadas con las materias
 */
@Service
@RequiredArgsConstructor
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final UsuarioService usuarioService;

    /**
     * Crea una nueva materia para un usuario
     * @param materiaDTO Datos de la materia
     * @param usuarioId ID del usuario propietario
     * @return Materia creada
     */
    public Materia crearMateria(MateriaDTO materiaDTO, Long usuarioId) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar que no existe una materia con el mismo nombre
        if (materiaRepository.existsByNombreAndUsuarioId(materiaDTO.getNombre(), usuarioId)) {
            throw new RuntimeException("Ya existe una materia con ese nombre");
        }

        Materia materia = convertirDTOaEntidad(materiaDTO);
        materia.setUsuario(usuario);
        return materiaRepository.save(materia);
    }

    /**
     * Busca una materia por su ID
     * @param id ID de la materia
     * @return Optional con la materia si existe
     */
    public Optional<Materia> buscarPorId(Long id) {
        return materiaRepository.findById(id);
    }

    /**
     * Busca todas las materias de un usuario
     * @param usuarioId ID del usuario
     * @return Lista de materias del usuario
     */
    public List<Materia> buscarPorUsuario(Long usuarioId) {
        return materiaRepository.findByUsuarioId(usuarioId);
    }

    /**
     * Actualiza una materia existente
     * @param materiaDTO Datos actualizados de la materia
     * @return Materia actualizada
     */
    public Materia actualizarMateria(MateriaDTO materiaDTO) {
        if (!materiaRepository.existsById(materiaDTO.getId())) {
            throw new RuntimeException("Materia no encontrada");
        }
        
        Materia materia = convertirDTOaEntidad(materiaDTO);
        return materiaRepository.save(materia);
    }

    /**
     * Elimina una materia
     * @param id ID de la materia a eliminar
     * @param usuarioId ID del usuario propietario
     */
    public void eliminarMateria(Long id, Long usuarioId) {
        Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

        // Verificar que la materia pertenece al usuario
        if (!materia.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permisos para eliminar esta materia");
        }

        materiaRepository.delete(materia);
    }

    /**
     * Convierte un DTO a entidad Materia
     * @param dto DTO con los datos de la materia
     * @return Entidad Materia
     */
    private Materia convertirDTOaEntidad(MateriaDTO dto) {
        Materia materia = new Materia();
        materia.setId(dto.getId());
        materia.setNombre(dto.getNombre());
        materia.setCodigo(dto.getCodigo());
        materia.setDescripcion(dto.getDescripcion());
        materia.setCreditos(dto.getCreditos());
        materia.setColor(dto.getColor());
        return materia;
    }
}