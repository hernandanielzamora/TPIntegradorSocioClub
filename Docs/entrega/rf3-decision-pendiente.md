# RF3: recepción de preinscripciones pendiente

La consigna original, páginas 2–3, pide dejar y recibir una preinscripción. La Parte A detalla validación JavaScript; eso no convierte la validación local en recepción. El formulario actual no realiza ninguna petición ni guarda información. Por ese motivo RF3 se audita como **parcial y bloqueante para declarar el cierre integral**.

## Solución mínima propuesta, todavía no implementada

1. Agregar una entidad `Preinscripcion` con UUID público, nombre, apellido, DNI, email, actividad solicitada y fecha de recepción. Mantenerla separada de `Socio` e `Inscripcion`: una solicitud pública no debe consumir cupo ni crear un socio.
2. Incorporar un repositorio JPA y servicio transaccional. Validar obligatorios, longitudes, email, DNI RN7 y actividad existente/activa en el servidor, además de las validaciones actuales del cliente.
3. Agregar `POST /api/preinscripciones`, contrato adicional claramente **fuera de los nueve endpoints del Anexo A**. Responder 201 solo tras persistir y devolver identificador; 400 ante entrada inválida, 404 ante actividad inexistente, 503 ante fallo de persistencia. No devolver datos personales en errores.
4. Obtener las actividades por sus identificadores reales de la API: los slugs del catálogo público no son PK MySQL. Conservar el catálogo inicial para consulta, indicando que la recepción requiere backend disponible.
5. Reemplazar el mensaje local de éxito por estados de envío, recepción confirmada y fallo recuperable. Un fallo de red nunca debe presentarse como recepción. No almacenar DNI o contactos en URL, localStorage ni repositorio.
6. Preparar y revisar una migración independiente; respaldar y aplicar **solo con autorización expresa**, sin seed ni alteración de socios/inscripciones existentes. La nueva entidad no debe habilitarse contra una base sin tabla, porque `ddl-auto=validate` impediría arrancar.
7. Verificar persistencia, RN7 inválida, actividad inexistente y fallo de red con tests y un caso HTTP real sobre base aislada autorizada; capturar el identificador de recepción sin publicar datos personales.

Esta propuesta es concreta, pero no afirma código de recepción implementado. Introducir una entidad activa sin su migración dejaría el arranque roto. No se añade almacenamiento alternativo en archivos o en el navegador porque el sistema académico exige compartir MySQL. Se necesita autorización para el cambio de esquema o una aclaración docente verificable que acepte expresamente el alcance local actual.

No se ejecutaron SQL nuevos ni se modificaron bases de datos.
