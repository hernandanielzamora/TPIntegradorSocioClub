# Guion editable de presentación — SocioClub

Versión: dev 4570207. Distribución propuesta para Hernán, Mateo, Matías y Fernando; no implica autoría histórica ni roles definitivos. Completar aportes personales solo con confirmación. Nombres completos, grupo y fecha siguen pendientes.

## Preparación antes del turno

Abrir sitio :5500, Secretaría del entorno acordado, React :3000 y VS Code. Mantener MySQL y dependencias disponibles sin depender de Internet. Abrir InscripcionService, ApiController, api.js y Catalogo. Elegir socio y actividad por lectura: no asumir IDs del seed. Las escrituras de demo se hacen solo en testing autorizado, backend :8081 y React apuntando a esa API. Revisar vigencia del fixture: una baja anterior no se revierte automáticamente.

## Presentación — 2 minutos

**0:00–1:00, Hernán:** “El club administraba socios y actividades con registros dispersos. SocioClub centraliza la inscripción y evita sobrecupos, duplicados, edades incompatibles y altas de morosos. Mostramos una versión académica local”. Presentar integrantes y contribuciones verificadas, sin atribuir código Codex como edición personal.

**1:00–2:00, Fernando:** “Construimos sitio público, Secretaría MVC y Mi Club React. Las operaciones persistentes comparten servicios Spring y MySQL. El formulario público solo valida localmente; no tenemos autenticación segura ni control RN8”. Mostrar arquitectura y declarar validaciones pendientes si las hubiera al exponer.

## Demostración — 7 minutos

**2:00–3:30, Matías:** sitio público, tarjetas, disciplina/edad y formulario inválido. Explicar mensaje local del formulario válido sin simular recepción o almacenamiento.

**3:30–5:30, Hernán:** Secretaría, búsqueda paginada, actividades y cupo. Mostrar alta solo en ensayo previamente autorizado; no dar de baja un socio de desarrollo para demostrar el botón.

**5:30–8:00, Mateo:** ingreso por DNI ficticio existente, catálogo y detalle, Mis actividades y resumen RN6. Una inscripción exitosa debe reflejarse en lista y cupo; baja de inscripción pide confirmación y conserva historial. Usar únicamente datos de ensayo acordados.

**8:00–9:00, Fernando:** rechazo previamente preparado, por ejemplo duplicado ACTIVA de socio habilitado. Mostrar aviso y respuesta 409/RN2, más ausencia de fila adicional. Si aparece otra regla, explicar la prioridad y revisar precondiciones; no modificar datos a escondidas. Este caso está propuesto: no se registra como ejecutado por tener este guion.

## Recorrido técnico — 3 minutos

**9:00–12:00, los cuatro:** el docente elige la funcionalidad. Para alta: Mateo sigue tarjeta/callback y Axios; Fernando explica JSON/controller/DTO; Hernán servicio, transacción y locks; Matías repositorio, FK, estados y actualización de pantalla. Este reparto es editable y todos deben poder completar cualquier tramo. Explicar una prueba positiva y una negativa y qué no cubren.

Cerrar: “Las reglas se centralizan y el historial se conserva. Tenemos evidencia automática y de lectura HTTP, con límites explícitos para pruebas integrales y seguridad”. No anunciar resultados pendientes como aprobados.

## Defensa y devolución posteriores

El enunciado reserva además 6 minutos de defensa individual y 2 de devolución; máximo general 20/25 minutos. Usar las sesenta preguntas de la guía para ensayar sin memorizar frases. Registrar devoluciones auténticas después de la clase, no anticiparlas.

## Plan de contingencia

Sin Internet, continuar servidores locales ya preparados. Sin MySQL, diagnosticar servicio/puerto y mostrar código y evidencia previa claramente identificada; no fingir una escritura. Si React no conecta, comprobar API, URL/puertos y CORS sin exponer .env. Si no se recupera a tiempo, avanzar al recorrido técnico y reconocer el paso no demostrado. Capturas reales pueden apoyar, pero no sustituyen el demo en vivo exigido. No reinicializar la base ni ejecutar migraciones durante la exposición.
