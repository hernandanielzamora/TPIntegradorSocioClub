# Inventario de archivos fuente

Generado desde el contenido real del workspace el 2026-11-02. Se omiten del arbol .git/, .cache/, node_modules/, target/ y build/, que contienen metadatos, cache o salidas generadas.

```text
.
├── .vscode/
│   └── extensions.json
├── app-socios/
│   ├── public/
│   │   ├── _redirects
│   │   ├── index.html
│   │   └── robots.txt
│   ├── src/
│   │   ├── componentes/
│   │   │   ├── Encabezado.js
│   │   │   └── Pie.js
│   │   ├── contexto/
│   │   │   └── README.md
│   │   ├── estilos/
│   │   │   └── estilos.css
│   │   ├── paginas/
│   │   │   └── Inicio.js
│   │   ├── servicios/
│   │   │   └── api.js
│   │   ├── tests/
│   │   │   └── README.md
│   │   ├── utils/
│   │   │   └── README.md
│   │   ├── App.js
│   │   ├── index.js
│   │   └── setupTests.js
│   ├── .env.example
│   ├── .gitignore
│   ├── package-lock.json
│   ├── package.json
│   └── README.md
├── backend/
│   ├── .mvn/
│   │   └── wrapper/
│   │       └── maven-wrapper.properties
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── ar/
│   │   │   │       └── edu/
│   │   │   │           └── siglo21/
│   │   │   │               └── socioclub/
│   │   │   │                   ├── config/
│   │   │   │                   │   └── WebConfig.java
│   │   │   │                   ├── controller/
│   │   │   │                   │   ├── api/
│   │   │   │                   │   │   └── package-info.java
│   │   │   │                   │   └── mvc/
│   │   │   │                   │       └── InicioController.java
│   │   │   │                   ├── dto/
│   │   │   │                   │   └── package-info.java
│   │   │   │                   ├── exception/
│   │   │   │                   │   └── package-info.java
│   │   │   │                   ├── model/
│   │   │   │                   │   └── package-info.java
│   │   │   │                   ├── repository/
│   │   │   │                   │   └── package-info.java
│   │   │   │                   ├── service/
│   │   │   │                   │   └── package-info.java
│   │   │   │                   └── SocioClubApplication.java
│   │   │   └── resources/
│   │   │       ├── static/
│   │   │       │   └── css/
│   │   │       │       └── estilos.css
│   │   │       ├── templates/
│   │   │       │   ├── fragments/
│   │   │       │   │   └── comunes.html
│   │   │       │   └── secretaria/
│   │   │       │       └── inicio.html
│   │   │       └── application.properties
│   │   └── test/
│   │       └── java/
│   │           └── ar/
│   │               └── edu/
│   │                   └── siglo21/
│   │                       └── socioclub/
│   │                           └── SocioClubApplicationTests.java
│   ├── .dockerignore
│   ├── .env.example
│   ├── .gitattributes
│   ├── .gitignore
│   ├── Dockerfile
│   ├── mvnw
│   ├── mvnw.cmd
│   └── pom.xml
├── database/
│   ├── schema.sql
│   └── seed.sql
├── Docs/
│   ├── Enunciado/
│   │   └── TPIntegradorSocioClubEnunciado.pdf
│   ├── alojamiento.md
│   ├── arquitectura.md
│   ├── bitacora-ia.md
│   ├── estructura.md
│   ├── pruebas-sistema.md
│   ├── registro-trabajo.md
│   ├── requerimientos.md
│   └── validacion-inicial.md
├── postman/
│   └── README.md
├── sitio-publico/
│   ├── assets/
│   │   └── README.md
│   ├── css/
│   │   └── estilos.css
│   ├── js/
│   │   └── script.js
│   ├── actividades.html
│   ├── index.html
│   └── preinscripcion.html
├── .editorconfig
├── .gitattributes
├── .gitignore
├── README.md
└── render.yaml
```

El PDF original se conserva. README y los tres documentos iniciales se actualizaron; los demás archivos fuente de aplicación fueron creados durante Prompt 01. Antes del primer commit se agregó .gitattributes y se ajustaron exclusiones y documentación a pedido del usuario. El resultado de commit y publicación se verifica en el historial de Git; no se efectuaron despliegues.

## Actualización del cierre

Además del árbol histórico anterior: sitio-publico/tests/script.test.cjs y assets/deporte.svg; database/propuestas (002/003 NO ejecutadas); Docs/mejoras-futuras.md, sitio-publico.md, cierre-desarrollo.md y preparacion-informe-defensa.md. Se conservan backend, app-socios y sitio-publico como las tres aplicaciones. target/build/node_modules/.cache se excluyen de Git. El estado de desarrollo vigente es cierre-desarrollo.md, no los pendientes del inventario inicial.
