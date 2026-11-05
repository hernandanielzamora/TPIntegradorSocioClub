# Pruebas de la app Mi Club

| Archivo | Qué prueba |
| --- | --- |
| `calcularCuotaMensual.test.js` | RN6 con 0, 1, 2 y 3 actividades (descuentos de 10 % y 15 %). |
| `esDniValido.test.js` | RN7: DNI válidos de 7 y 8 dígitos; rechazo con puntos, letras, espacios, 6 y 9 dígitos. |

Ejecutar desde `app-socios`:

```powershell
npm.cmd test -- --watchAll=false
```
