

## Matriz vigente de los seis casos obligatorios

Fecha del cierre: 2026-11-08. Los datos iniciales deben comprobarse en el catálogo elegido: los ejemplos históricos siguientes NO garantizan sus precondiciones hoy. Codex no ejecutó POST/PUT/DELETE contra las bases en esta etapa. Los resultados de unitarios/MockMvc/RTL son evidencia automática auténtica, no resultados E2E. El ensayo de baja de SOCIO confirmado por el usuario no sustituye estos casos de inscripción.

| ID / objetivo | Datos iniciales a comprobar | Pasos manuales (solo con autorización para esos datos) | Esperado | Observado actualmente | Evidencia auténtica | Estado |
| --- | --- | --- | --- | --- | --- | --- |
| PS01: alta exitosa | Socio ACTIVO/AL_DIA, edad apta, <3 ACTIVAS, no duplicado; actividad con vacante | Consultar y capturar datos/cupo/ACTIVAS; ingresar DNI en React; abrir detalle; Inscribirme; consultar Mis actividades/RN6 y cupo por GET | POST 201; una ACTIVA nueva, cupo baja 1, cuota actualizada | Unitario y MockMvc exitosos, React mock de alta/refresco aprobado; recorrido real no ejecutado por Codex | InscripcionServiceTests.inscripcionValidaConCupoDisponible; ApiControllerTests.inscripcionValida201; integracionApi.test.js | Implementado, pendiente de validación |
| PS02: RN1 sin cupo | Socio apto, sin duplicado ni máximo; ACTIVAS=cupo máximo | Capturar conteos, enviar POST /api/inscripciones desde Postman con IDs comprobados (el botón React se deshabilita sin cupo), volver a consultar | 409 regla RN1; ninguna fila nueva ni cambio de cupo | Unitario y respuesta MockMvc/React aprobados; caso real no ejecutado | rechazaCupoCompletoOYaExcedido, regla409ConCodigoDelAnexo(SIN_CUPO), test React RN1 | Implementado, pendiente de validación |
| PS03: RN2 duplicado | Socio ACTIVO/AL_DIA, edad apta, inscripción ACTIVA previa misma actividad | Capturar ID previo; intentar alta de misma pareja; consultar filas y cupo | 409 regla RN2; conserva la ACTIVA original | Unitario/MockMvc/React aprobados; caso real no ejecutado | rechazaActivaDuplicada; regla409ConCodigoDelAnexo(INSCRIPCION_DUPLICADA) | Implementado, pendiente de validación |
| PS04: RN3 edad | Socio ACTIVO/AL_DIA fuera del rango inclusivo; actividad existente | Calcular edad actual/registrar límites; intentar inscripción; consultar ausencia de alta | 409 regla RN3, ninguna ACTIVA nueva | Bordes/bisiesto y MockMvc/React aprobados; caso real no ejecutado | Tests de cumpleaños InscripcionServiceTests; regla409ConCodigoDelAnexo(EDAD_FUERA_DE_RANGO) | Implementado, pendiente de validación |
| PS05: RN4 deuda | Socio ACTIVO/MOROSO (no INACTIVO) | Capturar estado de cuota; intentar alta; consultar lista; no regularizar ni cambiar estados automáticamente | 409 regla RN4; no alta; consulta/baja siguen permitidas | Unitario/MockMvc/React aprobados; caso real no ejecutado | rechazaSocioMoroso; regla409ConCodigoDelAnexo(SOCIO_MOROSO) | Implementado, pendiente de validación |
| PS06: RN5 máximo | Socio ACTIVO/AL_DIA, edad apta, tres ACTIVAS distintas; destino nuevo con cupo | Capturar tres IDs, intentar cuarta actividad, verificar que conserva exactamente las tres y RN6 | 409 regla RN5; sin cuarta ACTIVA | Unitario/MockMvc/React aprobados; caso real no ejecutado | rechazaCuartaActividadOConteoYaExcedido; regla409ConCodigoDelAnexo(MAXIMO_ACTIVIDADES) | Implementado, pendiente de validación |

