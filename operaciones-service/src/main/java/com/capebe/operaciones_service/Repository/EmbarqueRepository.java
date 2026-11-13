package com.capebe.operaciones_service.Repository;

import com.capebe.operaciones_service.Entity.Embarque;
import com.capebe.operaciones_service.Entity.EstadoEmbarque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmbarqueRepository extends JpaRepository<Embarque, Long> {

    Optional<Embarque> findByVueloIdAndInscripcionId(Long vueloId, Long inscripcionId);

    List<Embarque> findByVueloId(Long vueloId);

    long countByVueloIdAndEstado(Long vueloId, EstadoEmbarque estado);
}