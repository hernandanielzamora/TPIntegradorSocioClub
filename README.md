# SocioClub

Trabajo Práctico Integrador de Taller de Construcción de Software — Universidad Siglo 21. Sistema para el Club Social y Deportivo Los Algarrobos, Córdoba.

**Etapa completada: inicialización estructural del Prompt 01.** Sitio público inicial, backend MVC y app React mínima. Sin CRUD, entidades, tablas, datos, reglas, ingreso ni consultas reales. Sin despliegues. El usuario autorizó el primer commit y la publicación en [TPIntegradorSocioClub](https://github.com/hernandanielzamora/TPIntegradorSocioClub); el resultado efectivo se verifica en el historial de Git y el informe de la sesión.

## Estructura

```text
.
├── sitio-publico/      HTML5, CSS3, JavaScript y assets
├── backend/            Maven Wrapper, Spring Boot, MVC/REST y Thymeleaf
├── app-socios/         Create React App
├── database/           schema.sql y seed.sql, solo comentarios
├── postman/            colección pendiente
├── Docs/               enunciado, trazabilidad y registros
├── .vscode/            recomendaciones de extensiones
├── .editorconfig
├── .gitignore
├── render.yaml         preparación futura, sin despliegue
└── README.md
```

[Árbol completo real de archivos fuente](Docs/estructura.md). Se conserva la carpeta existente `Docs/`, equivalente a `docs/` en este workspace Windows; los enlaces respetan su capitalización para Linux.

## Herramientas y dependencias

| Herramienta detectada | Versión |
| --- | --- |
| Git | 2.47.1.windows.1 |
| Node.js | 24.22.2 |
| npm vía npm.cmd | 11.19.1 |
| Java / javac | Oracle JDK 25.0.1 |
| Maven Wrapper | 3.10.0; wrapper 3.3.4 |
| Maven global | No encontrado en PATH |
| Cliente mysql | No encontrado en PATH |

No se encontró JDK 21; se utiliza Java 25, compatible con Spring Boot 4.1.2. No se cambiaron versiones ni se instalaron herramientas globales. PowerShell bloquea `npm.ps1`; los comandos usan `npm.cmd` sin cambiar la política de ejecución.

Backend: Spring Boot **4.1.2**; starters Web MVC, Thymeleaf, Data JPA y Validation; MySQL Connector/J **9.7.0**; Spring Boot Test y Web MVC Test **4.1.2** (JUnit Jupiter **6.0.3**), administrados por el parent de Boot. Maven Wrapper incluido; no requiere Maven global.

Frontend: generador CRA **5.1.0**, react-scripts **5.0.1**, React/React DOM **18.3.1**, React Router DOM **6.30.6**, Axios **1.21.0**, PropTypes **15.8.1**. Jest **27.5.1** mediante react-scripts; React Testing Library **16.3.3**. El archivo `package-lock.json` fija la instalación reproducible.

CRA está [deprecado](https://react.dev/blog/2025/02/14/sunsetting-create-react-app), pero se conserva por requisito docente. Se eligió React 18 porque [React 19 ignora PropTypes](https://react.dev/blog/2024/04/25/react-19-upgrade-guide#removed-proptypes-and-defaultprops-for-functions), exigido en la materia. No se usa Vite, Lombok, Redux, Tailwind ni Next.js en el código de las aplicaciones.

## Prerrequisitos locales

JDK 25, Node 24 con npm, Git y acceso a Internet para descargar dependencias. MySQL operativo con una base y un usuario con permisos para poder arrancar el backend completo. Python 3 es opcional para servir los archivos estáticos; también puede usarse Live Server de VS Code.

Abrir terminales PowerShell independientes desde la raíz del monorepo.

### Sitio público

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory .\sitio-publico
```

Abrir `http://localhost:5500/index.html`. La navegación entre las tres páginas funciona; catálogo y formulario se anuncian como pendientes. Alternativa sin servidor: `Start-Process .\sitio-publico\index.html`.

### Base MySQL y backend

Si se dispone del cliente MySQL y una cuenta administradora local:

```powershell
mysql.exe -u root -p -e "CREATE DATABASE IF NOT EXISTS socioclub CHARACTER SET utf8mb4;"
```

Este comando solo prepara la base vacía; no se ejecutó en esta sesión. `database/schema.sql` y `database/seed.sql` contienen comentarios, no crean tablas ni cargan datos.

En una terminal PowerShell:

```powershell
Set-Location .\backend
$env:DB_URL = 'jdbc:mysql://localhost:3306/socioclub'
$env:DB_USERNAME = '<usuario_mysql_local>'
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contraseña MySQL' -AsSecureString)).Password
$env:PORT = '8080'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:3000,http://localhost:5500'
.\mvnw.cmd spring-boot:run
```

Reemplazar el usuario por una cuenta real con acceso a la base. Abrir `http://localhost:8080/`. Esta pantalla inicial requiere que Spring pueda conectarse a MySQL. **No se verificó el arranque completo ni una conexión MySQL en esta sesión.**

Las variables se leen desde `application.properties`; `backend/.env.example` solo documenta nombres y no se carga automáticamente. Hibernate está configurado con `ddl-auto=none` y sin inicialización SQL.

Para compilar y ejecutar la prueba MVC aislada, sin conexión MySQL:

```powershell
Set-Location .\backend
.\mvnw.cmd -B verify
```

Se genera `backend/target/socioclub-0.0.1-SNAPSHOT.jar`. El sufijo SNAPSHOT es la versión del proyecto en desarrollo; Spring Boot utiliza una versión estable.

### App React

En otra terminal desde la raíz:

```powershell
Set-Location .\app-socios
npm.cmd ci
$env:REACT_APP_API_URL = 'http://localhost:8080/api'
npm.cmd start
```

Abrir `http://localhost:3000/`. Solo muestra la pantalla de bienvenida; el cliente Axios está preparado pero no hace peticiones. `REACT_APP_API_URL` es pública y se incorpora al compilar; nunca usarla para contraseñas.

Compilación y comprobación del runner de pruebas, desde `app-socios`:

```powershell
npm.cmd run build
npm.cmd test -- --watchAll=false --runInBand --passWithNoTests
```

No hay pruebas React implementadas todavía; `--passWithNoTests` no acredita pruebas exitosas. La salida estática queda en `app-socios/build/`. Detener servidores con Ctrl+C.

## Validación observada

Maven `verify`: **BUILD SUCCESS**, jar generado y 1 prueba MVC aprobada (0 fallos/errores). `npm ci` y `npm run build`: exitosos con React 18.3.1. Jest: ejecutado sin pruebas disponibles. Detalle de comprobaciones, fallos corregidos y límites en [validación inicial](Docs/validacion-inicial.md).

npm reportó **75 vulnerabilidades** (11 bajas, 51 moderadas, 13 altas) en el árbol instalado de CRA. No se ejecutó `npm audit fix --force`; queda pendiente evaluar esas dependencias conservando los requisitos docentes. La compilación no acredita seguridad ni funcionalidad de negocio.

## Documentación y trazabilidad

- [Enunciado principal](Docs/Enunciado/TPIntegradorSocioClubEnunciado.pdf).
- [Arquitectura y decisiones](Docs/arquitectura.md).
- [Matriz RF1–RF10 y RN1–RN7](Docs/requerimientos.md): todos en estado Pendiente.
- [Bitácora de IA — Anexo C](Docs/bitacora-ia.md): prompts, resultados, revisiones y correcciones reales.
- [Registro de trabajo manual](Docs/registro-trabajo.md): intervenciones del grupo, decisiones, validación y commits asociados.
- [Seis escenarios de sistema previstos](Docs/pruebas-sistema.md): sin ejecutar.
- [Preparación de alojamiento gratuito](Docs/alojamiento.md): Cloudflare Pages, Render y Aiven, sin desplegar.
- [Inventario de archivos](Docs/estructura.md) y [resultados de validación](Docs/validacion-inicial.md).

## Pendientes e integrantes

Integrantes y número de grupo pendientes de informar (el PDF exige 4 o 5 personas). No existen DNI de prueba ni intervenciones manuales confirmadas. Quedan para etapas posteriores RF1–RF10, RN1–RN7, modelo JPA, SQL y datos exactos del PDF, endpoints, pantallas de gestión, pruebas funcionales, colección Postman e informe PDF de 8–12 páginas.

El deploy no se ejecutó. Se preparan Dockerfile y blueprint Render, variables de entorno, CORS y fallback SPA para Pages. Falta validar Docker, conexión TLS a Aiven y los dominios reales antes de desplegar. Las vistas Thymeleaf se alojarán con el backend, no como frontend estático.
