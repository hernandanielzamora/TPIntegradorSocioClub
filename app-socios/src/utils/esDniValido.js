// RN7 — DNI válido: 7 u 8 dígitos numéricos, sin puntos ni espacios.
// Expresión regular:
//   ^      empieza la cadena
//   \d     un dígito del 0 al 9
//   {7,8}  repetido 7 u 8 veces
//   $      termina la cadena
const PATRON_DNI = /^\d{7,8}$/;

export function esDniValido(dni) {
  if (dni === null || dni === undefined) {
    return false;
  }
  // String() permite recibir el DNI como texto ("30111222") o como número (30111222).
  return PATRON_DNI.test(String(dni));
}

export default esDniValido;
