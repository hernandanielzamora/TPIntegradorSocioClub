# React integrado con la API REST

Rama feature/react-integracion-api, creada desde dev publicado en 08d7c403bcd2fe392fbaedd96a40c4eee8b81695. Backend integrado mediante d58094cfd940f8a885908335f10febf2effdb5b3 y merge 3ace199fc3ef6c847b47ce7457ae41680cb5bdb5. Esta rama React NO se integra a dev en esta etapa.

## Auditoría y autoría

Se conservaron React 18, React Router, Axios, PropTypes, CRA, componentes, CSS responsivo y rutas del trabajo existente. Git conserva, entre otros, commits de Mateo Hernandez 4268ffb (Mis Actividades y cuota), ec53dfb (contexto e ingreso) y dba8ac7 (tests de tarjeta). No se reescribe el frontend. Antes de esta etapa las cuatro páginas usaban servicios/datosLocales.js; api.js ya contenía axios.create sin consumo real. Había 20 tests de tarjeta/DNI/RN6. Codex realizó la integración y sus correcciones; decisiones/ensayo manual backend fueron del usuario. No consta edición humana de este código nuevo. AH-1–AH-3 e IH-MIG-01 conservan sus límites.

## Componentes, rutas y contrato

| Requerimiento / componente | Ruta React | Endpoint o función |
| --- | --- | --- |
| Identificación DNI, Ingreso + SocioContext | / | GET /api/socios/dni/{dni} |
| Catálogo, ListaActividades, TarjetaActividad | /actividades | GET /api/actividades; filtro local por disciplina, cliente admite params.disciplina |
| DetalleActividad | /actividades/:id | GET /api/actividades/{id} |
| Alta desde catálogo/detalle | mismas rutas | POST /api/inscripciones con socioId/actividadId |
| MisActividades | /mis-actividades | GET /api/socios/{id}/inscripciones |
| Baja con confirmación nativa | /mis-actividades | PUT /api/inscripciones/{id}/baja |
| RN6, ResumenCuota | /mis-actividades | actividad.cuotaMensual de inscripciones ACTIVAS; calcularCuotaMensual |
| Salir / cambiar socio | encabezado | limpia estado y sessionStorage, sin endpoint de autenticación |

RutaProtegida solo exige identificación local; no protege el backend. API de baja de SOCIO no se expone como autoservicio React: sigue en Secretaría. No falta ningún dato del DTO para RN6; no se cambió el backend para esta integración.

## HTTP y datos reales

servicios/api.js centraliza baseURL, timeout 10 segundos y GET/POST/PUT. Valida IDs, forma de arrays/DTOs, estados, campos de actividad, precios numéricos y relaciones de inscripción antes de renderizar. Una respuesta HTML/objeto inesperado genera error de datos, sin inventar resultados ni recurrir a datos locales. Las inscripciones BAJA no contribuyen a RN6. datosLocales.js y datosEjemplo.js se conservan como antecedente del hito anterior sin imports en producción, no como fallback. Solo los tests usan fixtures/mocks Axios.

mensajeDeError traduce 400/404/409 y errores de red/servidor a mensajes propios; usa regla RN1–RN5 o codigo SOCIO_INACTIVO y no muestra SQL/trazas/textos crudos. 404 en ingreso informa socio no encontrado. Una mutación confirmada cuyo refresco falla se informa como registrada con actualización pendiente: no se anuncia falsamente que falló la escritura. Ante timeout o respuesta inválida tras POST puede desconocerse si hubo commit: consultar antes de repetir, sin reintento automático de escrituras.

Las pantallas conservan carga, errores, vacíos y avisos. Se deshabilitan acciones durante la escritura y una referencia evita doble envío síncrono. Baja requiere window.confirm; Cancelar no envía PUT. Tras alta se refresca cupo; tras baja se refrescan inscripciones/cuota, y catálogo/detalle vuelven a consultar al navegar. No hay WebSocket ni refresco en tiempo real para cambios de otros usuarios. RN1–RN5 siguen siendo autoridad del backend; INACTIVO se muestra y bloquea alta como ayuda de interfaz, además de la validación real del servicio.

## Identificación y RN6

Solo se persiste el DNI en sessionStorage como identificador mínimo consultable por el contrato existente (no hay GET socio por ID). Al recargar se recupera de nuevo el DTO por Axios; errores de recuperación devuelven al ingreso. Se retira la clave anterior que guardaba el objeto completo. Nombre, nacimiento y estados quedan en memoria, no en sessionStorage. El DNI también es un dato personal: no guardar otros datos, no tratar esta identificación como contraseña/token ni exponer la aplicación públicamente.

