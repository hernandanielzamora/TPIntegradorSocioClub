# SocioClub

Trabajo Práctico Integrador de **Taller de Construcción de Software**, Universidad Siglo 21.

Sistema destinado al Club Social y Deportivo Los Algarrobos, Córdoba.

## Estado actual del proyecto

Backend JPA/MySQL, RN1–RN5, MVC de Secretaría y nueve endpoints REST del Anexo A implementados; baja lógica de socios adicional, con vigencia independiente de cuota y conservación del historial. Migración aplicada manualmente a testing y desarrollo tras respaldos mysqldump. Verify autenticado confirmado: **128 aprobadas, 0 fallos, 0 errores, 0 omitidas**, antes de cargar el fixture manual. El usuario confirmó el ensayo de Secretaría sobre testing:8081; HTTP 409, idempotencia y valores finales exactos aún requieren evidencia específica. No ejecutar nuevamente suites MySQL que exigen tablas vacías: testing contiene el fixture, no limpiarlo automáticamente.

Contrato: [API REST](Docs/api-rest.md). [Ensayo manual y fixture convencional](Docs/validacion-manual-baja-socios.md). [Configuración privada MySQL](Docs/mysql-local.md). No hay autenticación/autorización/CSRF: demostración académica local, sin exposición pública ni despliegue. Respaldos fuera de Git, sin restauración de ensayo confirmada. Backend integrado en dev; esta rama React conecta ingreso, catálogo/detalle e inscripciones mediante Axios real y conserva el trabajo del equipo. 58 pruebas frontend aprobadas; conexión real en navegador pendiente. Ver [integración React](Docs/react-integracion-api.md). React no se fusiona todavía a dev. El sitio público no cambia.
## Propósito

SocioClub es un proyecto académico que busca desarrollar un sistema para gestionar las actividades y los socios de un club deportivo.

El trabajo integrador contempla un sitio público, un backend con Spring Boot, una aplicación para socios con React, una API REST, persistencia MySQL y documentación técnica.

La implementación se realizará por etapas, siguiendo los requisitos funcionales y las reglas de negocio del enunciado.

## Estructura del proyecto

```text
.
├── sitio-publico/      HTML5, CSS3, JavaScript y assets
├── backend/            Maven Wrapper, Spring Boot, MVC/REST y Thymeleaf
├── app-socios/         Create React App
├── database/           schema.sql y seed.sql, carga manual
├── postman/            colección REST y entorno local
├── Docs/               enunciado, trazabilidad y registros
├── .vscode/            recomendaciones de extensiones
├── .editorconfig
├── .gitignore
├── render.yaml         preparación futura, sin despliegue
└── README.md
```

El [árbol completo de archivos fuente](Docs/estructura.md) contiene el inventario detallado.

Se conserva la carpeta existente `Docs/`. Los enlaces respetan su capitalización para mantener compatibilidad con sistemas Linux.

## Herramientas y dependencias

### Herramientas detectadas

| Herramienta | Versión o estado |
| --- | --- |
| Git | 2.47.1.windows.1 |
| Node.js | 24.22.2 |
| npm vía `npm.cmd` | 11.19.1 |
| Java / javac | Oracle JDK 25.0.1 |
| Maven Wrapper | 3.10.0; wrapper 3.3.4 |
| Maven global | No encontrado en PATH |
| Cliente MySQL | No encontrado en PATH |

No se encontró JDK 21. En el entorno utilizado se emplea Java 25 con Spring Boot 4.1.2. No se cambiaron versiones ni se instalaron herramientas globales.

En PowerShell, la ejecución de `npm.ps1` está bloqueada en el entorno utilizado. Por eso, los comandos de npm de esta documentación utilizan `npm.cmd`, sin modificar la política de ejecución.

### Backend

- Spring Boot 4.1.2.
- Spring Web MVC.
- Thymeleaf.
- Spring Data JPA.
- Spring Validation.
- MySQL Connector/J 9.7.0.
- Spring Boot Test y Web MVC Test 4.1.2.
- JUnit Jupiter 6.0.3, administrado por el parent de Spring Boot.
- Maven Wrapper incluido en el proyecto.

No se requiere una instalación global de Maven.

### Frontend

- Create React App 5.1.0.
- `react-scripts` 5.0.1.
- React y React DOM 18.3.1.
- React Router DOM 6.30.6.
- Axios 1.21.0.
- PropTypes 15.8.1.
- Jest 27.5.1, mediante `react-scripts`.
- React Testing Library 16.3.3.

El archivo `package-lock.json` fija las versiones de las dependencias para la instalación.

