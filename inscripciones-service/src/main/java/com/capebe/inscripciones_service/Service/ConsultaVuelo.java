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

import java.time.Duration;

@Component
public class ConsultaVuelo {

    private final RestClient restClient;

    public ConsultaVuelo(@Value("${vuelos.service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        restClient = RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }

    public DatosVuelo obtenerPorId(Long id) {
        try {
            DatosVuelo vuelo = restClient.get().uri("/api/v1/vuelos/{id}", id)
                    .retrieve().body(DatosVuelo.class);

            if (vuelo == null) {
                throw new ServicioVuelosException("El servicio de vuelos no devolvio informacion.");
            }
            return vuelo;
        } catch (HttpClientErrorException.NotFound e) {
            throw new NoEncontradoException("Vuelo no encontrado.");
        } catch (RestClientException e) {
            throw new ServicioVuelosException("No fue posible consultar el servicio de vuelos.");
        }
    }
}