La preparación manual puede necesitar datos adicionales: no concluir que el seed ya arma RN1 (Yoga está casi completa, no necesariamente llena). No usar al socio INACTIVO del ensayo para RN1–RN5: se rechaza antes por vigencia. No modificar cuota/edades/cupos ni borrar datos para alcanzar un escenario sin autorización separada. Documentar el orden de validaciones y evitar otra regla con prioridad anterior. Nunca limpiar socioclub_test ni ejecutar sus suites de vacío sobre el fixture actual.

### Captura de datos, solo lectura

Desde una conexión de Workbench al servidor previsto. Estas consultas no fueron ejecutadas por Codex en este cierre (sí se hicieron GET API de lectura). Guardar capturas fuera del repositorio si contienen datos personales; usar solo personas ficticias para demostrar:

```sql
SELECT DATABASE(),CURRENT_USER();
SELECT id,dni,estado,estado_cuota,TIMESTAMPDIFF(YEAR,fecha_nacimiento,CURRENT_DATE) AS edad
FROM socioclub.socio ORDER BY id;
SELECT a.id,a.nombre,a.edad_minima,a.edad_maxima,a.cupo_maximo,
       SUM(CASE WHEN i.estado='ACTIVA' THEN 1 ELSE 0 END) AS activas,
       a.cupo_maximo-SUM(CASE WHEN i.estado='ACTIVA' THEN 1 ELSE 0 END) AS disponibles
FROM socioclub.actividad a LEFT JOIN socioclub.inscripcion i ON i.actividad_id=a.id
GROUP BY a.id,a.nombre,a.edad_minima,a.edad_maxima,a.cupo_maximo ORDER BY a.id;
```

Una vez que el operador seleccione IDs, registrar petición/respuesta (sin secretos), pantalla, datos anteriores/posteriores y fecha por caso. Completar “Observado” solo después de hacerlo. Una inscripción exitosa AGREGA una fila y ocupa una plaza; darla de baja conserva esa fila como BAJA. No restaurar fixtures o estados automáticamente al terminar. La colección Postman tiene requests y ejemplos, pero un ejemplo no es una ejecución. No correr el runner entero a ciegas: incluye operaciones de eliminación y escenarios sin preparar.

## Antecedentes de escenarios preparados en etapas anteriores

Las referencias siguientes a funcionalidades todavía no implementadas corresponden al estado inicial; se conservan para trazabilidad. Su “Sin ejecutar” describe pruebas manuales que continúan sin evidencia individual; los resultados automatizados vigentes están arriba.
# Pruebas de sistema — SocioClub

## 1. Objetivo

Documentar los seis escenarios de prueba de sistema exigidos por la Parte E del TP Integrador SocioClub.

Se incluyen:
- Un caso de inscripción exitosa.
- Cinco casos de rechazo correspondientes a las reglas de negocio RN1 a RN5.

**Importante:** Ninguna de estas pruebas fue ejecutada. Son escenarios preparados para una ejecución futura. Requieren funcionalidades y datos que quedan fuera de Prompt 01.

Todos los datos personales incluidos son ficticios y se utilizan únicamente como ejemplos para preparar las pruebas.

## 2. Criterios generales

- Las inscripciones se realizan mediante el sistema o la API REST.
- Los rechazos de reglas de negocio deben devolver HTTP 409 Conflict en la API.
- Una inscripción rechazada no debe generar un nuevo registro ACTIVO.
- Cada escenario debe prepararse de manera independiente.
- Antes de ejecutar cada prueba se deben verificar las precondiciones para evitar que otra regla de negocio impida llegar a la regla que se quiere comprobar.
- Los datos deben restaurarse o prepararse nuevamente cuando sea necesario para que un caso no afecte al siguiente.

