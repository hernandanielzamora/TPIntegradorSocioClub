import {
  calcularCuotaMensual,
  obtenerPorcentajeDescuento,
} from '../utils/calcularCuotaMensual';

// Cuotas del enunciado (sección 3.3) usadas como datos de prueba.
const futbol = { nombre: 'Fútbol infantil', cuotaMensual: 18000 };
const voley = { nombre: 'Vóley mixto', cuotaMensual: 20000 };
const natacion = { nombre: 'Natación', cuotaMensual: 26000 };

describe('calcularCuotaMensual (RN6)', () => {
  test('con 0 actividades la cuota es 0', () => {
    expect(calcularCuotaMensual([])).toBe(0);
  });

  test('con 1 actividad se paga la cuota completa, sin descuento', () => {
    expect(calcularCuotaMensual([futbol])).toBe(18000);
  });

  test('con 2 actividades se aplica 10 % de descuento', () => {
    // (18000 + 20000) = 38000 → 38000 - 10 % = 34200
    expect(calcularCuotaMensual([futbol, voley])).toBe(34200);
  });

  test('con 3 actividades se aplica 15 % de descuento', () => {
    // (18000 + 20000 + 26000) = 64000 → 64000 - 15 % = 54400
    expect(calcularCuotaMensual([futbol, voley, natacion])).toBe(54400);
  });

  test('si no recibe un arreglo devuelve 0', () => {
    expect(calcularCuotaMensual(undefined)).toBe(0);
    expect(calcularCuotaMensual(null)).toBe(0);
  });

  test('no modifica el arreglo que recibe (función pura)', () => {
    const actividades = [futbol, voley];
    calcularCuotaMensual(actividades);
    expect(actividades).toEqual([futbol, voley]);
  });
});

describe('obtenerPorcentajeDescuento', () => {
  test('devuelve 0, 0, 10 y 15 para 0, 1, 2 y 3 actividades', () => {
    expect(obtenerPorcentajeDescuento(0)).toBe(0);
    expect(obtenerPorcentajeDescuento(1)).toBe(0);
    expect(obtenerPorcentajeDescuento(2)).toBe(10);
    expect(obtenerPorcentajeDescuento(3)).toBe(15);
  });
});
