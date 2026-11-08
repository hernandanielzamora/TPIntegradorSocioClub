# MySQL local en Windows

## Uso habitual con backend/.env — 2026-11-08

El usuario confirmó que la validación completa de Maven terminó con BUILD SUCCESS tras resolver autenticación y ajustar la aserción CHECK. Es un resultado comunicado por el usuario; Codex no repitió esa ejecución autenticada. Las secciones históricas siguientes conservan los diagnósticos anteriores.

Desde la raíz del repositorio, verificar las exclusiones antes de guardar claves:

~~~powershell
git check-ignore -v backend/.env
git check-ignore backend/.env.example
~~~

El primer comando debe identificar la regla .env; el segundo no debe mostrar salida (código 1 significa que la plantilla no está ignorada). El archivo local ya fue creado con marcadores sin secretos. En una copia nueva, crearlo solo si todavía no existe:

~~~powershell
if (-not (Test-Path backend/.env)) {
    Copy-Item backend/.env.example backend/.env
}
~~~

Editar backend/.env desde VS Code y reemplazar COMPLETAR_CLAVE_DESARROLLO y COMPLETAR_CLAVE_PRUEBAS por las contraseñas locales correspondientes. No enviar ese archivo al chat ni incluirlo con git add -f. Se guarda en texto plano local, no cifrado. Mantener socioclub/socioclub_app para desarrollo y socioclub_test/socioclub_tests para pruebas.

El formato es NOMBRE=valor, una variable por línea. Se divide por el primer signo =; los restantes = y los caracteres # son parte literal del valor. Solo las líneas cuyo primer carácter no blanco es # son comentarios. No agregar comentarios al final de un valor. Se admiten comillas externas simples o dobles, que se retiran sin interpretar escapes, expresiones, variables ni comandos. Para conservar espacios al inicio/final de una clave, usar comillas externas; no escapar signos $ ni barras con sintaxis PowerShell. No se admite contenido multilínea, export ni claves duplicadas o desconocidas.

En la terminal PowerShell integrada de VS Code, desde la raíz:

~~~powershell
.\backend\scripts\load-env.ps1
Set-Location backend
.\mvnw.cmd -B verify
.\mvnw.cmd spring-boot:run
~~~

Ejecutar Spring Boot después de que verify termine correctamente. Desde backend, el cargador se invoca como .\scripts\load-env.ps1. Desde otra ubicación, usar la ruta absoluta al script: este siempre busca el .env del backend junto a su carpeta scripts, independientemente del directorio actual. Ejecutarlo directamente en la terminal actual; no lanzarlo con powershell -File en otro proceso, porque ese proceso no puede configurar el entorno de la terminal padre.

Si Windows bloquea scripts locales por política de ejecución y la política de la organización lo permite, habilitarlo solo para esa terminal y repetir la carga:

~~~powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy RemoteSigned
~~~

El cargador valida primero todas las variables y la separación de cuentas/bases. No imprime valores, no evalúa código y solo configura el entorno del proceso actual y de sus futuros procesos hijos. No modifica variables de usuario/sistema ni application.properties. No usa dependencias externas. Es necesario cargarlo una vez por terminal nueva; Spring y Maven no leen .env por sí solos. PORT y CORS_ALLOWED_ORIGINS son opcionales y conservan sus valores predeterminados de Spring si no están definidos en el entorno.

MYSQL_TEST_DB_URL activa los siete tests incluso si MYSQL_TEST_ENABLED=false; para verify con esta plantilla se mantienen explícitamente activados. PersistenciaMySqlTests conserva credenciales exclusivas de ensayo, validación del catálogo socioclub_test, SELECT DATABASE(), validate, inicialización SQL desactivada y rollback. El cargador no prepara ni modifica bases.

Para migrar a otra computadora: clonar el repositorio, instalar los requisitos de la guía instalacion-desde-cero.md, preparar las dos bases/cuentas siguiendo esa guía y copiar .env.example a .env en el equipo nuevo. Completar allí sus claves locales y cargar el script. No depender de que Git transfiera el .env ni sobrescribir bases existentes. Cerrar la terminal elimina estas variables de sesión; el archivo local permanece para la próxima carga.

