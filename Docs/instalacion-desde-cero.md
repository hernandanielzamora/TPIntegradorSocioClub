# Instalación desde cero — Windows

Guía para continuar SocioClub desde otra computadora. Estado al 2026-11-08: modelo JPA, repositorios, scripts y pruebas implementados; persistencia real y arranque completo pendientes de confirmación. React conserva datos locales. No hay servicios RN1–RN5 ni API REST.

## 1. Instalar herramientas

Usar instaladores oficiales para la arquitectura de Windows del equipo:

| Herramienta | Preparación |
| --- | --- |
| Git for Windows | [Instalador oficial](https://git-scm.com/install/windows). Habilitar uso desde terminal. |
| Visual Studio Code | [Instalación oficial para Windows](https://code.visualstudio.com/docs/setup/windows). Abrir luego la carpeta del repositorio. |
| JDK 25 | Instalar un JDK completo, por ejemplo [Temurin 25](https://adoptium.net/temurin/releases/?version=25&os=windows&arch=x64). Configurar JAVA_HOME al JDK y su bin en PATH. El proyecto requiere Java 25; no cambiar pom.xml para usar otro Java. |
| Node.js 24 | En [descargas oficiales](https://nodejs.org/en/download), seleccionar la rama 24 e instalar con npm. En el equipo original se usó 24.22.2. |
| MySQL Community Server 8.4 | Instalar el servidor gratuito y configurarlo con MySQL Configurator, siguiendo la [guía oficial Windows](https://dev.mysql.com/doc/refman/8.4/en/windows-installation.html). |
| MySQL Workbench | Interfaz de administración local, desde las [descargas oficiales](https://dev.mysql.com/downloads/workbench/). El servidor se instala y ejecuta por separado. |
| Python 3, opcional | Puede servir el sitio estático; como alternativa usar Live Server en VS Code. |

No hace falta Maven global: el repositorio incluye Maven Wrapper. No instalar otra versión de React ni regenerar la app con un generador. El lockfile y npm ci reproducen las dependencias.

Cerrar y volver a abrir PowerShell después de instalar herramientas o modificar PATH. Comprobar:

~~~powershell
git --version
java -version
javac -version
node --version
npm.cmd --version
~~~

Si JAVA_HOME apunta a otra versión, corregirlo antes de compilar. Instalar en VS Code las extensiones recomendadas por .vscode/extensions.json. No hace falta cambiar la política de ejecución de PowerShell: los comandos usan npm.cmd y mvnw.cmd.

## 2. Recuperar la rama

Desde la carpeta donde se quiera guardar el proyecto:

~~~powershell
git clone --branch feature/backend-modelo-datos https://github.com/hernandanielzamora/TPIntegradorSocioClub.git
Set-Location TPIntegradorSocioClub
git status --short --branch
code .
~~~

Si ya se había clonado y el árbol está limpio:

~~~powershell
git fetch origin
git switch feature/backend-modelo-datos
git pull --ff-only
~~~

Si la rama todavía no existe localmente, usar git switch --track origin/feature/backend-modelo-datos. No descartar cambios de la otra computadora para actualizar. main es producción; dev integra; los PR de esta rama apuntan a dev y no se fusionan automáticamente.

## 3. Preparar MySQL local

En MySQL Configurator, configurar el servidor local y puerto 3306. Guardar la contraseña administradora solo en el equipo. Desde PowerShell:

~~~powershell
Get-Service *MySQL*
Test-NetConnection localhost -Port 3306
~~~

Si el servicio instalado se llama MySQL84 y está detenido, abrir otra consola PowerShell como administrador y ejecutar Start-Service MySQL84. No cambiar permisos del servicio ni reinicializar datadir para resolver problemas.

En Workbench, abrir una conexión administradora local y crear únicamente las bases del proyecto:

~~~sql
CREATE DATABASE IF NOT EXISTS socioclub CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS socioclub_test CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
~~~

En Administration → Users and Privileges, preparar cuentas locales con contraseñas privadas introducidas en la interfaz:

- socioclub_app@localhost: SELECT, INSERT, UPDATE, DELETE sobre socioclub.*.
- socioclub_tests@localhost: SELECT, INSERT, UPDATE, DELETE sobre socioclub_test.*.

Crear tablas con la cuenta administradora antes de usar esas cuentas. Las cuentas de aplicación y ensayo no requieren CREATE, ALTER ni DROP. No conceder a la cuenta de pruebas acceso a desarrollo. No usar root en Spring Boot.

## 4. Ejecutar esquema y datos

Los archivos SQL no contienen USE ni nombres de base fijos. Esto permite elegir explícitamente la base sin desviar una carga de ensayo hacia desarrollo.

En la conexión administradora de Workbench:

1. Ejecutar USE socioclub; SELECT DATABASE(); SHOW TABLES;. Si hay datos o tablas previas inesperados, detenerse y revisar; no borrar.
2. Abrir database/schema.sql con File → Open SQL Script. Agregar temporalmente USE socioclub; al inicio del editor, seleccionar todo y ejecutar con el primer botón del rayo. No guardar ese USE en el archivo del repositorio.
3. Revisar Action Output. Después abrir database/seed.sql y ejecutarlo de la misma forma con USE socioclub; temporal.
4. Si falla una sentencia del seed, ejecutar ROLLBACK y detenerse. Revisar el error antes de repetir.
5. En otra pestaña, abrir schema.sql con USE socioclub_test; temporal al principio y ejecutar únicamente el esquema. No cargar manualmente seed.sql en ensayo.

El esquema crea tablas ausentes y no migra tablas antiguas. El seed agrega datos ausentes sin sobrescribir ni reactivar vínculos existentes. Ejecutarlo sin escrituras concurrentes. No hay DROP, TRUNCATE ni borrado de preparación.

En desarrollo vacío se esperan 8 actividades, 21 socios (2 MOROSOS), 19 inscripciones (18 ACTIVAS/1 BAJA) y Yoga con 14 ACTIVAS de 15 plazas. Verificar:

~~~sql
SELECT COUNT(*) FROM socioclub.actividad;
SELECT COUNT(*) FROM socioclub.socio;
SELECT estado_cuota, COUNT(*) FROM socioclub.socio GROUP BY estado_cuota;
SELECT estado, COUNT(*) FROM socioclub.inscripcion GROUP BY estado;
SELECT a.nombre, a.cupo_maximo, COUNT(i.id) AS activas
FROM socioclub.actividad a
LEFT JOIN socioclub.inscripcion i ON i.actividad_id = a.id AND i.estado = 'ACTIVA'
GROUP BY a.id, a.nombre, a.cupo_maximo;
~~~

En socioclub_test deben existir las tres tablas, sin registros antes de la suite. No borrar datos existentes para lograrlo: revisar la preparación y reservar esa base para pruebas.

Las edades del seed tienen como referencia 2026-11-07; verificar edades y precondiciones de los escenarios antes de una exposición futura. Git transporta scripts, no el servicio MySQL, cuentas, contraseñas ni datos locales de Workbench.

## 5. Variables y backend

En una terminal normal desde la raíz del repositorio, sin transcripción de consola activada:

~~~powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/socioclub'
$env:DB_USERNAME = 'socioclub_app'
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contraseña de socioclub_app' -AsSecureString)).Password
$env:PORT = '8080'
$env:CORS_ALLOWED_ORIGINS = 'http://localhost:3000,http://localhost:5500'
Set-Location backend
.\mvnw.cmd -B verify
.\mvnw.cmd spring-boot:run
~~~

La contraseña se ingresa oculta y queda solo en el entorno de esa terminal y sus procesos hijos. No escribirla en el comando, chat, archivo versionado o log. Las variables de otra terminal no se transmiten a un proceso ya abierto. No imprimir ni listar todas las variables. backend/.env.example es una referencia; Spring no carga archivos .env automáticamente.

Abrir http://localhost:8080/. Esa pantalla requiere conexión real para el arranque completo. Hibernate usa ddl-auto=validate: valida el esquema precreado; no crea ni reconstruye tablas. Las variables PORT y CORS tienen valores por defecto; las credenciales deben proporcionarse localmente.

El verify sin configuración de ensayo aprueba las pruebas de integridad y MVC y omite siete tests MySQL: no acredita persistencia.

## 6. Ejecutar las pruebas JPA reales

En una terminal independiente, con las tres tablas de ensayo preparadas y vacías, desde backend:

~~~powershell
$env:MYSQL_TEST_DB_URL = 'jdbc:mysql://localhost:3306/socioclub_test'
$env:MYSQL_TEST_DB_USERNAME = 'socioclub_tests'
$env:MYSQL_TEST_DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contraseña de socioclub_tests' -AsSecureString)).Password
$env:MYSQL_TEST_ENABLED = 'true'
.\mvnw.cmd -B verify
~~~

Las credenciales específicas de ensayo sustituyen las propiedades de datasource antes de conectarse. La URL debe señalar exactamente socioclub_test; antes de cada test se verifica SELECT DATABASE(). El script compartido no puede contener USE. No hay inicialización SQL automática: si faltan tablas, Hibernate debe fallar y hay que prepararlas administrativamente.

Cada test revierte sus datos con rollback; el avance de AUTO_INCREMENT puede permanecer. El test de seed administra la transacción sin sus sentencias START TRANSACTION/COMMIT para poder revertir la carga. Una URL configurada activa las pruebas aunque falte el flag; fallos de autenticación/conexión/esquema producen error y no omisión.

Revisar backend/target/surefire-reports. Objetivo pendiente: 13 pruebas aprobadas, 0 fallos y 0 omitidas; solo registrar ese resultado después de observarlo realmente. Para retirar credenciales al terminar:

~~~powershell
Remove-Item Env:MYSQL_TEST_ENABLED,Env:MYSQL_TEST_DB_URL,Env:MYSQL_TEST_DB_USERNAME,Env:MYSQL_TEST_DB_PASSWORD -ErrorAction SilentlyContinue
~~~

## 7. Aplicación React

En otra terminal desde la raíz:

~~~powershell
Set-Location app-socios
npm.cmd ci
$env:REACT_APP_API_URL = 'http://localhost:8080/api'
npm.cmd start
~~~

Abrir http://localhost:3000/. La app conserva datos locales del PR #2; la variable prepara la conexión futura, no habilita una API inexistente. Para verificar:

~~~powershell
npm.cmd run build
npm.cmd test -- --watchAll=false --runInBand
~~~

La variable REACT_APP_API_URL es pública y se incorpora al build; nunca debe contener secretos.

## 8. Sitio público

Con Python disponible, desde otra terminal en la raíz:

~~~powershell
python -m http.server 5500 --bind 127.0.0.1 --directory .\sitio-publico
~~~

Abrir http://localhost:5500/index.html. Alternativa: abrir index.html con Live Server en VS Code. El sitio conserva los avances del PR #1; JavaScript de filtros/validaciones sigue pendiente.

Para detener servidores usar Ctrl+C. En la terminal del backend, retirar la contraseña al finalizar con Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue.

## 9. Continuar el desarrollo

Leer [validación del modelo](validacion-modelo-datos.md), [datos de prueba](datos-modelo.md), [guía de defensa](guia-defensa-tecnica.md) y [MySQL local](mysql-local.md). La confirmación de preparación manual no sustituye ejecutar la suite ni arrancar Spring Boot.

No copiar node_modules, target, build, .cache, contraseñas ni volcados privados de la otra computadora. npm ci y Maven regeneran dependencias/artefactos. Ejecutar git status antes de trabajar y antes de publicar. La siguiente tarea es validar JPA, no implementar RN1–RN5 durante este cierre.
