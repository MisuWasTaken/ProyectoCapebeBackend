package com.capebe.operaciones_service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "embarques", schema = "operaciones", 
        uniqueConstraints = @UniqueConstraint(name = "uq_embarque_vuelo_inscripcion", columnNames = {"vuelo_id", "inscripcion_id"}),
        indexes = {
            @Index(name = "idx_embarque_vuelo", columnList = "vuelo_id"),
            @Index(name = "idx_embarque_inscripcion", columnList = "inscripcion_id")
        })
public class Embarque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vuelo_id", nullable = false)
    private Long vueloId;

    @Column(name = "inscripcion_id", nullable = false)
    private Long inscripcionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoEmbarque estado;

    @Column(name = "foto_url", length = 500)
    private String fotoUrl;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        updatedAt = createdAt;
        if (this.estado == null) {
            this.estado = EstadoEmbarque.PENDIENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}