Create React App está [deprecado](https://react.dev/blog/2025/02/14/sunsetting-create-react-app), pero se conserva por requisito docente. Se utiliza React 18 porque [React 19 eliminó el soporte de PropTypes para componentes](https://react.dev/blog/2024/04/25/react-19-upgrade-guide#removed-proptypes-and-defaultprops-for-functions), requisito de la materia.

No se utilizan Vite, Lombok, Redux, Tailwind ni Next.js en el código de las aplicaciones.

## Prerrequisitos locales

Para trabajar con el proyecto se requieren:

- Git.
- JDK 25.
- Node.js 24 con npm.
- Acceso a Internet para descargar dependencias.
- MySQL operativo, con una base de datos y un usuario con permisos, para poder arrancar el backend completo.

Python 3 es opcional para servir los archivos estáticos del sitio público. Como alternativa, puede utilizarse Live Server de VS Code.

Los comandos siguientes están documentados para PowerShell. Se recomienda abrir terminales independientes desde la raíz del monorepo.

## Inicio de las aplicaciones

### 1. Sitio público

Desde la raíz del proyecto, ejecutar:

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory .\sitio-publico
```

Abrir en el navegador:

[http://localhost:5500/index.html](http://localhost:5500/index.html)

La navegación entre las tres páginas funciona. El catálogo y el formulario se encuentran pendientes de implementación funcional.

Alternativa sin servidor:

```powershell
Start-Process .\sitio-publico\index.html
```

### 2. Base MySQL y backend

Si se dispone del cliente MySQL y de una cuenta administradora local, se puede preparar una base vacía con:

```powershell
mysql.exe -u root -p -e "CREATE DATABASE IF NOT EXISTS socioclub CHARACTER SET utf8mb4;"
```

**Importante:** este comando solamente prepara la base vacía. No se ejecutó en esta sesión.

Después de crear la base, abrir el cliente MySQL desde la raíz del repositorio y ejecutar:

```sql
USE socioclub;
SOURCE database/schema.sql;
SOURCE database/seed.sql;
```

Los scripts están preparados y no se ejecutaron en esta sesión. No borran ni actualizan datos existentes. Revisar tablas y actividades homónimas antes de cargar una base poblada. Ver [procedimiento completo](Docs/mysql-local.md).

Para iniciar el backend, abrir una terminal PowerShell y ejecutar:

```powershell
Set-Location .\backend
$env:DB_URL = 'jdbc:mysql://localhost:3306/socioclub'
$env:DB_USERNAME = '<usuario_mysql_local>'
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contraseña MySQL' -AsSecureString)).Password
$env:PORT = '8080'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:3000,http://localhost:5500'
.\mvnw.cmd spring-boot:run
```

Reemplazar `<usuario_mysql_local>` por una cuenta real que tenga acceso a la base de datos.

Abrir:

[http://localhost:8080/](http://localhost:8080/)

La pantalla inicial requiere que Spring pueda conectarse a MySQL.

**No se verificó el arranque completo del backend ni una conexión real a MySQL en esta sesión.**

Las variables se leen desde `application.properties`. El archivo `backend/.env.example` documenta los nombres de las variables, pero no se carga automáticamente.

Hibernate usa `ddl-auto=validate`, sin generación de tablas ni inicialización SQL automática. Ejecutar el esquema antes del arranque.

#### Compilación y pruebas sin MySQL

Para compilar y ejecutar las pruebas de integridad en memoria y MVC sin MySQL, desde PowerShell (las siete pruebas MySQL se omiten salvo activación explícita):

```powershell
Set-Location .\backend
.\mvnw.cmd -B verify
```

Se genera el archivo:

```text
backend/target/socioclub-0.0.1-SNAPSHOT.jar
```

El sufijo `SNAPSHOT` corresponde a la versión del proyecto en desarrollo. Spring Boot utiliza una versión estable.

### 3. Aplicación React

Abrir otra terminal PowerShell desde la raíz del monorepo:

```powershell
Set-Location .\app-socios
npm.cmd ci
$env:REACT_APP_API_URL = 'http://localhost:8080/api'
npm.cmd start
```

Abrir:

[http://localhost:3000/](http://localhost:3000/)

La aplicación incluye ingreso por DNI, catálogo, detalle y Mis Actividades con datos locales del PR #2. La conexión a la API mediante Axios sigue pendiente.

`REACT_APP_API_URL` es una variable pública que se incorpora al compilar la aplicación. No debe utilizarse para almacenar contraseñas ni otros secretos.

#### Compilación y comprobación del runner de pruebas

Desde `app-socios`:

```powershell
npm.cmd run build
npm.cmd test -- --watchAll=false --runInBand
```

El PR #2 incorporó pruebas Jest y React Testing Library. En el cierre del 2026-11-08 se ejecutaron: 20 aprobadas, 0 fallidas y 0 omitidas, además del build exitoso. No se modificaron fuentes ni dependencias de los frontends.

La compilación estática queda en:

```text
app-socios/build/
```

Para detener los servidores, utilizar `Ctrl+C` en cada terminal.

## Validación histórica de inicialización y límites

Las siguientes comprobaciones son históricas (Prompt 01). Ver el resultado actual en [validación del modelo](Docs/validacion-modelo-datos.md):

| Comprobación | Resultado registrado |
| --- | --- |
| Maven `verify` | BUILD SUCCESS |
| Prueba MVC del backend | 1 aprobada, 0 fallos y 0 errores |
| Generación del JAR | Exitosa |
| `npm ci` | Exitoso |
| `npm run build` | Exitoso con React 18.3.1 |
| Ejecución de Jest | Ejecutada sin pruebas disponibles |
| Conexión real a MySQL | No verificada |
| Arranque completo del backend con MySQL | No verificado |
| Funcionalidades de negocio | Pendientes |
| Despliegue | No realizado |

El detalle de las comprobaciones, los fallos corregidos y sus límites está en [validación inicial](Docs/validacion-inicial.md).

### Advertencia sobre dependencias

Durante la instalación, npm reportó **75 vulnerabilidades** en el árbol de dependencias:

- 11 bajas.
- 51 moderadas.
- 13 altas.

No se ejecutó `npm audit fix --force`. Queda pendiente evaluar las dependencias afectadas, conservando los requisitos docentes.

Una compilación exitosa no acredita la seguridad de las dependencias ni el funcionamiento de las reglas de negocio.

## Organización de ramas y flujo de trabajo

El repositorio utiliza tres tipos de ramas con responsabilidades diferentes:

| Rama | Propósito |
| --- | --- |
| `main` | Rama principal y estable del proyecto. |
| `dev` | Rama de integración del trabajo en desarrollo. |
| `feature/*` | Ramas de trabajo para implementar tareas o funcionalidades específicas. |

### Flujo de trabajo

1. Partir de la rama `dev` actualizada para iniciar una tarea.
2. Crear una rama `feature/*` para trabajar de forma aislada.
3. Realizar los cambios correspondientes a esa tarea.
4. Revisar los archivos modificados y ejecutar las comprobaciones que correspondan.
5. Publicar la rama de trabajo en el repositorio remoto.
6. Abrir un Pull Request desde `feature/*` hacia `dev`.
7. Revisar los cambios y resolver las observaciones antes de integrar.
8. Mantener `main` como rama principal estable; no utilizarla como rama de trabajo cotidiana.

Los cambios de cada tarea deben limitarse a los archivos necesarios. La integración mediante Pull Request permite revisar el trabajo antes de incorporarlo a `dev`.

La existencia y el estado efectivo de cada rama o Pull Request deben comprobarse en GitHub. Esta sección documenta el flujo de trabajo previsto; no afirma que todas las tareas o integraciones ya se hayan realizado.

## Documentación y trazabilidad

La carpeta `Docs/` reúne el enunciado, las decisiones técnicas, los registros de trabajo y la documentación de las etapas del proyecto.

- [Enunciado principal](Docs/Enunciado/TPIntegradorSocioClubEnunciado.pdf): consigna del trabajo integrador.
- [Arquitectura y decisiones](Docs/arquitectura.md): organización y decisiones técnicas.
- [Matriz RF1–RF10 y RN1–RN7](Docs/requerimientos.md): requisitos, reglas y avances parciales; cumplimiento integral pendiente.
- [Bitácora de IA — Anexo C](Docs/bitacora-ia.md): prompts, resultados, revisiones y correcciones reales.
- [Registro de trabajo manual](Docs/registro-trabajo.md): intervenciones del grupo, decisiones, validaciones y commits asociados.
- [Pruebas de sistema](Docs/pruebas-sistema.md): seis escenarios previstos, todavía sin ejecutar.
- [Preparación de alojamiento gratuito](Docs/alojamiento.md): documentación sobre Cloudflare Pages, Render y Aiven; sin despliegue.
- [Inventario de archivos](Docs/estructura.md): árbol detallado del proyecto.
- [Resultados de validación](Docs/validacion-inicial.md): comprobaciones registradas durante la inicialización.

## Pendientes

Las siguientes tareas corresponden a etapas posteriores del trabajo integrador:

- Informar los integrantes y el número de grupo. El enunciado exige un grupo de 4 o 5 personas.
- Registrar las intervenciones manuales reales cuando se realicen; los DNI preparados están en Docs/datos-modelo.md.
- Implementar RF1–RF10 y RN1–RN7.
- Validar la persistencia real del modelo y ejecutar esquema/seed en MySQL operativo.
- Implementar los endpoints de la API REST.
- Completar las pantallas de gestión y las funcionalidades de la aplicación para socios.
- Implementar y ejecutar pruebas funcionales y de interfaz.
- Completar la colección de Postman.
- Elaborar el informe PDF de 8–12 páginas.

### Alojamiento y despliegue

El despliegue todavía no se realizó.

Se encuentran documentadas tareas de preparación relacionadas con:

- Dockerfile y configuración de Render.
- Variables de entorno.
- Configuración CORS.
- Fallback SPA para Pages.
- Posible conexión TLS a Aiven.

Antes de desplegar será necesario validar Docker, la conexión TLS a Aiven y los dominios reales.

Las vistas Thymeleaf se alojarán junto con el backend, no como frontend estático.

## Estado de las pruebas

Los escenarios de prueba de sistema están documentados, pero no ejecutados. Los resultados deben registrarse únicamente después de realizar cada prueba y observar el comportamiento real del sistema.

No deben interpretarse las comprobaciones de compilación o inicialización como evidencia de que las reglas de negocio, las operaciones CRUD o las integraciones estén funcionando.
