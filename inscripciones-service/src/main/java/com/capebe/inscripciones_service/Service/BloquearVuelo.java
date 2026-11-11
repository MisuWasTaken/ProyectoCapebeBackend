package com.capebe.inscripciones_service.Service;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;

@Component
public class BloquearVuelo {

    private final JdbcTemplate jdbcTemplate;

    public BloquearVuelo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void bloquear(Long vueloId) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            try (PreparedStatement statement = connection.prepareStatement("SELECT pg_advisory_xact_lock(?)")) {
                statement.setLong(1, Math.addExact(5_000_000_000L, vueloId));
                statement.execute();
            }
            return null;
        });
    }
}
