# Preinscripciones públicas — RF3

El sitio público ahora **recibe y guarda** solicitudes en MySQL. El formulario carga actividades desde `GET /api/actividades`, resuelve los slugs del catálogo inicial a los IDs reales y envía `POST /api/preinscripciones`. Solo HTTP 201 confirma recepción y muestra la referencia asignada. Un error de red conserva los datos y advierte que la recepción puede haber ocurrido antes del corte; un doble clic en curso no dispara otra petición.

```json
{"nombre":"Ana","apellido":"Ensayo","dni":"87654321","email":"ana@example.invalid","actividadId":8}
```

Los campos se validan en JavaScript y mediante Bean Validation en el servidor. La API devuelve 400 ante datos inválidos, 404 ante actividad inexistente y 503 ante fallo de persistencia. La respuesta exitosa contiene únicamente `id` y `fechaRecepcion`; no devuelve DNI ni email. No hay un GET público de solicitudes.

`PreinscripcionApiController → PreinscripcionService → PreinscripcionRepository → MySQL`. La entidad guarda nombre, apellido, DNI, email, actividad y fecha. La FK preserva la referencia a la actividad. Recibir una solicitud no crea socios, no crea inscripciones ni reserva cupo.

La nueva pantalla es **`/secretaria/preinscripciones`**. Aparece en la navegación y en el panel. Muestra las solicitudes más recientes primero y pagina diez por página. Thymeleaf escapa el contenido ingresado; MVC comparte los manejadores de errores existentes.

## Preparación del esquema

- **Base nueva:** `database/schema.sql` ya incluye la cuarta tabla; cargar `seed.sql` únicamente sobre la base nueva prevista para demostración.
- **Base anterior:** respaldar, seleccionar la base correcta y aplicar `database/migrations/004_preinscripcion.sql` antes de arrancar este backend. La migración solo crea la tabla y sus restricciones. No modifica las tres tablas existentes ni ejecuta seed.
- `001_estado_socio.sql` solo corresponde a bases anteriores sin estado de vigencia. No repetir migraciones innecesarias. **002 y 003 siguen como propuestas no ejecutadas.**

En este cierre el usuario eligió una instancia **MySQL aislada en 127.0.0.1:3308**. 004 se ejecutó dos veces sobre un seed nuevo y los conteos anteriores se conservaron; también se comprobó recepción por SQL y por la bandeja. **No se inició MySQL84 ni se aplicó la migración a las bases originales.** Para usar esta versión con esas bases, todavía corresponde aplicar 004 allí con respaldo.

## Evidencia

- Siete pruebas nuevas de controlador/contrato y una de persistencia MySQL.
- Sitio: 29 tests, con envío, errores de servidor/red, IDs reales y bloqueo de doble envío.
- Playwright real: formulario → HTTP 201 → fila SQL → listado de Secretaría, sin cambiar socios ni cupos.
- Newman: tres peticiones RF3 incluidas en la colección de 25 peticiones y 34 aserciones aprobadas.
- [Resultados consolidados](entrega/verificaciones.json) y [recepción comprobada](entrega/evidencias/rf3-recepcion.json).

La aplicación sigue siendo académica y local: Secretaría no incorpora autenticación robusta. CORS no reemplaza autorización. No se publicó un despliegue ni se añadieron RN8 o administración React.