### Datos generales para la preparación

Se utilizan actividades del enunciado original:

| Actividad | Rango de edad | Cupo máximo |
| --- | --- | --- |
| Fútbol infantil | 6 a 12 años | 40 |
| Vóley mixto | 13 a 60 años | 24 |
| Natación | 5 a 70 años | 30 |
| Funcional adultos | 18 a 65 años | 30 |
| Yoga | 16 a 99 años | 15 |

Los identificadores de socios y actividades deberán verificarse en la base de datos al preparar cada escenario. No se presuponen IDs existentes.

---

## 3. Casos de prueba

### PS01 — Inscripción exitosa

**Regla comprobada:** Inscripción permitida cuando se cumplen todas las reglas de negocio.

**Precondiciones:**

- El socio existe en la base de datos.
- Su estado de cuota es AL_DIA.
- Tiene una edad válida para la actividad.
- Tiene menos de tres inscripciones ACTIVAS.
- No está inscripto previamente en la actividad elegida.
- La actividad tiene cupo disponible.

**Datos ficticios:**

| Dato | Valor |
| --- | --- |
| DNI | 30111222 |
| Nombre | Juan |
| Apellido | Pérez |
| Fecha de nacimiento | 15/05/2001 |
| Estado de cuota | AL_DIA |
| Actividad | Funcional adultos |
| Inscripciones activas iniciales | 0 |
| Cupo disponible | Al menos 1 |

**Pasos:**

1. Verificar que el socio Juan Pérez exista y tenga estado AL_DIA.
2. Consultar sus inscripciones y confirmar que no tenga inscripciones ACTIVAS.
3. Consultar la actividad Funcional adultos y verificar que tenga cupo disponible.
4. Confirmar que la edad del socio esté dentro del rango de 18 a 65 años.
5. Enviar la solicitud de inscripción.
6. Consultar nuevamente las inscripciones del socio.
7. Verificar el cupo disponible de la actividad.

**Resultado esperado:**

- La inscripción se registra correctamente con estado ACTIVA.
- La API devuelve HTTP 201 Created.
- El socio aparece inscripto en Funcional adultos.
- El cupo disponible disminuye en una unidad.

**Resultado obtenido:** Sin ejecutar / Pendiente.

**Estado:** Pendiente.

**Evidencia pendiente:**

- Captura de la solicitud enviada.
- Respuesta HTTP 201.
- Consulta de la inscripción creada.
- Verificación del cupo actualizado.

---

### PS02 — Rechazo por RN1: Cupo completo

**Regla comprobada:** RN1 — Una actividad no puede superar su cupo máximo de inscripciones ACTIVAS.

**Precondiciones:**

- La actividad tiene todas sus plazas ocupadas.
- El socio está habilitado para inscribirse.
- El socio tiene estado de cuota AL_DIA.
- Su edad está dentro del rango permitido.
- Tiene menos de tres inscripciones ACTIVAS.
- No tiene una inscripción previa en la actividad evaluada.

**Datos ficticios:**

| Dato | Valor |
| --- | --- |
| DNI | 30222333 |
| Nombre | Lucas |
| Apellido | Gómez |
| Fecha de nacimiento | 10/06/2016 |
| Estado de cuota | AL_DIA |
| Actividad | Fútbol infantil |
| Edad permitida | 6 a 12 años |
| Cupo máximo | 40 |
| Inscripciones ACTIVAS existentes | 40 |
| Cupo disponible | 0 |

**Pasos:**

1. Verificar que el socio Lucas Gómez exista y tenga estado AL_DIA.
2. Confirmar que su edad esté dentro del rango de Fútbol infantil.
3. Verificar que tenga menos de tres inscripciones ACTIVAS.
4. Confirmar que no esté inscripto en Fútbol infantil.
5. Consultar los inscriptos de Fútbol infantil y verificar que existan 40 inscripciones ACTIVAS.
6. Intentar inscribir al socio en Fútbol infantil.
7. Consultar nuevamente los inscriptos y verificar que no se haya generado una nueva inscripción ACTIVA.

