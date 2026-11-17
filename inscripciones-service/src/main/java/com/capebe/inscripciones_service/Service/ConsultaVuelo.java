
package com.capebe.inscripciones_service.Service;

import com.capebe.inscripciones_service.Dto.DatosVuelo;
import com.capebe.inscripciones_service.Exception.ServicioVuelosException;
import com.capebe.inscripciones_service.Exception.NoEncontradoException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;

@Component
public class ConsultaVuelo {

    private final RestClient restClient;

    public ConsultaVuelo(@Value("${vuelos.service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));

        restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public DatosVuelo obtenerPorId(Long id) {

        ServletRequestAttributes contexto =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        String token = contexto == null ? null :
                contexto.getRequest().getHeader("Authorization");

        if (token == null || token.isBlank()) {
            System.err.println("No se recibio el token de autorizacion.");
            throw new ServicioVuelosException("No se recibio el token.");
        }

        try {
            DatosVuelo vuelo = restClient.get()
                    .uri("/api/v1/vuelos/{id}", id)
                    .header("Authorization", token)
                    .retrieve()
                    .body(DatosVuelo.class);

            if (vuelo == null) {
                throw new ServicioVuelosException(
                        "El servicio de vuelos no devolvio informacion."
                );
            }

            return vuelo;

        } catch (HttpClientErrorException.NotFound e) {
            throw new NoEncontradoException("Vuelo no encontrado.");

        } catch (RestClientResponseException e) {
            System.err.println("Error del servicio de vuelos: " + e.getStatusCode());
            throw new ServicioVuelosException(
                    "No fue posible consultar el servicio de vuelos."
            );

        } catch (RestClientException e) {
            System.err.println("Error de conexion con vuelos: " + e.getMessage());
            throw new ServicioVuelosException(
                    "No fue posible consultar el servicio de vuelos."
            );
        }
    }
}
