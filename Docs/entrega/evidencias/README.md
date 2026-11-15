# Evidencia visual auténtica

Las diez PNG se obtuvieron con Chrome headless real mediante CDP contra servidores locales temporales. Se capturaron archivos actuales y el build React real, sin simular API o sesiones. Los servidores de captura se cerraron. `verificacion-navegador.json` registra URLs y tamaños: sin excepciones JavaScript ni desbordes horizontales en esas vistas. Esto no es una prueba completa de accesibilidad ni un recorrido E2E con MySQL.

- Sitio: inicio, catálogo, filtro Fútbol, formulario, errores y tres vistas móviles de 390 × 844.
- React: ingreso y error RN7 con DNI vacío.
- **No disponibles:** Secretaría, React autenticado, Mis actividades y cuota. Backend/MySQL no estaban activos al inspeccionar este entorno.

## Cómo completar lo que falta

No ejecutar seed, migraciones ni suites MySQL para obtener capturas. Utilizar la base existente configurada por el equipo, con personas ficticias.

1. Iniciar MySQL con el procedimiento habitual del equipo. Desde `backend`, cargar `.\scripts\load-env.ps1` y ejecutar `.\mvnw.cmd '-Dspring-boot.run.arguments=--server.port=8080' spring-boot:run`. Las claves permanecen en `.env` local ignorado.
2. En el navegador abrir `http://localhost:8080/secretaria`. Capturar a 1440 × 1000 el panel, listado/búsqueda de socios, actividades con cupos e inscripciones. Guardar como `secretaria-panel.png`, `secretaria-socios.png`, `secretaria-actividades.png`, `secretaria-inscripciones.png`.
3. Desde `app-socios`, definir `$env:PORT='3000'` y `$env:REACT_APP_API_URL='http://localhost:8080/api'`, ejecutar `npm.cmd start`. Abrir `http://localhost:3000`, ingresar un DNI ficticio verificado del seed y capturar `react-catalogo.png`, `react-mis-actividades.png` y `react-cuota.png`. El ingreso por DNI es académico, no autenticación robusta.
4. Recorrer las pruebas PS01–PS06 únicamente con datos y escrituras autorizados; anotar IDs, precondiciones, respuesta y estado posterior. Guardar evidencias y completar `Docs/pruebas-sistema.md`.
5. Agregar las capturas y resultados al contenido de `herramientas/generar_informe.py`, regenerar y revisar el PDF. Reemplazar el aviso de evidencia pendiente solo cuando existan las imágenes reales.

No publicar credenciales, datos de personas reales, ventanas de configuración privada ni tokens. Las ilustraciones SVG del sitio son recursos gráficos del proyecto, no fotografías de instalaciones.
