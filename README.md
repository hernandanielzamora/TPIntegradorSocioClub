# SocioClub

Trabajo Práctico Integrador de **Taller de Construcción de Software**, Universidad Siglo 21.

Sistema destinado al Club Social y Deportivo Los Algarrobos, Córdoba.

## Estado actual del proyecto

**LISTO PARA ENTREGAR en lo técnico, con salvedad académica de participación.** RF1–RF10 y RN1–RN7 implementados y verificados. RF3 guarda en MySQL y muestra solicitudes paginadas en **`/secretaria/preinscripciones`**, accesible desde el panel y el menú. [Contrato y migración RF3](Docs/preinscripciones.md).

**Grupo 3:** Hernán Zamora, Matias Jure, Mateo Hernandez, Fernando Oliva y Joaquín Mazza. Sin fecha de entrega por indicación del usuario. El usuario confirmó que no hubo aportes de Fernando y Joaquín y pidió continuar; el historial solo acredita a los otros tres. No se crean commits ni autorías ficticias. Es una salvedad frente al requisito de participación de todos.

Pruebas nuevas: **136 backend (18 MySQL), 58 React y 29 sitio**, todas aprobadas; JAR backend y build React correctos. **Seis casos de sistema aprobados** con Playwright/Chrome y API/MySQL reales. **Newman ejecutó las 25 peticiones Postman: 34 aserciones aprobadas, cero fallos.** Hay 33 capturas reales de las tres interfaces, incluida cuota, bajas y solicitudes recibidas. [Resultados](Docs/entrega/verificaciones.json).

El usuario eligió una instancia MySQL nueva aislada en **127.0.0.1:3308**. Solo allí se ejecutaron esquema, seed, migración 004 y fixtures. Las bases originales y MySQL84 no se modificaron. Para usar el nuevo backend en una base anterior, respaldar y aplicar **004_preinscripcion.sql**; Hibernate solo valida y no crea la tabla automáticamente. En una base nueva basta schema.sql.

Entregables: [PDF final de 12 páginas](Docs/entrega/TPI_SocioClub_Grupo3.pdf), [DOCX editable](Docs/entrega/informe-tecnico-final.docx), [checklist](Docs/entrega/checklist-final.md), [matriz](Docs/entrega/matriz-auditoria.md) y [guía del paquete](Docs/entrega/README.md). ZIP y manifest se generan en entrega-final/, excluida de Git. El repositorio es público y se comprobó acceso anónimo. **Entregar la rama dev**; main no se actualizó y requiere autorización expresa.

Uso académico LOCAL: identificación por DNI y Secretaría sin autenticación robusta; no se desplegó el sistema ni se implementaron RN8 o panel administrativo React. Los documentos anteriores conservan antecedentes; la auditoría actual está en Docs/entrega/.

## Propósito

SocioClub es un proyecto académico que busca desarrollar un sistema para gestionar las actividades y los socios de un club deportivo.

El trabajo integrador contempla un sitio público, un backend con Spring Boot, una aplicación para socios con React, una API REST, persistencia MySQL y documentación técnica.

La implementación integra las etapas de la materia; el cierre vigente distingue funcionalidad implementada y evidencia pendiente.

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

Catálogo inicial y filtros disciplina/edad completos. El formulario carga IDs reales de la API y guarda las solicitudes en MySQL; Secretaría las muestra en /secretaria/preinscripciones. Para tests del sitio, después de npm.cmd ci en app-socios: node --test sitio-publico/tests/script.test.cjs.

Alternativa sin servidor:

```powershell
Start-Process .\sitio-publico\index.html
```

### 2. Base MySQL y backend

Bases existentes: respaldar y aplicar 004_preinscripcion.sql antes del nuevo backend; no repetir schema/seed ni migraciones ya aplicadas. Instalaciones nuevas: seguir [mysql-local.md](Docs/mysql-local.md) y [plan de migración](Docs/plan-migracion-estado-socio.md). Desarrollo usa socioclub/socioclub_app y testing socioclub_test/socioclub_tests; nunca root desde Spring. .env se completa localmente desde la plantilla, permanece ignorado y no se carga solo.

Terminal independiente, desde la raíz:

```powershell
Get-Service -Name MySQL84
Set-Location .\backend
.\scripts\load-env.ps1
if (-not $?) { throw 'No se cargó el entorno.' }
$env:PORT = '8080'
.\mvnw.cmd '-Dspring-boot.run.arguments=--server.port=8080' spring-boot:run
```

Si el servicio MySQL está detenido, iniciarlo desde Servicios de Windows o Start-Service -Name MySQL84 en una terminal administradora. No modificar credenciales para ocultar un fallo de conexión. Esperar Started SocioClubApplication. Abrir http://localhost:8080/secretaria (panel académico sin login) y http://localhost:8080/api/actividades. Hibernate solo valida: ddl-auto=validate, init SQL desactivado. GET de Secretaría y API verificaron 200 durante este cierre.

La base de testing contiene el fixture manual: NO ejecutar verify/MySQL tests que exigen vacío ni limpiarla para que pase. Para regresión segura, desde backend y sin cargar .env:

```powershell
.\mvnw.cmd -B '-Dtest=PreinscripcionControllerTests,ApiControllerTests,ApiConsultaServiceTests,SecretariaControllerTests,SecretariaServiceTests,SocioBajaServiceTests,InscripcionServiceTests,IntegridadModeloTests,SocioClubApplicationTests' test
```

Son 118 casos sin MySQL. El cierre completo ejecutó 136 con MySQL aislado, incluyendo 18 de persistencia/concurrencia. Surefire carga Mockito como agente explícito en Java 25; no depende del autoacoplamiento al proceso Java. Los 128/0/0/0 históricos corresponden a la ejecución autenticada del usuario antes del fixture; no se reemplazan por esta selección.

