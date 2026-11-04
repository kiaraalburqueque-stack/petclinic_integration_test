# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2

## Laboratorio 12 - Integrante 2: SEBASTIAN - OwnerController

- Integrante: SebastianEspiritu <sebastian.espiritu@tecsup.edu.pe>.
- Rama: `SEBASTIAN`. Cuenta autenticada comprobada mediante `GET https://api.github.com/user`: `SebastianEspiritu`.
- Ejercicio 2, Grupo 1 de `TASKS_INTEGRATION.md`: nueve escenarios en ambas modalidades.

| TASK oficial | Integracion real H2 | Mockito |
| --- | --- | --- |
| testFindAllOwners | OK: JSON, primer id 1 | OK |
| testFindOwnerOK | OK: Franklin | OK |
| testFindOwnerKO | OK: 404 para 666 | OK |
| testCreateOwner | OK: 201, campos JSON y SQL | OK |
| testCreateOwner_BadRequest | OK: 400 sin lastName, sin insercion | OK: sin llamadas al servicio |
| testFindByLastName | OK: Davis, ids 2 y 4 | OK |
| testUpdateOwner | OK: POST, PUT, GET y SQL | OK |
| testDeleteOwner | OK: POST, DELETE, GET 404 y SQL | OK |
| testDeleteOwnerKO | OK: 404 para 1000 | OK |

Arquitectura: MockMvc -> OwnerController -> OwnerService -> OwnerRepository -> H2.
Se agregaron OwnerDTO y OwnerMapper (MapStruct), OwnerNotFoundException y OwnerObjectCreator.
El controlador valida lastName obligatorio (null o blanco devuelve 400).
El servicio usa transacciones; el POST genera un id nuevo y el PUT utiliza el id de la ruta.
OwnerControllerTest usa @SpringBootTest, @AutoConfigureMockMvc, @ActiveProfiles("h2") y
@Transactional con rollback, sin mocks. Las consultas JDBC verifican persistencia despues de flush.
OwnerControllerMockitoTest utiliza @MockitoBean OwnerService y verifica argumentos e interacciones,
sin consultar la base de datos. Los archivos originales de pruebas permanecen intactos.

### Validacion

```powershell
.\mvnw.cmd "-Dtest=OwnerControllerTest,OwnerControllerMockitoTest" test
.\mvnw.cmd test
.\mvnw.cmd clean test "-Dspring.profiles.active=h2"
```

- Ambas clases OwnerController: 18 aprobadas, 0 fallos, 0 errores, 0 omitidas.
- Suite completa: 45 aprobadas (27 originales + 18 Owner), 0 fallos, 0 errores, 0 omitidas.
- Clean test con H2: 45 aprobadas, 0 fallos, 0 errores, 0 omitidas.
- La rama SEBASTIAN no contiene los cambios de VetController: se mantienen en KIARA.
- Se corrigio una ambiguedad de isA entre Mockito y Hamcrest usando jsonPath("$").isArray().
- Advertencias heredadas: Unsafe de Maven/JDK, auto-adjuncion de Mockito y visits sin mapear en PetMapper;
  no son fallos de compilacion ni de pruebas.
- No se realizaron merges ni se resolvieron conflictos. Revision, merge y evidencia de conflicto
  del trabajo grupal quedan para una etapa posterior, segun la instruccion de no fusionar aun.

### Capturas exactas para el informe

1. Ejecutar el comando de ambas clases: guardar las dos lineas de 9 tests y el resumen 18 con BUILD SUCCESS.
2. Ejecutar la suite completa: guardar resumen 45, 0/0/0 y BUILD SUCCESS.
3. Ejecutar clean test con perfil H2: guardar comando y resumen 45 con BUILD SUCCESS.
4. Mostrar ambas clases con sus anotaciones: H2 sin mocks y @MockitoBean OwnerService.
5. Mostrar los tests de validacion, actualizacion y borrado, con jsonPath, GET posterior y verificaciones SQL/Mockito.
6. `git log --all --graph --oneline`: mostrar las ramas independientes, sin merge.
7. `git log origin/main..HEAD --format="%h %an <%ae> %s"`: mostrar autores de los commits.
8. `git status -sb` y `git rev-parse HEAD origin/SEBASTIAN`: mostrar rama limpia y hashes iguales tras push.

Los logs de validacion se conservan localmente en target/owner-tests.log,
target/owner-full-suite.log y target/owner-clean-h2.log (target no se versiona).
