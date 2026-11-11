# Proyecto CAPEBE Backend
Backend del proyecto CAPEBE, desarrollado para gestionar la informacion de los vuelos recreativos de la aplicacion movil.
La idea del proyecto es separar la aplicacion Android del manejo de los datos y la logica principal, utilizando microservicios desarrollados con Spring Boot.

## Integrantes

- Francisco Riveros
- Victoria Rivera

## Tecnologias utilizadas

- Java
- Spring Boot
- PostgreSQL
- Docker
- Maven
- JUnit
- Mockito
- Postman

## Base de datos

El proyecto utiliza una sola base de datos PostgreSQL llamada `capebe_db`.
Dentro de esta base se crearon tres schemas para organizar la informacion de cada microservicio:

- vuelos
- inscripciones
- operaciones

Actualmente se encuentran funcionando los schemas de vuelos e inscripciones.

## Microservicio de vuelos y reportes

El primer microservicio desarrollado es `vuelos-reportes-service`.

Este permite:

- Crear vuelos.
- Ver todos los vuelos registrados.
- Buscar un vuelo por su ID.
- Cambiar el estado de un vuelo.
- Generar un reporte general de vuelos.
- Validar fechas, horarios y cupos.
- Controlar errores al ingresar datos incorrectos.

Los estados disponibles para un vuelo son:

- DISPONIBLE
- CERRADO
- FINALIZADO
- CANCELADO

Los vuelos utilizan un ID autoincremental, comenzando desde 1.

## Microservicio de inscripciones

El segundo microservicio desarrollado es `inscripciones-service`.

Este permite:

- Inscribir pasajeros en los vuelos disponibles.
- Ver los pasajeros inscritos en un vuelo.
- Consultar las inscripciones de un usuario.
- Contar los pasajeros inscritos en un vuelo.
- Cambiar una inscripcion a otro vuelo.
- Evitar inscripciones duplicadas.
- Comprobar que existan cupos y que el vuelo este disponible.
- Validar que el pasajero acepte el consentimiento.
- Controlar errores al ingresar datos incorrectos.

Este microservicio consulta al de vuelos para comprobar la informacion antes de registrar una inscripcion.
Ambos utilizan la misma base de datos, pero cada uno trabaja con su propio schema.

## Pruebas

El funcionamiento de ambos microservicios fue probado mediante Postman.
En vuelos se comprobaron casos como:

- Creacion correcta de vuelos.
- Busqueda de vuelos.
- Cambio de estado.
- Generacion del reporte.
- Cupos invalidos.
- Fechas pasadas.
- Vuelos inexistentes.
- Estados incorrectos.

En inscripciones se comprobaron casos como:

- Creacion y consulta de inscripciones.
- Inscripciones duplicadas.
- Consentimiento no aceptado.
- Vuelos sin cupos o no disponibles.
- Cambio de vuelo.
- Cantidad de pasajeros inscritos.
- Datos incorrectos.
- Caida del servicio de vuelos, devolviendo un error controlado.

Tambien se implementaron pruebas unitarias utilizando JUnit y Mockito en ambos microservicios.
Actualmente las pruebas funcionaron correctamente, arrojando el resultado de:

- Vuelos y reportes: 8 pruebas correctas.
- Inscripciones: 12 pruebas correctas.

## Ejecucion

PostgreSQL se ejecuta mediante Docker.
Para iniciar la base de datos:

`docker compose up -d`

Luego, dentro de cada microservicio, se inicia Spring Boot con:

`.\mvnw.cmd spring-boot:run`

Los servicios quedan disponibles localmente en:

- Vuelos y reportes: `http://localhost:8081`
- Inscripciones: `http://localhost:8082`

Para probar las inscripciones es necesario tener ambos microservicios funcionando.

## Estado del proyecto

Actualmente se encuentran terminados y probados localmente los dos primeros microservicios:

- vuelos-reportes-service
- inscripciones-service

Finalmente se conectara el backend con la aplicacion Android mediante Retrofit.
Y luego los microservicios seran desplegados en la nube y las fotografias de los pasajeros dejaran de almacenarse localmente.