Verificación automática del cargador: Windows PowerShell, datos ficticios en .cache, carga desde otra ruta, preservación de #/= y caracteres PowerShell, comillas externas, rechazo de variables faltantes y de root, sin carga parcial ante configuración inválida. No se leyó el .env local para estas pruebas ni se conectó a MySQL.

## Estado al cierre del 2026-11-08

El usuario confirmó MySQL 8.4 funcionando, las bases socioclub/socioclub_test y cuentas locales separadas. También confirmó creación de tablas mediante schema.sql y preparación manual de datos/pruebas. Es una intervención humana comunicada; Codex todavía no verificó tablas ni conteos mediante una conexión autenticada.

En el proceso de cierre no están disponibles DB_USERNAME/DB_PASSWORD ni la configuración de ensayo, tampoco como variables del usuario Windows. No se solicitaron contraseñas. Maven compiló y ejecutó integridad/MVC; las siete pruebas MySQL se omitieron expresamente. Persistencia real, consultas JPA, Hibernate validate contra MySQL y arranque completo siguen pendientes.

Para una computadora nueva, seguir [instalación desde cero](instalacion-desde-cero.md). Allí están instalación, clonación, Workbench, credenciales locales y arranque de las tres aplicaciones.

## Servicio y diagnóstico inicial

El servicio del equipo original se llama MySQL84, tiene inicio Manual y usa NT AUTHORITY\NetworkService. El ejecutable es `C:\Program Files\MySQL\MySQL Server 8.4\bin\mysqld.exe`, con configuración `C:\ProgramData\MySQL\MySQL Server 8.4\my.ini`. El cliente mysql.exe está en ese mismo bin y reportó 8.4.13, aunque no figura en PATH.

my.ini configura puerto 3306 y datadir `C:/ProgramData/MySQL/MySQL Server 8.4/Data`; su log es DESKTOP-5Q7UPAT.err. Inicialmente el servicio estaba detenido, TCP no respondía y el proceso de Codex no estaba elevado. Start-Service fue rechazado antes de iniciar mysqld. sc.exe query informó 1077, que net helpmsg tradujo como ausencia de intentos de inicio desde el último arranque: no demostraba corrupción ni error de contraseña.

La lectura de Data fue denegada; no se cambiaron ACL ni cuenta del servicio. Más tarde se observó Running, códigos 0 y TCP accesible. El usuario confirmó posteriormente la configuración manual mediante Workbench.

Si en otra computadora el servicio está detenido, ejecutar desde PowerShell administrador, usando el nombre real instalado:

~~~powershell
Start-Service MySQL84 -ErrorAction Stop
Get-Service MySQL84
Test-NetConnection localhost -Port 3306
~~~

