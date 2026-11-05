# Mi Club — app de socios (Parte D)

App React creada con Create React App. Ver el [README principal](../README.md) para la instalación general del monorepo.

Estado: rutas, componentes, estado, contexto y pruebas funcionando con **datos locales** (un arreglo en `src/datos/datosEjemplo.js`), como pide el hito de la semana 2. Falta conectar la API REST con Axios (tarea 6.5).

## Cómo levantarla

Desde la carpeta `app-socios`:

```powershell
npm.cmd ci
npm.cmd start
```

Se abre `http://localhost:3000/`. Si `npm ci` falla con «Missing: yaml… from lock file», la versión de npm es anterior a la 11: usar `npx.cmd npm@11 ci`.

Pruebas (Jest y React Testing Library):

```powershell
npm.cmd test -- --watchAll=false
```

## Rutas

| Ruta | Página | Qué hace |
| --- | --- | --- |
| `/` | `Ingreso` | El socio escribe su DNI (validado con `esDniValido`, RN7) y se guarda en el contexto. |
| `/actividades` | `Catalogo` | Tarjetas de las 8 actividades con filtro por disciplina, cupo disponible e «Inscribirme». |
| `/actividades/:id` | `Detalle` | Detalle de la actividad con el botón «Inscribirme». |
| `/mis-actividades` | `MisActividades` | Inscripciones activas, botón «Darme de baja» y `ResumenCuota` (RN6). |

Las tres últimas están envueltas en `RutaProtegida`: sin socio ingresado redirigen a `/`.

## DNI de prueba (datos locales)

Son los socios ficticios de `Docs/pruebas-sistema.md`:

| DNI | Socio | Para mostrar |
| --- | --- | --- |
| 30111222 | Juan Pérez | Inscripción exitosa; empieza sin actividades. |
| 30222333 | Lucas Gómez | RN1: Fútbol infantil sin cupo (botón deshabilitado). |
| 30333444 | Martín López | RN2: ya inscripto en Fútbol infantil. |
| 30444555 | Nicolás Fernández | RN3: 14 años; rechazo en Funcional adultos o Yoga. |
| 30555666 | Federico Ramírez | RN4: cuota MOROSO; rechazo al inscribirse. |
| 30666777 | Agustín Torres | RN5: ya tiene 3 actividades; muestra la bonificación del 15 %. |

Los datos locales vuelven al estado inicial al recargar la página.

## Estructura de `src`

```text
src/
├── App.js                 rutas y SocioProvider
├── componentes/           Encabezado, Pie, TarjetaActividad, ListaActividades,
│                          DetalleActividad, ResumenCuota, RutaProtegida, tiposActividad
├── contexto/              SocioContext (createContext / useContext)
├── datos/                 datosEjemplo.js (temporal)
├── estilos/               estilos.css
├── paginas/               Ingreso, Catalogo, Detalle, MisActividades, NoEncontrada
├── servicios/             api.js (Axios) y datosLocales.js (simulación temporal)
├── tests/                 pruebas Jest y React Testing Library
└── utils/                 calcularCuotaMensual, esDniValido, formatearPesos, mensajeDeError
```

## Para conectar la API (tarea 6.5)

`servicios/datosLocales.js` simula la API: cada función devuelve una Promesa y los errores tienen la forma de Axios (`error.response.data.error`). Para pasar al backend:

1. En `servicios/api.js`, crear funciones con los mismos nombres usando la instancia `api` de Axios:

   | Función | Endpoint del Anexo A |
   | --- | --- |
   | `obtenerActividades()` | `GET /api/actividades` |
   | `obtenerActividad(id)` | `GET /api/actividades/{id}` |
   | `buscarSocioPorDni(dni)` | `GET /api/socios/dni/{dni}` |
   | `obtenerInscripcionesDeSocio(socioId)` | `GET /api/socios/{id}/inscripciones` |
   | `inscribir(socioId, actividadId)` | `POST /api/inscripciones` con `{ socioId, actividadId }` |
   | `darDeBaja(inscripcionId)` | `PUT /api/inscripciones/{id}/baja` |

   Cada una devuelve `respuesta.data`. Las inscripciones se esperan con la forma `{ id, fechaInscripcion, estado, actividad: { … } }` y las actividades con `cupoDisponible`.
2. Cambiar en las páginas `from '../servicios/datosLocales'` por `from '../servicios/api'`.
3. Borrar `servicios/datosLocales.js` y `datos/datosEjemplo.js`, para que las reglas RN1 a RN5 queden solo en el servicio del backend.
