
package com.capebe.operaciones_service.Service;

import com.capebe.operaciones_service.Exception.InscripcionNoValidaException;
import com.capebe.operaciones_service.Exception.ServicioInscripcionesException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.util.Map;

@Component
public class ConsultaInscripcion {

    private final RestClient restClient;

    public ConsultaInscripcion(@Value("${inscripciones.service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public void validarInscripcion(Long vueloId, Long inscripcionId) {

        ServletRequestAttributes contexto =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String token = contexto == null ? null :
                contexto.getRequest().getHeader("Authorization");

        if (token == null || token.isBlank()) {
            throw new ServicioInscripcionesException(
                    "No se recibio el token de autorizacion.", null);
        }

        try {
            Map<?, ?> inscripcion = restClient.get()
                    .uri("/api/v1/inscripciones/{id}", inscripcionId)
                    .header("Authorization", token)
                    .retrieve()
                    .body(Map.class);

            if (inscripcion == null ||
                    !(inscripcion.get("vueloId") instanceof Number id)
                    || id.longValue() != vueloId.longValue()) {
                throw new InscripcionNoValidaException(
                        "La inscripcion no corresponde al vuelo indicado.");
            }

        } catch (HttpClientErrorException.NotFound e) {
            throw new InscripcionNoValidaException(
                    "La inscripcion no existe.");

        } catch (RestClientException e) {
            System.err.println("Error al consultar inscripciones: " + e.getMessage());

            throw new ServicioInscripcionesException(
                    "No fue posible consultar el servicio de inscripciones.", e);
        }
    }
}
