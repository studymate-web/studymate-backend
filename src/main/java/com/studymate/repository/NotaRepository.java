package com.studymate.repository;

import com.studymate.model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    // Buscar todas las notas de un usuario
    List<Nota> findByUsuarioId(Long usuarioId);

    // Buscar notas de una materia específica
    List<Nota> findByMateriaIdAndUsuarioId(Long materiaId, Long usuarioId);

    // Buscar notas generales (sin materia)
    List<Nota> findByUsuarioIdAndMateriaIsNull(Long usuarioId);

    // Buscar notas por título (búsqueda)
    List<Nota> findByUsuarioIdAndTituloContainingIgnoreCase(Long usuarioId, String titulo);
}