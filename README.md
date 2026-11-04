# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2

## Grupo 2 - VetController - KIARA

Integrante: KIARA (kiaraalburqueque-stack, kiara.alburqueque@tecsup.edu.pe).
Rama de entrega: `KIARA`. Integracion a main y revision del equipo pendientes para una etapa posterior.

Se implementaron los nueve escenarios oficiales en `VetControllerTest` (stack real con H2,
sin mocks, transacciones con rollback) y `VetControllerMockitoTest` (`@MockitoBean VetService`
y helper `VetObjectCreator`, verificando argumentos e interacciones).

| TASK | H2 | Mockito |
| --- | --- | --- |
| testFindAllVets | OK | OK |
| testFindVetOK | OK | OK |
| testFindVetKO | OK | OK |
| testCreateVet | OK | OK |
| testDeactivateVet | OK | OK |
| testReactivateVet | OK | OK |
| testFindActiveVets | OK | OK |
| testFindInactiveVets | OK | OK |
| testDeleteVetKO | OK | OK |

Reactivacion: `PUT /vets/6/reactivate`. Los filtros respetan los datos semilla: 5 activos y 1 inactivo.
La creacion comprueba campos JSON e insercion SQL; los cambios de estado se verifican por SQL y GET.

Validacion:

```powershell
.\mvnw.cmd "-Dtest=VetControllerTest,VetControllerMockitoTest" test
.\mvnw.cmd test
.\mvnw.cmd clean test "-Dspring.profiles.active=h2"
```

Resultado: 18 pruebas nuevas y 27 originales, 45 aprobadas, 0 fallos, 0 errores, 0 omitidas.
Se conservaron todos los archivos de pruebas originales sin modificaciones.
Durante la implementacion se corrigio la lectura SQL antes del flush de JPA en los tests
transaccionales de activacion: se ejecuta `EntityManager.flush()` antes de consultar con JDBC.
No se han resuelto conflictos Git ni realizado merges en esta entrega.

Capturas para el informe: resumen verde de ambas clases (18), resumen de la suite (45),
resumen de clean test con perfil H2 (45), matriz anterior, anotaciones de ambas clases,
`git log --all --graph --oneline`, autores de los commits y `git status -sb` tras el push.
