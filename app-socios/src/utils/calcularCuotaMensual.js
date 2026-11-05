// RN6 — Bonificación por cantidad de actividades.
// La cuota mensual estimada es la suma de las cuotas de las actividades activas:
//   - 1 actividad: sin descuento
//   - 2 actividades: 10 % de descuento
//   - 3 actividades: 15 % de descuento
// Es una función pura: con los mismos datos siempre devuelve el mismo resultado
// y no modifica nada fuera de ella. Por eso se puede probar fácilmente con Jest.

// Devuelve el porcentaje de descuento que corresponde a una cantidad de actividades.
export function obtenerPorcentajeDescuento(cantidadActividades) {
  if (cantidadActividades >= 3) {
    return 15;
  }
  if (cantidadActividades === 2) {
    return 10;
  }
  return 0;
}

// Recibe un arreglo de actividades (cada una con su cuotaMensual)
// y devuelve el total a pagar con la bonificación aplicada.
export function calcularCuotaMensual(actividades) {
  if (!Array.isArray(actividades) || actividades.length === 0) {
    return 0;
  }

  const subtotal = actividades.reduce(
    (suma, actividad) => suma + Number(actividad.cuotaMensual || 0),
    0
  );
  const descuento = obtenerPorcentajeDescuento(actividades.length);

  // Se multiplica por (100 - descuento) y se divide por 100 para evitar
  // errores de redondeo de los decimales (por ejemplo 0.85 en JavaScript).
  return Math.round((subtotal * (100 - descuento)) / 100);
}

export default calcularCuotaMensual;
