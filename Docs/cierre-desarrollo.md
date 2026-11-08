# Cierre de desarrollo obligatorio — Prompt 22

Veredicto técnico: **LISTO CON VALIDACIONES MANUALES PENDIENTES**. No se exige FE-ADMIN-01/RN8: son mejoras futuras NO IMPLEMENTADAS, con SQL conservado en database/propuestas. No hay despliegue ni seguridad administrativa robusta. Estado de integración y hashes efectivos se registran al finalizar en registro-trabajo.md; no inferirlos de esta descripción.

## Desarrollo y arquitectura

Tres aplicaciones: sitio HTML/CSS/JS con catálogo inicial y formulario local; Secretaría MVC/Thymeleaf; React Router/Axios. MVC y REST comparten servicios y repositorios JPA/MySQL. React no accede a la base directamente. Los nueve endpoints obligatorios del Anexo A están presentes; PUT baja de socio es un adicional ya implementado y se conserva. No se agregaron más endpoints/extras en este cierre. RN1–RN5 en servicio transaccional; RN6 JavaScript con precios de ACTIVAS y redondeo; RN7 en formularios y validación de entrada. Detalles en arquitectura.md, api-rest.md, sitio-publico.md y react-integracion-api.md.

Modelo final aplicado previamente: Socio(id/DNI/estadoCuota/estadoSocio), Actividad(días/horario inicio/rango edad/cupo/cuota), Inscripcion(FK socio/actividad/fecha/ACTIVA o BAJA). Se preservan historial y FK sin cascadas de borrado. Días/horario permanecen como strings: RN8 no existe. ddl-auto=validate, SQL automático desactivado; migración 001 fue aplicada por el usuario después de respaldos manuales. No hay evidencia de restauración de ensayo. 002/003 no son migraciones de esta entrega ni se ejecutaron.

El sitio público conserva el diseño del equipo, completa la Parte A con ocho objetos Actividad, DOM, filtros y formulario accesible. Usa los datos INICIALES de la consigna; el catálogo real de la API leído hoy tiene nueve registros. No presentar datos estáticos como disponibilidad MySQL. Preinscripción valida localmente y dice que no envía ni guarda: no hay recepción remota/persistencia, alcance y posible aclaración docente de RF3 documentados en sitio-publico.md. La inscripción persistente es el flujo de SOCIOS por React/MVC.

## Evidencia de ESTA ejecución

| Comprobación | Resultado y alcance |
| --- | --- |
| Backend, selección explícita sin MySQL | 111 aprobadas, 0 fallos, 0 errores, 0 omitidas; Maven goal test BUILD SUCCESS |
| React | 58 aprobadas, 0 fallidas, 0 omitidas, cinco suites; mocks Axios solo en tests |
| React build | npm.cmd run build, exit 0, Compiled successfully; mismo stack/lockfile |
| Sitio público | node --check correcto; 25 pruebas Node/jsdom aprobadas, 0 fallos/omisiones; sin paquetes nuevos |
| HTTP estático real | Servidor Python temporal de loopback: tres HTML + CSS + JS + SVG, seis GET 200; servidor cerrado al terminar |
| HTTP backend/React real, sin escrituras | GET backend /, /secretaria y catálogo 200; detalle/socio ficticio/ACTIVAS por GET 200; ID inválido 400 e inexistente 404; React :3000 devuelve 200 |
| Contrato leído | Campo estadoSocio/estadoCuota y array de ACTIVAS compatibles; detalle coincide con ID; precios/cupos en DTO |
| CORS real | OPTIONS /api/inscripciones 200; origen permitido http://localhost:3000 y métodos explícitos, sin '*' |
| REST 201/204/409 | Cubiertos por MockMvc en las 111; NO se ejecutaron escrituras HTTP reales para obtenerlos |
| Postman | Colección y entorno JSON válidos; runner NO ejecutado; ejemplos no son evidencias reales |
| Git/secretos | diff --check y lista publicable revisados antes de commits; .env ignorado; sin publicación de dumps, target, build o node_modules |

Los 128/0/0/0 históricos de verify/MySQL fueron confirmados por el usuario ANTES del fixture manual, no ejecutados otra vez. Las 111 actuales no equivalen a verify completo y los XML que conservan MySQL anterior pueden mezclar fechas; no sumar reportes antiguos y nuevos como una única corrida. Evidencia local ignorada: .cache/prompt22-backend-tests.txt, prompt22-react-tests.txt, prompt22-react-build.txt, prompt22-sitio-tests.txt. El informe no depende de publicar logs con datos sensibles.

No hay capturas nuevas ni prueba visual real de clics en navegador de Codex. El usuario había confirmado que todo arrancaba y que la baja de socio desde Secretaría funcionó en ensayo; eso no confirma cada caso de sistema, cupo final o código HTTP. Se conservaron esos límites. Las pruebas jsdom comprueban DOM/eventos/errores, no layout real, consola Chrome ni validación W3C completa.

