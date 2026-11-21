# Proyecto CAPEBE Backend

La idea del proyecto es separar la aplicación Android del manejo de los datos y la lógica principal, utilizando tres microservicios desarrollados con Spring Boot, conectados a una base de datos PostgreSQL y desplegados en la nube.

## Integrantes

- Francisco Riveros
- Victoria Rivera

## Tecnologías utilizadas

- Java 21
- Spring Boot
- PostgreSQL
- Supabase
- Docker
- Maven
- JUnit
- Mockito
- Postman
- Render

## Base de datos

El proyecto utiliza PostgreSQL, alojado en Supabase.

Para organizar la informacion de los microservicios se utilizan tres esquemas:

- `vuelos`: información de los vuelos registrados.
- `inscripciones`: inscripciones de pasajeros a los vuelos.
- `operaciones`: control operativo y participacion de pasajeros.

También se utiliza Supabase Auth para gestionar la autenticación de los usuarios y Supabase Storage para almacenar sus fotos de identificación.

Los tres microservicios trabajan sobre la misma base de datos pero de forma separada.

## Microservicio de vuelos y reportes

El primer microservicio es `vuelos-reportes-service`.

Este permite:

- Crear vuelos.
- Ver todos los vuelos registrados.
- Buscar un vuelo por su ID.
- Cambiar el estado de un vuelo.
- Generar reportes de vuelos.
- Validar fechas, horarios y cupos.
- Controlar errores al ingresar datos incorrectos.

Los estados disponibles para un vuelo son:

- DISPONIBLE
- CERRADO
- FINALIZADO
- CANCELADO

Los vuelos utilizan un ID autoincremental.

## Microservicio de inscripciones

El segundo microservicio es `inscripciones-service`.

Este permite:

- Inscribir pasajeros en vuelos disponibles.
- Consultar los pasajeros inscritos en un vuelo.
- Consultar las inscripciones de un usuario.
- Contar los pasajeros inscritos.
- Cambiar una inscripción a otro vuelo.
- Evitar inscripciones duplicadas.
- Comprobar los cupos disponibles.
- Validar el consentimiento del pasajero.
- Controlar errores al ingresar datos incorrectos.

Este microservicio se comunica con el servicio de vuelos para validar la información antes de registrar una inscripción.

## Microservicio de operaciones

El tercer microservicio es `operaciones-service`.

Este permite:

- Gestionar el control de participacion de los pasajeros.
- Consultar información de las inscripciones.
- Registrar si un pasajero asistió o no asistio.
- Actualizar la información de participación.
- Validar los datos antes de realizar las operaciones.

Este microservicio se comunica con el servicio de inscripciones para comprobar la información de los pasajeros y sus vuelos.

## Autenticación y seguridad

La aplicacion utiliza Supabase Auth para registrar y autenticar usuarios.

Una vez iniciada la sesión, Android obtiene un token de autenticación que se utiliza para realizar solicitudes protegidas a los microservicios.

El backend valida los tokens y controla el acceso a las operaciones según los permisos del usuario.

La información de autenticación se mantiene separada de la lógica de los vuelos, inscripciones y operaciones.

## Integración con Android

Los microservicios se encuentran conectados con la aplicación móvil CAPEBE.

La comunicación se realiza mediante Retrofit.

Las operaciones realizadas desde la aplicación utilizan la base de datos centralizada, permitiendo que la información se comparta entre dispositivos.

## Despliegue en la nube

Los tres microservicios están desplegados en Render y utilizan Supabase como servicio de base de datos.

Al utilizar el plan gratuito de Render, los servicios entran en reposo después de un rato de inactividad y pueden demorar en responder a las primeras solicitudes.

## Endpoints

Los microservicios exponen endpoints REST para realizar operaciones sobre los vuelos, inscripciones y participación.

- `GET`: consultar información.
- `POST`: registrar información.
- `PUT`: actualizar registros.
- `DELETE`: eliminar registros.

La aplicación Android ocupa estos servicios mediante Retrofit, enviando los datos necesarios y los tokens de autenticación correspondientes.

## Pruebas

Los microservicios fueron probados mediante Postman, comprobando tanto operaciones correctas como situaciones de error.

También se implementaron pruebas unitarias utilizando JUnit y Mockito en el backend.

## Estado del proyecto

Los tres microservicios se encuentran desarrollados y desplegados en Render, utilizando PostgreSQL en Supabase.

La aplicación Android está integrada con el backend, permitiendo gestionar vuelos, inscripciones y participación de pasajeros.

Se generó una APK firmada digitalmente para su instalación en dispositivos Android.