**Resultado esperado:**

- La inscripción es rechazada.
- La API devuelve HTTP 409 Conflict.
- El mensaje de error es: "La actividad no tiene cupo disponible".
- La cantidad de inscripciones ACTIVAS continúa siendo 40.

**Resultado obtenido:** Sin ejecutar / Pendiente.

**Estado:** Pendiente.

**Evidencia pendiente:**

- Captura de la actividad con sus 40 inscriptos.
- Solicitud de inscripción.
- Respuesta HTTP 409 con el mensaje correspondiente.
- Verificación de que no se agregó una inscripción.

---

### PS03 — Rechazo por RN2: Inscripción duplicada

**Regla comprobada:** RN2 — Un socio no puede tener dos inscripciones ACTIVAS en la misma actividad.

**Precondiciones:**

- El socio ya tiene una inscripción ACTIVA en la actividad elegida.
- La actividad todavía tiene cupo disponible.
- El socio tiene estado de cuota AL_DIA.
- Su edad está dentro del rango permitido.
- Tiene menos de tres inscripciones ACTIVAS.

**Datos ficticios:**

| Dato | Valor |
| --- | --- |
| DNI | 30333444 |
| Nombre | Martín |
| Apellido | López |
| Fecha de nacimiento | 12/08/2015 |
| Estado de cuota | AL_DIA |
| Actividad | Fútbol infantil |
| Edad permitida | 6 a 12 años |
| Inscripciones ACTIVAS iniciales | 1 |
| Inscripción existente | Fútbol infantil |
| Cupo disponible | Al menos 1 |

**Pasos:**

1. Verificar que el socio Martín López exista y tenga estado AL_DIA.
2. Confirmar que su edad esté dentro del rango permitido para Fútbol infantil.
3. Verificar que tenga menos de tres inscripciones ACTIVAS.
4. Consultar sus inscripciones y confirmar que ya tenga una inscripción ACTIVA en Fútbol infantil.
5. Verificar que Fútbol infantil tenga cupo disponible.
6. Intentar inscribir nuevamente al socio en Fútbol infantil.
7. Consultar sus inscripciones y verificar que continúe existiendo una sola inscripción ACTIVA en esa actividad.

**Resultado esperado:**

- La inscripción duplicada es rechazada.
- La API devuelve HTTP 409 Conflict.
- El mensaje de error es: "El socio ya está inscripto en esta actividad".
- No se crea una segunda inscripción ACTIVA.

**Resultado obtenido:** Sin ejecutar / Pendiente.

**Estado:** Pendiente.

**Evidencia pendiente:**

- Consulta de la inscripción ACTIVA existente.
- Solicitud de inscripción duplicada.
- Respuesta HTTP 409 con el mensaje correspondiente.
- Verificación de que no se duplicó el registro.

---

### PS04 — Rechazo por RN3: Edad fuera de rango

**Regla comprobada:** RN3 — La edad del socio debe estar entre la edad mínima y máxima permitida para la actividad.

**Precondiciones:**

- El socio existe y tiene estado de cuota AL_DIA.
- Su edad está fuera del rango permitido para la actividad.
- Tiene menos de tres inscripciones ACTIVAS.
- No tiene una inscripción previa en la actividad.
- La actividad tiene cupo disponible.

**Datos ficticios:**

| Dato | Valor |
| --- | --- |
| DNI | 30444555 |
| Nombre | Nicolás |
| Apellido | Fernández |
| Fecha de nacimiento | 10/04/2012 |
| Edad al momento de la prueba | 14 años |
| Estado de cuota | AL_DIA |
| Actividad | Fútbol infantil |
| Edad permitida | 6 a 12 años |
| Inscripciones ACTIVAS iniciales | 0 |
| Cupo disponible | Al menos 1 |