### 3. Aplicación React

Otra terminal desde la raíz; fijar PORT porque una terminal que herede PORT=8080 también cambia el puerto de CRA:

```powershell
Set-Location .\app-socios
npm.cmd ci
$env:PORT = '3000'
$env:REACT_APP_API_URL = 'http://localhost:8080/api'
npm.cmd start
```

Abrir http://localhost:3000. Ingreso por DNI de 7/8 dígitos, catálogo/detalle, ACTIVAS, alta/baja confirmada y cuota RN6 usan Axios real. Solo el DNI se guarda en sessionStorage; no es autenticación segura. CORS autoriza localhost:3000, no usar '*' para corregir un origen distinto. Ver [contratos e integración](Docs/react-integracion-api.md). POST alta crea una fila ACTIVA/ocupa plaza y PUT baja conserva historial/libera plaza: revisar datos antes de probar escrituras manuales; Codex solo hizo consultas HTTP de lectura.

Pruebas y build, desde app-socios:

```powershell
npm.cmd test -- --watchAll=false --runInBand
npm.cmd run build
```

58 pruebas con Axios simulado solo en tests. Además se ejecutaron seis casos de sistema con Playwright sobre Axios/API/MySQL reales; ver los resultados de cierre. build y node_modules están ignorados. No hay despliegue. Detener cada servidor con Ctrl+C.
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

## Organización de ramas y flujo histórico de trabajo

El repositorio utiliza tres tipos de ramas con responsabilidades diferentes:

| Rama | Propósito |
| --- | --- |
| `main` | Rama principal y estable del proyecto. |
| `dev` | Rama de integración del trabajo en desarrollo. |
| `feature/*` | Ramas de trabajo para implementar tareas o funcionalidades específicas. |

### Flujo histórico; excepción autorizada para el cierre

1. Partir de la rama `dev` actualizada para iniciar una tarea.
2. Crear una rama `feature/*` para trabajar de forma aislada.
3. Realizar los cambios correspondientes a esa tarea.
4. Revisar los archivos modificados y ejecutar las comprobaciones que correspondan.
5. Publicar la rama de trabajo en el repositorio remoto.
6. Abrir un Pull Request desde `feature/*` hacia `dev`.
7. Revisar los cambios y resolver las observaciones antes de integrar.
8. Mantener `main` como rama principal estable; no utilizarla como rama de trabajo cotidiana.

Los cambios de cada tarea deben limitarse a los archivos necesarios. En este cierre el usuario indicó no abrir PR e integrar directamente los cambios seguros a dev tras las pruebas. main requiere autorización expresa.

La existencia y el estado efectivo de cada rama o Pull Request deben comprobarse en GitHub. Esta sección documenta el flujo de trabajo previsto; no afirma que todas las tareas o integraciones ya se hayan realizado.

## Documentación y trazabilidad

La carpeta `Docs/` reúne el enunciado, las decisiones técnicas, los registros de trabajo y la documentación de las etapas del proyecto.

- [Enunciado principal](Docs/Enunciado/TPIntegradorSocioClubEnunciado.pdf): consigna del trabajo integrador.
- [Arquitectura y decisiones](Docs/arquitectura.md): organización y decisiones técnicas.
- [Matriz RF1–RF10 y RN1–RN7](Docs/requerimientos.md): trazabilidad actual, evidencia y límites explícitos.
- [Bitácora de IA — Anexo C](Docs/bitacora-ia.md): prompts, resultados, revisiones y correcciones reales.
- [Registro de trabajo manual](Docs/registro-trabajo.md): intervenciones del grupo, decisiones, validaciones y commits asociados.
- [Pruebas de sistema](Docs/pruebas-sistema.md): seis escenarios ejecutados y aprobados con evidencia real.
- [Preparación de alojamiento gratuito](Docs/alojamiento.md): documentación sobre Cloudflare Pages, Render y Aiven; sin despliegue.
- [Inventario de archivos](Docs/estructura.md): árbol detallado del proyecto.
- [Resultados de validación](Docs/validacion-inicial.md): comprobaciones registradas durante la inicialización.

## Estado de cierre y salvedades

- **Código y validaciones:** RF3 persistido; seis casos E2E, Newman, automatizadas y capturas completos. Ver Docs/entrega/verificaciones.json.
- **Participación:** solo Hernán, Matias y Mateo tienen aportes; el usuario confirmó la ausencia de aportes de Fernando y Joaquín y pidió continuar. Se registra sin inventar autoría.
- **main:** sin actualizar por falta de autorización expresa; entregar dev.
- **Dependencias:** auditoría actual con 37 vulnerabilidades (16 bajas, 8 moderadas, 13 altas, ninguna crítica). Se conserva CRA por consigna; no se aplicó fix --force ni se presenta el demo local como despliegue seguro.
- **Entorno original:** intacto; para usar este backend allí corresponde respaldar y aplicar 004. El cierre usa MySQL nuevo 3308 elegido por el usuario.
- **Mejoras futuras:** FE-ADMIN-01/RN8, autenticación y despliegue postergados; SQL 002/003 no ejecutados.

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

136 backend (18 MySQL), 58 React y 29 sitio aprobados. Seis casos de sistema reales aprobados; Newman ejecutó 25 peticiones y 34 aserciones, sin fallos. Playwright produjo 33 capturas de las tres interfaces. No se mezclan estos resultados con los antecedentes de inicialización.

## Mejoras futuras postergadas

FE-ADMIN-01 y RN8 no están implementadas y no forman parte del cierre obligatorio. Ver [registro de mejoras](Docs/mejoras-futuras.md); sus SQL están en database/propuestas, no deben ejecutarse como migraciones.
