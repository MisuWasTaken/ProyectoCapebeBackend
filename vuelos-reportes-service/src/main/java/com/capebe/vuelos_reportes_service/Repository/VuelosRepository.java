package com.capebe.vuelos_reportes_service.Repository;

import com.capebe.vuelos_reportes_service.Entity.EstadoVuelo;
import com.capebe.vuelos_reportes_service.Entity.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VuelosRepository extends JpaRepository<Vuelo, Long> {

    long countByEstado(EstadoVuelo estado);
}