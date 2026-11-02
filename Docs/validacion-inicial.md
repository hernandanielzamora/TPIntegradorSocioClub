# Validación de Prompt 01 — 2026-11-02

Resultados de la herramienta durante esta sesión. No constituyen revisión humana, pruebas de negocio ni certificación de un despliegue.

## Entorno observado

| Comprobación | Resultado |
| --- | --- |
| git --version | 2.47.1.windows.1 |
| node --version | v24.22.2 |
| npm --version | Bloqueado por política de ejecución de npm.ps1 |
| npm.cmd --version | 11.19.1 |
| java -version / javac -version | Oracle 25.0.1; javac 25.0.1 |
| JDK 21 | No encontrado en ubicaciones inspeccionadas |
| mvn / mysql en PATH | No encontrados |
| Servicio local con nombre MySQL | No encontrado en Get-Service |
| Maven Wrapper | Apache Maven 3.10.0; wrapper 3.3.4 |

Se utiliza npm.cmd sin cambiar ExecutionPolicy. No se modifica software global. Las descargas de Initializr y dependencias se realizaron con acceso de red autorizado tras el bloqueo de red inicial del entorno restringido.

## Backend

Initializr devolvió Spring Boot 4.1.2.RELEASE. El primer intento falló con `Non-resolvable parent POM`: esa coordenada no existía en Maven Central. Se consultó el metadata oficial y se corrigió a **4.1.2**, estable y publicada.

Comando ejecutado desde `backend/`, con caché de Maven configurada en el workspace:

```powershell
$env:MAVEN_USER_HOME = [System.IO.Path]::GetFullPath((Join-Path (Get-Location).Path '../.cache/maven'))
.\mvnw.cmd -B '-Dmaven.repo.local=../.cache/m2' verify
```

Resultado: **BUILD SUCCESS**, jar ejecutable `target/socioclub-0.0.1-SNAPSHOT.jar`. Surefire: **1 prueba, 0 fallos, 0 errores, 0 omitidas**. La prueba usa `@WebMvcTest` para verificar respuesta HTTP, vista y contenido inicial de secretaría; no levanta JPA ni se conecta a MySQL.

El primer comando de validación tuvo además un error al calcular la ruta de caché con `(Get-Location).Parent`; se corrigió usando `.Path` y `GetFullPath`. No se atribuye este ajuste al grupo.

`dependency:tree` también finalizó con BUILD SUCCESS. Versiones efectivas: starters Boot 4.1.2, MySQL Connector/J 9.7.0 y JUnit Jupiter 6.0.3.

**No ejecutado:** arranque completo con persistencia. No se proporcionaron credenciales ni se confirmó una instancia MySQL operativa. No se simula éxito con H2 ni se desactiva JPA para presentar la aplicación como conectada.

## React

Proyecto generado mediante `npx.cmd --yes create-react-app@5.1.0 app-socios --use-npm`. CRA generó un repositorio temporal y pretendía crear un commit; se bloqueó ese commit mediante un hook temporal. CRA informó `Git commit not created` y retiró su `.git` temporal. El repositorio raíz permanece sin commits.

Se agregaron React Router DOM 6.30.6, Axios 1.21.0 y PropTypes 15.8.1; se fijaron React y React DOM 18.3.1 para preservar las comprobaciones PropTypes que React 19 omite. Se retiraron reportWebVitals y los recursos de ejemplo sin uso. Los cambios son locales al proyecto.

Comandos ejecutados: `npm.cmd ci`, `npm.cmd run build` con `CI=true`, y `npm.cmd test -- --watchAll=false --runInBand --passWithNoTests`.

Instalación reproducible y compilación con React 18.3.1: **exitosas**. Jest informó `No tests found, exiting with code 0`: **cero pruebas ejecutadas**, no una suite aprobada. Las pruebas RF/RN y de TarjetaActividad siguen pendientes.

Advertencias reales: CRA deprecado; dependencias transitivas deprecadas; advertencia Node `fs.F_OK`; npm notificó scripts postinstall de core-js/core-js-pure sin aprobación automática. No impidieron la compilación observada. Al cambiar React aparecieron advertencias temporales de peers sobre React 19; se comprueba el árbol final con `npm ls`.

npm reportó **75 vulnerabilidades: 11 bajas, 51 moderadas, 13 altas**. No se ejecutó audit fix ni audit fix --force. Su evaluación queda pendiente; no se certifica seguridad con una compilación.

## Límites y evidencias

Sin pruebas funcionales, Postman, escenarios de sistema ni conexión Aiven. Sin imagen Docker construida ni validación remota de Blueprint, Pages o despliegue. Los archivos de configuración no acreditan servicios funcionando.

Las salidas extensas se conservan localmente en `.cache/backend-verify.log`, `.cache/backend-dependencies.log`, `.cache/react-install.log`, `.cache/react-build.log` y `.cache/react-tests.log`; están ignoradas por Git. Surefire deja su informe en `backend/target/surefire-reports/`. El resumen versionable es este documento; al clonar deberán ejecutarse de nuevo las validaciones.

Las comprobaciones finales de estructura, enlaces y servicio estático se registran debajo solo después de ejecutarlas.

## Comprobaciones finales ejecutadas

- Matriz: 17 filas unicas RF/RN, todas Pendientes.
- Enlaces Markdown locales: destinos existentes.
- Archivos fuente: 65, sin archivos vacios y sin repositorio Git anidado en app-socios.
- Servicio HTTP estatico temporal: 12 respuestas 200 entre las tres paginas publicas, el build React y sus CSS/JS. Servidores detenidos al terminar. Esto no verifica ejecucion JavaScript en un navegador ni flujos funcionales.
- Puerto local 3306 no accesible durante la comprobacion; no se confirmo MySQL operativo.
- npm ls final: React y React DOM 18.3.1, sin dependencias invalidas reportadas. Jest 27.5.1.