Si falla, revisar el log configurado con permisos administrativos. No usar --initialize ni reinstalar sobre el directorio de datos para ocultar el error. La [documentación oficial de instalación](https://dev.mysql.com/doc/refman/8.4/en/windows-installation.html) explica MSI y MySQL Configurator.

## Esquema y cuentas

Preparar con una cuenta administradora únicamente socioclub y socioclub_test. Crear tablas con database/schema.sql en cada una. La cuenta habitual de Spring será socioclub_app@localhost, con SELECT/INSERT/UPDATE/DELETE sobre socioclub.*. La cuenta socioclub_tests@localhost tiene esos mismos permisos únicamente sobre socioclub_test.*.

La suite usa spring.sql.init.mode=never y ddl-auto=validate. No ejecuta DDL ni requiere CREATE/REFERENCES si el administrador preparó las tablas. No conceder acceso a desarrollo a la cuenta de ensayo, ni usar root para arrancar Spring Boot.

Antes de ejecutar scripts, seleccionar la base correcta y comprobar SELECT DATABASE(). En Workbench puede agregarse temporalmente USE socioclub; o USE socioclub_test; en la pestaña del editor. No guardar esa selección en los scripts compartidos: el test reutiliza seed.sql sobre ensayo.

En desarrollo ejecutar schema.sql y luego seed.sql. En ensayo ejecutar solo schema.sql y mantener sus tablas sin registros antes de la suite; el propio test carga/revierte el seed. Si hay datos inesperados, detenerse sin borrarlos.

Los scripts no tienen DROP, TRUNCATE, DELETE ni actualizaciones de registros existentes. CREATE TABLE IF NOT EXISTS no migra tablas antiguas. El seed conserva registros y bajas; no sirve para restaurar automáticamente escenarios. Ante un error durante carga manual, ejecutar ROLLBACK y revisar antes de continuar.

La selección por LEFT JOIN evita subconsultas sobre la tabla destino del INSERT; ver [condiciones oficiales de INSERT SELECT](https://dev.mysql.com/doc/refman/8.4/en/insert-select.html). Ejecutar la carga manual sin escritores concurrentes.

## Variables y conexión

En la terminal desde la que se arrancará el backend:

~~~powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/socioclub'
$env:DB_USERNAME = 'socioclub_app'
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contraseña de desarrollo' -AsSecureString)).Password
~~~

En una terminal separada para pruebas:

~~~powershell
$env:MYSQL_TEST_DB_URL = 'jdbc:mysql://localhost:3306/socioclub_test'
$env:MYSQL_TEST_DB_USERNAME = 'socioclub_tests'
$env:MYSQL_TEST_DB_PASSWORD = [System.Net.NetworkCredential]::new('', (Read-Host 'Contraseña de ensayo' -AsSecureString)).Password
$env:MYSQL_TEST_ENABLED = 'true'
Set-Location backend
.\mvnw.cmd -B verify
~~~

Ingresar contraseñas solo en el prompt oculto. No activar transcripción, imprimir variables completas ni incluir contraseñas en argumentos, archivos versionados o logs. Los valores viven en esa terminal y sus procesos hijos: una terminal distinta o un proceso ya abierto no los hereda. .env.example documenta nombres pero Spring no carga .env automáticamente.

Las credenciales MYSQL_TEST_DB_USERNAME/PASSWORD sustituyen las de desarrollo para la suite. Ambas variables de ensayo son obligatorias al activar la suite; si faltan, se rechaza la configuración sin heredar DB_USERNAME/PASSWORD. La URL debe señalar exactamente socioclub_test y antes de cada test se verifica SELECT DATABASE().

Una URL de ensayo activa los tests aunque falte MYSQL_TEST_ENABLED. El flag true también activa y exige URL; false no fuerza omisión si hay URL. Un valor de flag inválido falla. Con configuración presente, una conexión fallida produce errores, nunca una omisión silenciosa. Sin configuración, las siete pruebas se omiten con motivo expreso.

Cada método revierte datos con rollback; AUTO_INCREMENT puede avanzar. La prueba de seed quita únicamente START TRANSACTION/COMMIT para que su carga forme parte del rollback y rechaza instrucciones USE antes de ejecutar. Los controles negativos anteriores demostraron rechazo de base de desarrollo y conexión inaccesible; no acreditan persistencia aprobada.

Al terminar, retirar credenciales de las terminales:

~~~powershell
Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
Remove-Item Env:MYSQL_TEST_DB_URL,Env:MYSQL_TEST_DB_USERNAME,Env:MYSQL_TEST_DB_PASSWORD,Env:MYSQL_TEST_ENABLED -ErrorAction SilentlyContinue
~~~

## Qué falta comprobar realmente

- Tres tablas, PK/FK, DNI único, estados, fechas y precisión monetaria.
- Ocho actividades exactas, 21 socios, dos MOROSOS, 19 inscripciones y Yoga con 14 ACTIVAS/15 plazas.
- Siete pruebas MySQL aprobadas sin omisiones.
- Arranque completo con Hibernate validate y respuesta MVC mediante HTTP.

La preparación manual fue confirmada por el usuario; estos resultados de aplicación permanecen sin acreditar. Ver [validación](validacion-modelo-datos.md) y [guía de defensa](guia-defensa-tecnica.md).

## Autenticación y aislamiento — actualización 2026-11-08

La ejecución manual de verify falló por MySQL 1045 / SQLState 28000 para socioclub_tests@localhost, antes del error secundario de dialecto. Ver evidencia y procedimiento en validacion-modelo-datos.md, sección Diagnóstico de autenticación. La causa concreta del rechazo de credenciales sigue pendiente de comprobación local.

A partir de esta corrección, MYSQL_TEST_DB_USERNAME y MYSQL_TEST_DB_PASSWORD son obligatorias cuando se activa la suite: se eliminó el fallback a las credenciales de desarrollo descrito anteriormente. No se modificaron application.properties, scripts ni tablas. Mantener claves en variables temporales e ingresarlas con Read-Host -AsSecureString; no imprimirlas. Los procesos de Codex no heredan las variables de la terminal del usuario.

## Cierre de conexión y errores 1045 — 2026-11-08

El usuario confirmó la corrección de las contraseñas locales de ambas cuentas. La ejecución final de Maven verify terminó con BUILD SUCCESS; los XML Surefire inspeccionados por Codex muestran 13 aprobadas (5 integridad, 7 MySQL, 1 MVC), 0 fallos, 0 errores y 0 omitidas. También confirmó arranque completo conectado a jdbc:mysql://localhost:3306/socioclub, MySQL 8.4.13, EntityManagerFactory inicializado y Tomcat 8080 con Started SocioClubApplication. No hay aún confirmación HTTP GET /; el arranque no sustituye esa consulta.

Los dos incidentes 1045/SQLSTATE 28000 correspondieron a cuentas diferentes: socioclub_tests durante JPA y socioclub_app durante el arranque normal. Codex identificó el rechazo en los logs y propuso contrastar las cuentas con el cliente MySQL mediante prompt oculto, sin cambiar el esquema ni agregar dialecto. No consta que el usuario ejecutara ese comando concreto; sí confirmó la corrección manual de contraseñas y la validación final. Los tests exitosos de ensayo no verifican automáticamente las credenciales de desarrollo.

La incorporación de .env fue propuesta por el usuario para reutilizar el entorno entre terminales y computadoras. Codex escribió la plantilla y el cargador; el usuario ingresó y corrigió las claves locales. La configuración de prueba continúa separada y obligatoria. La plantilla se versiona; .env no se distribuye por Git y se completa en cada equipo. Cargar el script de nuevo tras editar el archivo y en cada terminal nueva. No se leyó el archivo local en la auditoría. Se confirmó que está ignorado, fuera del índice y sin commits en el historial accesible; Git no registra adiciones al índice luego retiradas sin commit.

Para completar la comprobación MVC real, con Spring ejecutándose y desde otra terminal:

~~~powershell
(Invoke-WebRequest -Uri 'http://localhost:8080/' -UseBasicParsing).StatusCode
~~~

Registrar el resultado real antes de afirmarlo aprobado. El detalle de los seis incidentes y la clasificación de aportes humanos está en bitacora-ia.md. Este cierre actualiza los estados históricos anteriores.

## Confirmación HTTP y auditoría de plantilla — 2026-11-08

El usuario confirmó manualmente respuesta 200 de HTTP GET / con Spring Boot conectado a socioclub. Queda superado el pendiente HTTP de las entradas anteriores. Codex no hizo la petición autenticada ni abrió .env. La plantilla pública usa COMPLETAR_CLAVE_DESARROLLO y COMPLETAR_CLAVE_PRUEBAS, coherentes con el rechazo de marcadores del cargador; el usuario corrigió manualmente la discrepancia detectada en la auditoría. Recargar el entorno tras completar las claves locales. Publicación autorizada solo en la rama feature, con revisión del PR antes del merge.
