package com.studymate.repository;

import com.studymate.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {

    // Buscar todas las materias de un usuario
    List<Materia> findByUsuarioId(Long usuarioId);

    // Buscar materia por nombre y usuario
    Optional<Materia> findByNombreAndUsuarioId(String nombre, Long usuarioId);

    // Verificar si existe una materia con ese nombre para el usuario
    boolean existsByNombreAndUsuarioId(String nombre, Long usuarioId);
}