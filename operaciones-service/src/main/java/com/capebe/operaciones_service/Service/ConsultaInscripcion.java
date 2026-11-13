package com.capebe.operaciones_service.Service;

import com.capebe.operaciones_service.Exception.InscripcionNoValidaException;
import com.capebe.operaciones_service.Exception.SupabaseStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class ConsultaInscripcion {

    private final RestClient restClient;

    public ConsultaInscripcion(@Value("${inscripciones.service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        this.restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public void validarInscripcion(Long vueloId, Long inscripcionId) {
        try {
            // Consulta al microservicio de inscripciones (puerto 8082)
            Object inscripcion = restClient.get()
                    .uri("/api/v1/inscripciones/{id}", inscripcionId)
                    .retrieve()
                    .body(Object.class);

            if (inscripcion == null) {
                throw new InscripcionNoValidaException("El servicio de inscripciones no devolvió información.");
            }
        } catch (HttpClientErrorException.NotFound e) {
            throw new InscripcionNoValidaException("El pasajero no se encuentra inscrito (Inscripción no encontrada).");
        } catch (RestClientException e) {
            throw new SupabaseStorageException("No fue posible comunicarse con el servicio de inscripciones (puerto 8082).");
        }
    }
}
