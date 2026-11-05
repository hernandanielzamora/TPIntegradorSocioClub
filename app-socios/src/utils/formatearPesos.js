// Convierte un número en pesos argentinos: 18000 → "$ 18.000".
// Intl.NumberFormat es un objeto propio de JavaScript para dar formato a números.
const formato = new Intl.NumberFormat('es-AR', {
  style: 'currency',
  currency: 'ARS',
  maximumFractionDigits: 0,
});

export function formatearPesos(valor) {
  return formato.format(Number(valor) || 0);
}

export default formatearPesos;
