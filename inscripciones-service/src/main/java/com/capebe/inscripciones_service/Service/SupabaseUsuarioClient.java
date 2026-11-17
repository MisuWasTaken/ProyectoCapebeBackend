
package com.capebe.inscripciones_service.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class SupabaseUsuarioClient {

    private final RestClient restClient;

    public SupabaseUsuarioClient(
            @Value("${supabase.url}") String url,
            @Value("${supabase.publishable.key}") String apiKey
    ) {
        restClient = RestClient.builder()
                .baseUrl(url)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    public UsuarioAutenticado autenticar(String autorizacion) {
        if (autorizacion == null || !autorizacion.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Debes iniciar sesion."
            );
        }

        try {
            UsuarioAutenticado usuario = restClient.get()
                    .uri("/auth/v1/user")
                    .header("Authorization", autorizacion)
                    .retrieve()
                    .body(UsuarioAutenticado.class);

            if (usuario == null || usuario.id() == null || usuario.email() == null) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Sesion no valida."
                );
            }

            return usuario;

        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Sesion no valida."
            );
        } catch (RestClientException e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo consultar Supabase Auth."
            );
        }
    }

    public record UsuarioAutenticado(UUID id, String email) {
    }
}
