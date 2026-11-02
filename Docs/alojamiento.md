# Preparación de alojamiento — sin despliegue

Configuración preparada para una etapa posterior. No se publicaron sitios ni se crearon recursos remotos. Las restricciones y disponibilidad de planes deben confirmarse nuevamente al desplegar.

## Cloudflare Pages

Crear en el futuro dos proyectos del mismo monorepo:

| Proyecto | Directorio raíz | Comando de compilación | Salida |
| --- | --- | --- | --- |
| Sitio público | `sitio-publico` | `exit 0` (sin compilación) | `.` |
| App socios | `app-socios` | `npm ci && npm run build` | `build` |

En el entorno de compilación React establecer `NODE_VERSION=24` y `REACT_APP_API_URL=https://<backend>.onrender.com/api`. La URL es pública y queda incluida en el bundle. El archivo `app-socios/public/_redirects` prepara el fallback a `index.html` para navegación SPA. No definir secretos en variables `REACT_APP_*`.

Fuentes oficiales: [HTML estático](https://developers.cloudflare.com/pages/framework-guides/deploy-anything/), [configuración de compilación](https://developers.cloudflare.com/pages/configuration/build-configuration/), [redirects](https://developers.cloudflare.com/pages/configuration/redirects/), [límites](https://developers.cloudflare.com/pages/platform/limits/). La guía React actual propone otro generador; aquí se mantiene CRA por exigencia docente.

## Render

`render.yaml` describe un servicio Docker de plan `free`, con raíz `backend` y despliegue automático desactivado. `backend/Dockerfile` compila con JDK 25 y ejecuta el jar. Su construcción no requiere conexión MySQL. Docker no se ejecutó en esta etapa: falta validar la imagen y el despliegue real.

Completar las variables de ejecución en el proveedor, nunca en archivos versionados:

- `DB_URL`: URL JDBC de Aiven, por ejemplo `jdbc:mysql://<host>:<puerto>/defaultdb?sslMode=VERIFY_IDENTITY`.
- `DB_USERNAME` y `DB_PASSWORD`: credenciales del servicio.
- `CORS_ALLOWED_ORIGINS`: orígenes exactos de ambos proyectos Pages, separados por comas, sin barras finales. Agregar explícitamente otros orígenes de prueba si se necesitan; no se permite un comodín global.
- `PORT`: Render la proporciona; Spring la utiliza y escucha por defecto en todas las interfaces.

La comprobación de salud apunta a `/`, que renderiza la página inicial de secretaría. JPA requiere conexión correcta a MySQL al arrancar. No se omite la conexión ni se utiliza una base simulada.

Fuentes oficiales: [Docker en Render](https://render.com/docs/docker), [Blueprint](https://render.com/docs/blueprint-spec), [plan gratuito](https://render.com/docs/free). El servicio gratuito se suspende por inactividad y tiene límites de uso; no se promete disponibilidad continua ni ausencia de límites.

## Aiven MySQL

Crear el servicio gratuito solo cuando se autorice desplegar. Usar host, puerto, base y credenciales suministrados por Aiven. Descargar su certificado CA y configurar confianza TLS cuando corresponda; no desactivar la validación del servidor para evitar errores.

Connector/J admite `sslMode=VERIFY_IDENTITY`. Si la CA del servicio no está en el almacén Java, preparar fuera del repositorio un truststore que la contenga y configurar `trustCertificateKeyStoreUrl`, `trustCertificateKeyStoreType` y `trustCertificateKeyStorePassword` mediante configuración protegida. La ubicación del archivo dependerá del montaje de secretos de Render. No incluir contraseñas en la URL JDBC ni en logs. Probar conexión y validación TLS antes del despliegue final.

`schema.sql` y `seed.sql` solo contienen comentarios: no hay tablas ni datos todavía. No ejecutar estos archivos esperando una aplicación completa.

Fuentes oficiales: [Aiven MySQL gratuito](https://aiven.io/docs/products/mysql/concepts/mysql-free-tier), [conexión con Java](https://aiven.io/docs/products/mysql/howto/connect-with-java), [TLS de Connector/J](https://dev.mysql.com/doc/connector-j/en/connector-j-reference-using-ssl.html).
