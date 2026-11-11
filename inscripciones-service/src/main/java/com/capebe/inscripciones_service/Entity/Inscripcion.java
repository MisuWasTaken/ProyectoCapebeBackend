package com.capebe.inscripciones_service.Entity;

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
@Table(name = "inscripciones", schema = "inscripciones",
uniqueConstraints = @UniqueConstraint(name = "uq_inscripcion_email_vuelo", columnNames = {"email", "vuelo_id"}),
indexes = {@Index(name = "idx_inscripcion_vuelo", columnList = "vuelo_id"),
@Index(name = "idx_inscripcion_usuario", columnList = "usuario_id")})
public class Inscripcion {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(name = "usuario_id", nullable = false)
     private Long usuarioId;

     @Column(name = "vuelo_id", nullable = false)
     private Long vueloId;

     @Column(nullable = false, length = 120)
     private String nombre;

     @Column(nullable = false, length = 254)
     private String email;

     @Column(nullable = false)
     private Boolean consentimientoAceptado;

     @Column(nullable = false, updatable = false)
     private OffsetDateTime createdAt;

     @Column(nullable = false)
     private OffsetDateTime updatedAt;

     @PrePersist
     public void prePersist() {
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        updatedAt = createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
