# Sitio público — cierre de la Parte A

Se conservaron la portada, marca, paleta, navegación y formulario existentes. Se completó actividades.html y script.js, se ampliaron estilos y se agregó una ilustración SVG original identificada como tal (no foto de instalaciones). Referencia: enunciado original, Parte A/página 6 y ocho actividades de sección 3.3/página 4. Los datos institucionales se limitan al caso: club barrial de zona norte de Córdoba y gestión anterior en cuadernos/planillas; no se inventan fecha de fundación o domicilio.

## Comportamiento real

Actividad tiene constructor, validaciones de datos iniciales y método esAptaParaEdad con límites inclusivos. Ocho instancias forman el catálogo estático del enunciado, coherente con seed.sql. NO es el catálogo vivo de MySQL: la base consultada en este cierre tenía nueve actividades. Mostrar cupo máximo no equivale a vacantes actuales. React/Secretaría sí consultan el backend real.

Las tarjetas article se generan solo por DOM con createElement/textContent y dl de horarios, edades, profesor, cupo y cuota. addEventListener combina disciplina/edad y muestra resultados vacíos o error de edad inválida; Limpiar restaura ocho tarjetas. No hay fetch ni tarjetas duplicadas en HTML. El enlace prepara preinscripcion.html?actividad=slug; location.search preselecciona únicamente un identificador interno del catálogo, nunca DNI/email en URL. Esto es uso funcional del objeto location. No se cambia historia/URL para decorar requisitos.

El formulario valida nombre/apellido obligatorios hasta 80, email hasta 150, DNI RN7 de 7/8 dígitos sin puntos/espacios y actividad conocida. preventDefault impide envío en todos los casos. Los errores se muestran en small asociados por aria-describedby y aria-invalid; foco en el primero inválido y mensaje de estado accesible al validar. Sin alert. Sin JavaScript el botón está deshabilitado y se explica el límite. No se guardan datos personales en localStorage/sessionStorage ni se envían al servidor.

**Alcance exacto RF3:** se cumple el mínimo de Parte A (formulario validado y resultado local), pero NO hay recepción remota ni registro de preinscripciones en MySQL. El éxito dice explícitamente que no se enviaron/guardaron y remite a Secretaría. La inscripción persistente del SOCIO es otro flujo, React/MVC → API/servicio → MySQL. El Anexo A no especifica endpoint de preinscripción; no se inventó uno ni se presenta el formulario como envío registrado. Si la cátedra interpreta RF3 como recepción remota adicional, aclarar este alcance antes de entregar; no ocultarlo como una captura exitosa de persistencia.

## Estructura y accesibilidad

Tres HTML con header/nav/main/section/footer, un h1 por página, h2/h3 jerárquicos. Portada: article informativo y aside para ubicación/atención; catálogo: article dinámicos y aside para distinguir datos iniciales de vacantes; no se agregaron etiquetas sin propósito. Enlaces y recursos locales, viewport, imagen con alt y dimensiones. CSS externo con selectores de tipo/clase/ID/atributo, foco visible, errores por aria-invalid y media queries: tres columnas, dos hasta 60rem y una hasta 40rem. Se respeta prefers-reduced-motion.

No se hicieron validación W3C ni revisión visual de contraste/desbordamiento en un navegador; esos controles no se deducen de que jsdom pueda parsear HTML/CSS. La paleta/focos se conservan o amplían, sin rediseño completo.

## Pruebas ejecutadas

node --check sitio-publico/js/script.js: correcto. node --test sitio-publico/tests/script.test.cjs: **25 aprobadas, cero fallos/omisiones**. Node test runner y jsdom ya fijado por react-scripts, sin dependencias nuevas; ejecutar npm.cmd ci en app-socios si falta node_modules. Los tests cubren constructor/edad, coherencia textual con seed, filtros, DOM/recursos/IDs, RN7, email, campos vacíos, enlace de preselección, foco, prevención de envío y almacenamiento, reset y CSS responsivo declarado. Consola jsdom sin errores; no equivale a consola Chrome real.

Servidor HTTP Python temporal en loopback, apagado al terminar: index.html, actividades.html, preinscripcion.html, CSS, JS y SVG devolvieron 200. No hubo POST ni bases modificadas. Evidencia local de tests en .cache/prompt22-sitio-tests.txt (ignorado).

## Ejecutar y revisar manualmente

Desde la raíz, terminal 1:

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory .\sitio-publico
```

Abrir http://localhost:5500/index.html, recorrer las tres páginas, combinar Fútbol/12 (un resultado) y Fútbol/13 (vacío), edad -1 (error), limpiar; seguir enlace de Yoga y comprobar preselección. En formulario probar vacíos, DNI con puntos/6/9 dígitos/email inválido y datos ficticios válidos: el resultado debe decir validación LOCAL, no registro. Navegar con Tab/Enter y comprobar foco. En DevTools revisar consola y ausencia de requests POST; a 360/768/1280 px verificar tarjetas, menú y formulario sin desbordamiento. No generar capturas ficticias ni probar con datos personales reales. Capturas/lista global en cierre-desarrollo.md.
