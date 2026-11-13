package com.capebe.operaciones_service.Service;

import com.capebe.operaciones_service.Exception.SupabaseStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@Service
public class SupabaseService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key}")
    private String supabaseKey;

    @Value("${supabase.bucket-name}")
    private String bucketName;

    private final RestTemplate restTemplate = new RestTemplate();

    public String subirFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debes seleccionar una foto.");
        }

        String tipo = foto.getContentType();
        if (!MediaType.IMAGE_JPEG_VALUE.equals(tipo) && !MediaType.IMAGE_PNG_VALUE.equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten fotos en formato JPG o PNG.");
        }
        if (foto.getSize() > 5L * 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La foto no puede superar los 5 MB.");
        }
        if (supabaseUrl.isBlank() || supabaseKey.isBlank() || bucketName.isBlank()) {
            throw new SupabaseStorageException("Supabase no esta configurado.");
        }

        String extension = MediaType.IMAGE_PNG_VALUE.equals(tipo) ? ".png" : ".jpg";
        String nombreArchivo = UUID.randomUUID() + extension;
        String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + nombreArchivo;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(supabaseKey);
            headers.set("apikey", supabaseKey);
            headers.setContentType(MediaType.parseMediaType(tipo));

            HttpEntity<byte[]> peticion = new HttpEntity<>(foto.getBytes(), headers);
            ResponseEntity<String> respuesta = restTemplate.exchange(url, HttpMethod.POST, peticion, String.class);

            if (!respuesta.getStatusCode().is2xxSuccessful()) {
                throw new SupabaseStorageException("Error al subir la foto a Supabase Storage.");
            }
            return nombreArchivo;
        } catch (IOException | RestClientException e) {
            throw new SupabaseStorageException("No fue posible subir la foto a Supabase Storage.", e);
        }
    }
}