RN6 reutiliza la función existente y sus pruebas, con mejora de redondeo a centavos: 0 actividades → 0; 1 → sin descuento; 2 → 10%; 3 → 15%. Precios de las ACTIVAS reales, suma en centavos y resultado con dos decimales. La presentación usa ARS; no es facturación ni modifica cuotas en MySQL.

## Ejecución local reproducible

No se arrancaron backend ni navegador contra bases por Codex en esta etapa. Los comandos siguientes son para ejecución humana. Backend development (terminal 1, desde backend, conexión socioclub/socioclub_app):

```powershell
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno.' }
$env:PORT = '8080'
.\mvnw.cmd spring-boot:run
```

Terminal 2, desde app-socios:

```powershell
npm.cmd ci
$env:REACT_APP_API_URL = 'http://localhost:8080/api'
npm.cmd start
```

Abrir http://localhost:3000. REACT_APP_API_URL se incorpora al compilar; no poner secretos, claves MySQL ni credenciales en variables React. También puede copiarse .env.example a .env local ignorado; reiniciar CRA al cambiar la URL. El backend autoriza por defecto Origin http://localhost:3000; si se abre React por otro hostname hay que revisar CORS_ALLOWED_ORIGINS, sin habilitar '*'.

Verificar solo lectura primero desde otra terminal y Network del navegador:

```powershell
(Invoke-WebRequest 'http://localhost:8080/' -UseBasicParsing).StatusCode
Invoke-RestMethod 'http://localhost:8080/api/actividades'
Invoke-WebRequest -Method Options -Uri 'http://localhost:8080/api/inscripciones' -Headers @{
    Origin='http://localhost:3000'
    'Access-Control-Request-Method'='POST'
    'Access-Control-Request-Headers'='content-type'
} -UseBasicParsing
```

Esperar origen permitido en preflight. No se afirma ejecución de estas peticiones. Ingresar un DNI ficticio existente de desarrollo, consultar catálogo, filtros, detalle y Mis actividades. No usar los IDs de los mocks como si fueran IDs de MySQL.

Antes de probar escrituras: alta crea UNA inscripción ACTIVA para el socio/actividad elegidos y ocupa una plaza; baja cambia esa inscripción a BAJA, libera plaza y conserva historial. No modifica estadoSocio ni estadoCuota. Este trabajo no ejecutó escrituras en desarrollo. Si se desea ensayar con el fixture manual de testing ya cargado, usar la instancia dedicada del procedimiento validacion-manual-baja-socios.md y fijar REACT_APP_API_URL=http://127.0.0.1:8081/api, manteniendo React en localhost:3000. No recargar el fixture ni ejecutar MySQL tests que exigen vacío. Si Martín ya quedó INACTIVO, debe rechazarse el alta; no reactivarlo para conseguir un caso exitoso. Elegir otro fixture requiere decisión humana separada, no carga automática.

## Pruebas y límites de validación

Comandos sin MySQL, con mocks de Axios:

```powershell
npm.cmd test -- --watchAll=false --runInBand
npm.cmd run build
```

Se conservan tests previos y se agregan servicio/contrato, ingreso inválido/404/exitoso/recuperación/salida, respuestas inesperadas, catálogo vacío/cargado/filtrado, red, detalle, alta con refresco y doble clic, conflictos RN1–RN5, cancelación/confirmación/error de baja y actualización de cuota. Los resultados exactos se registran tras ejecutar; no se usa passWithNoTests. Pendientes de validación manual: CORS real, recorrido navegador con Spring/MySQL y persistencia observable después de escrituras autorizadas. Mocks prueban frontend/contrato esperado, no conectividad HTTP real ni regresión completa MySQL.

La aplicación sigue sin autenticación/autorización/CSRF robustas. No hubo despliegue, migración, cambio de credenciales o modificación de sitio-publico. Esta etapa solo publica la rama feature después de validar; no se abre PR ni se integra React a dev.

## Resultado efectivo de validación React

npm.cmd test -- --watchAll=false --runInBand: 58 aprobadas, 0 fallidas, 0 omitidas, cinco suites, sin MySQL. npm.cmd run build con CI=true: exit 0, Compiled successfully (bundle JS gzip 80.08 kB, CSS 2.51 kB). El aviso Node DEP0176/fs.F_OK procede del stack de herramientas existente y no impidió compilar; no se migró CRA ni se cambiaron dependencias para ocultarlo. npm ci mantuvo package.json y package-lock.json sin modificaciones. Logs/resultados locales en .cache ignorado; build y node_modules no se publican. No hubo pruebas reales en navegador, solicitudes contra MySQL ni despliegue. Esos pendientes están descritos en react-integracion-api.md.
