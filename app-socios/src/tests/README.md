# Pruebas de la app Mi Club

| Archivo | Qué prueba |
| --- | --- |
| `calcularCuotaMensual.test.js` | RN6 con 0, 1, 2 y 3 actividades (descuentos de 10 % y 15 %). |
| `esDniValido.test.js` | RN7: DNI válidos de 7 y 8 dígitos; rechazo con puntos, letras, espacios, 6 y 9 dígitos. |
| `TarjetaActividad.test.js` | React Testing Library (`render`, `screen`, `fireEvent`): muestra el nombre; con cupo 0 muestra «Sin cupo», el botón está deshabilitado y la tarjeta tiene la clase `sin-cupo`; con cupo el botón llama a `onInscribir`. |

Ejecutar desde `app-socios`:

```powershell
npm.cmd test -- --watchAll=false
```


Integración Axios: api.test.js valida contratos/errores con mocks de Axios e integracionApi.test.js prueba ingreso, catálogo, detalle, alta, baja confirmada/cancelada y refresco. fixturesApi.js contiene solo datos sintéticos de tests. RN6 también cubre centavos. No se consulta MySQL ni se usa passWithNoTests. Comando completo: npm.cmd test -- --watchAll=false --runInBand.
