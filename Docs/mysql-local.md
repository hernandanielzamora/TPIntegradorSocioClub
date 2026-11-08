# MySQL local en Windows

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

Las credenciales MYSQL_TEST_DB_USERNAME/PASSWORD sustituyen las de desarrollo para la suite. Si no se proporcionan, el código conserva DB_USERNAME/PASSWORD; se recomienda siempre la cuenta de ensayo separada. La URL debe señalar exactamente socioclub_test y antes de cada test se verifica SELECT DATABASE().

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
