# Contexto del socio

`SocioContext.js` crea el contexto con `createContext` y exporta `SocioProvider`, que envuelve toda la app en `App.js`.

El valor del contexto es `{ socio, ingresar, salir, cargandoSesion, errorSesion }`:

- `socio`: el socio que ingresó con su DNI, o `null`.
- `ingresar(socio)`: lo guarda después de buscarlo por DNI.
- `salir()`: lo borra y vuelve a la pantalla de ingreso.

Se lee con `useContext(SocioContext)` en `Encabezado`, `RutaProtegida`, `Ingreso`, `Catalogo`, `Detalle` y `MisActividades`. Solo el DNI se guarda en `sessionStorage`; al recargar se consulta nuevamente el DTO en la API. No se persiste el objeto completo ni se autentica identidad. `cargandoSesion` bloquea la navegación durante esa consulta y `errorSesion` informa un fallo de recuperación.