## Pendientes clasificados

- **Bloqueantes:** no se detectó un fallo obligatorio de código en la validación disponible. Revisar con la cátedra el límite expreso de RF3 si exige recepción remota adicional; no afirmar que esa recepción existe.
- **Validaciones manuales:** seis casos completos de pruebas-sistema.md, runner Postman preparado por escenarios, recorridos visuales/teclado/móvil, red/refresh y confirmaciones HTTP de escrituras con datos aprobados. No limpiar socioclub_test ni ejecutar su suite de vacío; no modificar datos de demostración sin decidir qué caso/filas se afectan.
- **Entrega y defensa:** nombres/número de grupo/contribuciones verificables, capturas reales, informe PDF 8–12 páginas, revisión de rúbrica de ajustes humanos, estudio posterior con versión final. No se redactó todavía una guía enorme ni se generó un PDF ficticio.
- **Mejoras futuras:** FE-ADMIN-01 y RN8; seguridad/autorización/CSRF y condiciones de exposición pública, despliegue. Ver mejoras-futuras.md. No son implementaciones actuales ni condición para el demo local académico.

## Demostración reproducible

Terminal 1, raíz del repo:

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory .\sitio-publico
```

Terminal 2, desde backend, con MySQL funcionando y .env privado completo:

```powershell
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno.' }
$env:PORT = '8080'
.\mvnw.cmd '-Dspring-boot.run.arguments=--server.port=8080' spring-boot:run
```

Terminal 3, desde app-socios (npm.cmd ci si faltan dependencias):

```powershell
$env:PORT = '3000'
$env:REACT_APP_API_URL = 'http://localhost:8080/api'
npm.cmd start
```

Abrir sitio http://localhost:5500/index.html, Secretaría http://localhost:8080/secretaria y React http://localhost:3000. No mezclar PORT heredado de backend con React; CORS autoriza localhost:3000 y no cualquier hostname. La cuenta de aplicación es socioclub_app, no root. En MySQL puede consultarse solo lectura SELECT id,dni,estado,estado_cuota FROM socioclub.socio para elegir una persona ficticia; INACTIVO debe rechazar nuevas inscripciones.

Antes de mostrar alta: explicar que POST agrega una ACTIVA, ocupa una plaza y modifica el cálculo de cuota. Antes de baja: confirmar que se cambia a BAJA, se libera plaza y se conserva historial. No se puede deshacer la baja lógica de socio desde la app. Capturar datos anteriores/posteriores, no utilizar al INACTIVO para escenarios RN1–RN5 ni reactivarlo a escondidas. Detener servidores con Ctrl+C; no detener MySQL durante el demo.

Pruebas reproducibles sin base: comandos en README; sitio con node --test sitio-publico/tests/script.test.cjs tras instalar lock de app-socios. No ejecutar verify/MySQL indiscriminadamente con el fixture poblado.

## Checklist de evidencia visual (NO capturas realizadas)

- [ ] Sitio: portada con ubicación general, ocho tarjetas, filtros combinados/vacío, error de edad, formulario inválido con errores por campo y válido con aviso local, móvil 360 px.
- [ ] Secretaría: dashboard, búsqueda/paginación socios, cupo/listado ACTIVAS, formulario y mensaje de regla, confirmación de baja sin cambiar nada antes de confirmar.
- [ ] React: ingreso válido/inexistente, catálogo/filtro/detalle, ACTIVAS y cuota, confirmación/cancelación de baja, error de red y actualización después de escritura autorizada.
- [ ] Regla rechazada: screenshot del aviso y Network/Postman con HTTP 409 y regla RNx, junto a estado previo/posterior del caso. No incluir claves, cookies o información real de personas.

No llenar casillas por tener tests automatizados. Exportar capturas reales seleccionadas para informe, no logs/dumps privados. La aceptación de ajustes humanos por la rúbrica debe comprobarse: AH-1/AH-2 son correcciones operativas de cuentas, AH-3 diseño de entorno, IH-MIG-01 ejecución de migraciones y validaciones; no son automáticamente tres modificaciones manuales de Java/React. La corrección manual de plantilla y la alternativa SQL comunicadas se registran con ese alcance, sin inventar edición del archivo generado.

## Integración comprobada

React: commit e09dffe6572b726a5e5a072496b80f9cac97ed19 y merge bb7d51fd6bdada67d02f646503c02e6064b8090a; sitio: commit 7bb4f0201d8008a781233be1381c9fe3ff45854f y merge e7fe985927c27e31af675d05874a1f7adb556cf0. Ambos sin conflictos, ramas conservadas y main intacta. Pruebas postmerge del sitio 25/0 y backend/React idénticos al árbol recién validado. Publicación final de dev se comprueba en Git tras el commit documental. Veredicto: LISTO CON VALIDACIONES MANUALES PENDIENTES, con los límites explícitos de este documento.
