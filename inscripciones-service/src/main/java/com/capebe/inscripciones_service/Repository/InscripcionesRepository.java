package com.capebe.inscripciones_service.Repository;

import com.capebe.inscripciones_service.Entity.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InscripcionesRepository extends JpaRepository<Inscripcion, Long> {

    boolean existsByEmailIgnoreCaseAndVueloId(String email, Long vueloId);
    long countByVueloId(Long vueloId);
    List<Inscripcion> findByVueloIdOrderByIdAsc(Long vueloId);
    List<Inscripcion> findByUsuarioIdOrderByIdAsc(Long usuarioId);
    List<Inscripcion> findByEmailIgnoreCaseOrderByIdAsc(String email);
}
