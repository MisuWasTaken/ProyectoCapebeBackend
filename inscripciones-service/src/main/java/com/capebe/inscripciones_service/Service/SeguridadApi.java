package com.capebe.inscripciones_service.Service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Component
public class SeguridadApi extends OncePerRequestFilter {

    private final RestClient restClient;

    public SeguridadApi(@Value("${supabase.url}") String url,
                        @Value("${supabase.publishable.key}") String apiKey) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        restClient = RestClient.builder()
                .baseUrl(url)
                .requestFactory(factory)
                .defaultHeader("apikey", apiKey)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        String metodo = request.getMethod();

        if ("GET".equals(metodo) && "/actuator/health".equals(ruta)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ") || token.substring(7).isBlank()) {
            response.sendError(401, "Debes iniciar sesion.");
            return;
        }

        Perfil perfil;
        try {
            Usuario usuario = restClient.get()
                    .uri("/auth/v1/user")
                    .header("Authorization", token)
                    .retrieve().body(Usuario.class);

            if (usuario == null || usuario.id() == null) {
                response.sendError(401, "Sesion no valida.");
                return;
            }

            Perfil[] perfiles = restClient.get()
                    .uri("/rest/v1/perfiles?select=id,rol&id=eq.{id}", usuario.id())
                    .header("Authorization", token)
                    .retrieve().body(Perfil[].class);

            if (perfiles == null || perfiles.length != 1 ||
                    !usuario.id().equals(perfiles[0].id())) {
                response.sendError(403, "No se encontro un perfil autorizado.");
                return;
            }
            perfil = perfiles[0];
        } catch (RestClientResponseException e) {
            int codigo = e.getStatusCode().value();
            if (codigo == 400 || codigo == 401 || codigo == 403) {
                response.sendError(401, "Sesion no valida.");
            } else {
                response.sendError(503, "No se pudo consultar Supabase.");
            }
            return;
        } catch (RestClientException e) {
            response.sendError(503, "No se pudo consultar Supabase.");
            return;
        }

        if ((!"Administrador".equals(perfil.rol()) && !"Invitado".equals(perfil.rol())) ||
                !permitido(metodo, ruta, perfil.rol())) {
            response.sendError(403, "No tienes permisos para esta operacion.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean permitido(String metodo, String ruta, String rol) {
        if ("GET".equals(metodo) &&
                ruta.matches("/api/v1/inscripciones/vuelo/[0-9]+/cantidad")) {
            return true;
        }
        if (("POST".equals(metodo) && ruta.equals("/api/v1/inscripciones")) ||
                ("GET".equals(metodo) && ruta.equals("/api/v1/inscripciones/usuario"))) {
            return "Invitado".equals(rol);
        }
        return "Administrador".equals(rol);
    }

    public record Usuario(UUID id) {}
    public record Perfil(UUID id, String rol) {}
}
