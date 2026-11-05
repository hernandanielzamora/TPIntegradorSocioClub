# Contexto del socio

`SocioContext.js` crea el contexto con `createContext` y exporta `SocioProvider`, que envuelve toda la app en `App.js`.

El valor del contexto es `{ socio, ingresar, salir }`:

- `socio`: el socio que ingresó con su DNI, o `null`.
- `ingresar(socio)`: lo guarda después de buscarlo por DNI.
- `salir()`: lo borra y vuelve a la pantalla de ingreso.

Se lee con `useContext(SocioContext)` en `Encabezado`, `RutaProtegida`, `Ingreso`, `Catalogo`, `Detalle` y `MisActividades`. Se guarda en `sessionStorage` para no perder el ingreso al recargar la pestaña.
