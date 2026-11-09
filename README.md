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

Actualmente se encuentra funcionando el schema de vuelos.

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

## Pruebas

El funcionamiento del microservicio fue probado mediante Postman.
Se comprobaron casos como:

- Creacion correcta de vuelos.
- Busqueda de vuelos.
- Cambio de estado.
- Generacion del reporte.
- Cupos invalidos.
- Fechas pasadas.
- Vuelos inexistentes.
- Estados incorrectos.

Tambien se implementaron pruebas unitarias utilizando JUnit y Mockito.
Actualmente las pruebas se ejecutan correctamente con:

`BUILD SUCCESS`

## Ejecucion

PostgreSQL se ejecuta mediante Docker.
Para iniciar la base de datos:

`docker compose up -d`

Luego, dentro de `vuelos-reportes-service`, se inicia Spring Boot con:

`.\mvnw.cmd spring-boot:run`

El servicio queda disponible localmente en:

`http://localhost:8081`

## Estado del proyecto

Actualmente se encuentra terminado el primer microservicio de vuelos y reportes.
Los siguientes pasos son desarrollar:

- inscripciones-service
- operaciones-service

Despues se conectara el backend con la aplicacion Android mediante Retrofit.
Finalmente, los microservicios seran desplegados en la nube y las fotografias de los pasajeros dejaran de almacenarse localmente.