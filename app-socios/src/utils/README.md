# Funciones puras

| Archivo | Regla | Qué hace |
| --- | --- | --- |
| `calcularCuotaMensual.js` | RN6 | Suma las cuotas de las actividades activas y aplica 10 % de descuento con 2 actividades y 15 % con 3. También exporta `obtenerPorcentajeDescuento`. |
| `esDniValido.js` | RN7 | Devuelve `true` si el DNI tiene 7 u 8 dígitos numéricos, sin puntos ni espacios. |

Son funciones puras: no leen ni modifican nada fuera de sus parámetros. Sus pruebas Jest están en `src/tests/`.