**Pasos:**

1. Verificar que el socio Nicolás Fernández exista y tenga estado AL_DIA.
2. Consultar su fecha de nacimiento y calcular su edad a la fecha de ejecución.
3. Confirmar que tiene 14 años y que la actividad admite edades de 6 a 12 años.
4. Verificar que tenga menos de tres inscripciones ACTIVAS.
5. Confirmar que no esté inscripto en Fútbol infantil.
6. Verificar que Fútbol infantil tenga cupo disponible.
7. Intentar inscribir al socio en Fútbol infantil.
8. Verificar que no se haya generado una inscripción ACTIVA.

**Resultado esperado:**

- La inscripción es rechazada por no cumplir el rango de edad.
- La API devuelve HTTP 409 Conflict.
- El mensaje de error indica que la edad del socio está fuera del rango permitido de 6 a 12 años.
- No se crea una inscripción ACTIVA.

**Resultado obtenido:** Sin ejecutar / Pendiente.

**Estado:** Pendiente.

**Evidencia pendiente:**

- Datos del socio y fecha de nacimiento.
- Datos de la actividad y rango de edad.
- Solicitud de inscripción.
- Respuesta HTTP 409 con el mensaje correspondiente.
- Verificación de que no se creó la inscripción.

---

### PS05 — Rechazo por RN4: Socio moroso

**Regla comprobada:** RN4 — Un socio con estado MOROSO no puede inscribirse en nuevas actividades.

**Precondiciones:**

- El socio existe y tiene estado de cuota MOROSO.
- Su edad está dentro del rango permitido.
- Tiene menos de tres inscripciones ACTIVAS.
- No tiene una inscripción previa en la actividad elegida.
- La actividad tiene cupo disponible.

**Datos ficticios:**

| Dato | Valor |
| --- | --- |
| DNI | 30555666 |
| Nombre | Federico |
| Apellido | Ramírez |
| Fecha de nacimiento | 20/03/2006 |
| Edad al momento de la prueba | 20 años |
| Estado de cuota | MOROSO |
| Actividad | Yoga |
| Edad permitida | 16 a 99 años |
| Inscripciones ACTIVAS iniciales | 0 |
| Cupo disponible | Al menos 1 |

**Pasos:**

1. Verificar que el socio Federico Ramírez exista.
2. Consultar su estado de cuota y confirmar que sea MOROSO.
3. Confirmar que su edad esté dentro del rango permitido para Yoga.
4. Verificar que tenga menos de tres inscripciones ACTIVAS.
5. Confirmar que no esté inscripto previamente en Yoga.
6. Verificar que Yoga tenga cupo disponible.
7. Intentar inscribir al socio en Yoga.
8. Consultar nuevamente sus inscripciones y confirmar que no se haya creado una nueva inscripción ACTIVA.

**Resultado esperado:**

- La inscripción es rechazada.
- La API devuelve HTTP 409 Conflict.
- El mensaje de error es: "Debe regularizar su cuota en secretaría".
- No se crea una inscripción ACTIVA.
- El socio conserva la posibilidad de consultar sus inscripciones y darse de baja de actividades existentes.

**Resultado obtenido:** Sin ejecutar / Pendiente.

**Estado:** Pendiente.

**Evidencia pendiente:**

- Consulta del socio mostrando estado MOROSO.
- Solicitud de inscripción.
- Respuesta HTTP 409 con el mensaje correspondiente.
- Verificación de que no se creó una inscripción.
- Comprobación de que puede consultar sus inscripciones y solicitar una baja, si esas funcionalidades están disponibles.

---

### PS06 — Rechazo por RN5: Máximo de 3 actividades

**Regla comprobada:** RN5 — Un socio puede tener como máximo tres inscripciones ACTIVAS.

**Precondiciones:**

