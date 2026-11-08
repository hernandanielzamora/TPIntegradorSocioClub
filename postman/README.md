# Postman — API REST SocioClub

Importar SocioClub.postman_collection.json (Collection v2.1) y, opcionalmente, SocioClub-local.postman_environment.json mediante Import / Files. Seleccionar el entorno local. baseUrl vale http://localhost:8080, sin /api al final. No hay credenciales en estos archivos.

La colección contiene 22 requests para los nueve endpoints del Anexo A y uno voluntario de baja de socios, filtros y casos de error, ejemplos de respuestas y aserciones de estado/regla. Se verificó su sintaxis JSON; no se ejecutó Postman ni un runner. Los ejemplos son ilustrativos, no transcripciones de una ejecución real.

Arrancar la instancia de ensayo según Docs/validacion-manual-baja-socios.md: datasource socioclub_test con socioclub_tests, vinculada a 127.0.0.1:8081. Duplicar el entorno local y fijar baseUrl=http://127.0.0.1:8081; el archivo importado conserva 8080 como ejemplo general, no utilizarlo para escrituras de este ensayo. Comenzar por consultas y comprobar los IDs/DNI del fixture. socioId=1 y actividadId=8 son ejemplos; no asumir que AUTO_INCREMENT conserva esos valores. Revisar todas las variables antes de enviar escrituras. No dirigir pruebas ni bajas a desarrollo.

El grupo ABM crea una actividad de ensayo, captura su ID en actividadCreadaId, la edita y elimina si continúa sin historial. Alta/baja utiliza socioId/actividadId elegidos y captura inscripcionCreadaId. Los scripts solo capturan IDs si la respuesta es exitosa. Un fallo previo puede dejar variables viejas: revisarlas antes de continuar. No ejecutar la colección completa a ciegas: los escenarios de reglas no se preparan automáticamente.

Para cada caso RN1–RN5 completar rn1SocioId/rn1ActividadId (y equivalentes) con datos que incumplan la regla esperada. RN1: socio apto, sin duplicado ni máximo y actividad llena; RN2: inscripción ACTIVA ya existente; RN3: edad fuera del rango con cuota al día; RN4: MOROSO; RN5: tres ACTIVAS y actividad nueva. No debe haber otra regla de mayor prioridad que altere el resultado (cuota, edad, duplicado, máximo, cupo). Los IDs de escenarios están vacíos a propósito: no se inventan fixtures ni se promete que los datos iniciales satisfagan todos los casos.

Las bajas no eliminan historial: una actividad con BAJAS sigue protegida por la FK y su eliminación devuelve 409. No borrar inscripciones para conseguir un 204. La aplicación no tiene autenticación/autorización ni CSRF; CORS local no es autenticación. Solo demostración local hasta incorporar seguridad adicional.

## Migraciones confirmadas; recorrido manual pendiente

El usuario confirmó migración manual de ambas bases y verify 128 aprobadas, cero fallos/errores/omisiones. No se atribuye esa ejecución a Codex ni implica que Postman se haya ejecutado. La carpeta de baja se utiliza solo en la instancia de testing 8081 y con socioBajaId elegido del fixture ficticio. Incluye baja, repetición, inexistente e inscripción de INACTIVO. No hay reactivación. En este procedimiento no ejecutar la colección completa ni los requests DELETE: preparar y enviar los casos individualmente; los escenarios RN requieren sus propios fixtures. Ver Docs/validacion-manual-baja-socios.md. Se mantienen pendientes la validación visual y los resultados HTTP/JSON manuales posteriores a migración.
