# Evidencias auténticas del cierre

**33 PNG reales**, capturadas con Playwright y Chrome instalado. No se simularon API, sesiones ni datos de respuesta. Las personas, DNI y contactos de ensayo son ficticios; correos bajo example.invalid. Las ilustraciones SVG del sitio son recursos visuales, no fotos de instalaciones.

- Público: inicio, catálogo, filtro, tarjeta, formulario, errores RN7, recepción con referencia y versiones móviles.
- Secretaría: panel, socios, actividades, inscripciones, preinscripciones y vista móvil. Las versiones *-legible.png son capturas de elementos reales para el informe.
- React: ingreso, RN7, catálogo, detalle, ACTIVAS, cuota, baja confirmada y móvil.
- Sistema: ps01-sistema.png a ps06-sistema.png, con mensajes y estado reales.

## Registros comprobables

- verificacion-navegador.json: URLs/dimensiones finales, sin desbordes ni excepciones JS.
- rf3-recepcion.json: HTTP 201, referencia, consulta SQL, aparición en Secretaría y cupos conservados.
- migracion-rf3.json: migración 004 aplicada dos veces sobre seed aislado, conteos conservados.
- casos-sistema-ejecutados.json: seis recorridos reales, HTTP, ACTIVAS/cupos previos y posteriores.
- newman-resultados.json: 25 peticiones, 34 aserciones, cero fallos, detalle por petición.
- escenarios.json: datos ficticios e IDs de esta instancia; no asumir que sirven en otra base.
- cors-y-acceso.json: OPTIONS HTTP del sitio y repositorio público consultable anónimamente.

El usuario eligió una instancia MySQL nueva en 127.0.0.1:3308. Sus bases de ensayo no son las bases del servicio original MySQL84, que siguió detenido. Se crearon cuentas con claves aleatorias privadas bajo .cache ignorada; ningún secreto aparece en el paquete. No se ejecutaron SQL 002/003.

PS01 y PS03–PS06 recorrieron React/Axios/backend/MySQL. PS02 verificó botón sin cupo deshabilitado y un POST HTTP real con Playwright. Tras los seis se ejecutó baja React real. Newman se ejecutó después con socios adicionales propios para alta/baja, conservando los escenarios de reglas.

Las capturas reflejan distintos momentos reales del recorrido. Tres solicitudes de Ana corresponden a repeticiones de captura tras corregir el menú móvil y el empaquetado; otra solicitud de Luz pertenece a Newman. No son inscripciones ni consumen cupo. La confirmación de referencia fotografiada es la #3.