- El socio existe y tiene estado de cuota AL_DIA.
- Tiene exactamente tres inscripciones ACTIVAS en actividades diferentes.
- No está inscripto en la actividad nueva elegida.
- Su edad está dentro del rango permitido.
- La actividad nueva tiene cupo disponible.

**Datos ficticios:**

| Dato | Valor |
| --- | --- |
| DNI | 30666777 |
| Nombre | Agustín |
| Apellido | Torres |
| Fecha de nacimiento | 15/02/2006 |
| Edad al momento de la prueba | 20 años |
| Estado de cuota | AL_DIA |
| Inscripciones ACTIVAS iniciales | 3 |

Actividades en las que ya está inscripto:

| Actividad | Estado |
| --- | --- |
| Vóley mixto | ACTIVA |
| Natación | ACTIVA |
| Funcional adultos | ACTIVA |

Actividad a la que intenta inscribirse:

| Dato | Valor |
| --- | --- |
| Actividad | Yoga |
| Edad permitida | 16 a 99 años |
| Cupo disponible | Al menos 1 |
| Inscripción previa en Yoga | No |

**Pasos:**

1. Verificar que el socio Agustín Torres exista y tenga estado AL_DIA.
2. Consultar sus inscripciones y confirmar que tenga exactamente tres ACTIVAS.
3. Verificar que las tres actividades sean Vóley mixto, Natación y Funcional adultos.
4. Confirmar que no esté inscripto en Yoga.
5. Verificar que su edad esté dentro del rango permitido para Yoga.
6. Consultar Yoga y confirmar que tenga cupo disponible.
7. Intentar inscribir al socio en Yoga.
8. Consultar nuevamente sus inscripciones y verificar que conserve las tres inscripciones ACTIVAS originales.

**Resultado esperado:**

- La inscripción es rechazada.
- La API devuelve HTTP 409 Conflict.
- El mensaje de error es: "Alcanzó el máximo de 3 actividades".
- El socio conserva sus tres inscripciones ACTIVAS.
- No se genera una cuarta inscripción ACTIVA.

**Resultado obtenido:** Sin ejecutar / Pendiente.

**Estado:** Pendiente.

**Evidencia pendiente:**

- Consulta de las tres inscripciones ACTIVAS iniciales.
- Solicitud de inscripción en Yoga.
- Respuesta HTTP 409 con el mensaje correspondiente.
- Consulta posterior que confirme que continúa teniendo solamente tres inscripciones ACTIVAS.

---

## 4. Registro de ejecución

Esta sección deberá completarse cuando los escenarios se ejecuten realmente.

| Caso | Fecha de ejecución | Responsable | Resultado observado | Evidencia |
| --- | --- | --- | --- | --- |
| PS01 | Pendiente | Pendiente | Sin ejecutar | Pendiente |
| PS02 | Pendiente | Pendiente | Sin ejecutar | Pendiente |
| PS03 | Pendiente | Pendiente | Sin ejecutar | Pendiente |
| PS04 | Pendiente | Pendiente | Sin ejecutar | Pendiente |
| PS05 | Pendiente | Pendiente | Sin ejecutar | Pendiente |
| PS06 | Pendiente | Pendiente | Sin ejecutar | Pendiente |

**Nota:** No completar fechas, responsables, resultados observados ni evidencias hasta realizar cada prueba.

## 5. Observaciones

- Los escenarios son una preparación documental y no demuestran por sí mismos que las reglas de negocio estén funcionando.
- Los datos ficticios deberán cargarse o adaptarse a los registros disponibles en la base de datos antes de ejecutar cada prueba.
- Se deben verificar las precondiciones antes de cada intento de inscripción para asegurar que se está comprobando la regla correspondiente.
- Si una prueba falla o aparece un mensaje de otra regla, se debe revisar la preparación del escenario antes de registrar el resultado.
- Las comprobaciones de compilación o inicialización del proyecto no reemplazan estas pruebas de sistema.
- Una vez ejecutados los escenarios, se deben actualizar los resultados y adjuntar evidencias reales.

