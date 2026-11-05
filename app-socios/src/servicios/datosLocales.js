import { actividadesEjemplo } from '../datos/datosEjemplo';

// Servicio TEMPORAL que simula la API REST con los datos locales.
// Cada función devuelve una Promesa, igual que una llamada con Axios. Así las
// pantallas ya usan useEffect y .then() como lo harán con el backend real.
// Para conectar la API (tarea 6.5) se escriben funciones con los mismos nombres
// en api.js usando Axios y se cambia el import en las páginas.

const DEMORA_MS = 300; // Simula el tiempo de respuesta de la red.

// Copia de los datos para no modificar el arreglo original.
const actividades = actividadesEjemplo.map((actividad) => ({ ...actividad }));

function responder(datos) {
  return new Promise((resolve) => {
    setTimeout(() => resolve(datos), DEMORA_MS);
  });
}

// Devuelve un error con la misma forma que los errores de Axios
// (error.response.status y error.response.data), para que las pantallas
// muestren el mensaje igual que cuando responda el backend.
function rechazar(status, mensaje, regla) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const error = new Error(mensaje);
      error.response = { status, data: { error: mensaje, regla } };
      reject(error);
    }, DEMORA_MS);
  });
}

// Simula GET /api/actividades
export function obtenerActividades() {
  return responder(actividades.map((actividad) => ({ ...actividad })));
}

// Simula GET /api/actividades/{id} (200 o 404)
export function obtenerActividad(id) {
  const actividad = actividades.find((a) => a.id === Number(id));
  if (!actividad) {
    return rechazar(404, 'La actividad no existe');
  }
  return responder({ ...actividad });
}
