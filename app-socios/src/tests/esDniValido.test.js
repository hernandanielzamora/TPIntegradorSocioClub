import { esDniValido } from '../utils/esDniValido';

describe('esDniValido (RN7)', () => {
  test('acepta un DNI de 8 dígitos', () => {
    expect(esDniValido('30111222')).toBe(true);
  });

  test('acepta un DNI de 7 dígitos', () => {
    expect(esDniValido('4123456')).toBe(true);
  });

  test('acepta el DNI escrito como número', () => {
    expect(esDniValido(30111222)).toBe(true);
  });

  test('rechaza un DNI con puntos', () => {
    expect(esDniValido('30.111.222')).toBe(false);
  });

  test('rechaza un DNI con letras', () => {
    expect(esDniValido('30111A22')).toBe(false);
  });

  test('rechaza un DNI de 6 dígitos (uno menos que el mínimo)', () => {
    expect(esDniValido('301112')).toBe(false);
  });

  test('rechaza un DNI de 9 dígitos (uno más que el máximo)', () => {
    expect(esDniValido('301112223')).toBe(false);
  });

  test('rechaza un DNI con espacios', () => {
    expect(esDniValido('30 111 222')).toBe(false);
  });

  test('rechaza un DNI vacío, nulo o sin definir', () => {
    expect(esDniValido('')).toBe(false);
    expect(esDniValido(null)).toBe(false);
    expect(esDniValido(undefined)).toBe(false);
  });
});
