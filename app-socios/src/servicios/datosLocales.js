import { actividadesEjemplo, inscripcionesEjemplo, sociosEjemplo } from '../datos/datosEjemplo';

// Servicio TEMPORAL que simula la API REST con los datos locales.
// Cada función devuelve una Promesa, igual que una llamada con Axios. Así las
// pantallas ya usan useEffect y .then() como lo harán con el backend real.
// Para conectar la API (tarea 6.5) se escriben funciones con los mismos nombres
// en api.js usando Axios, se cambia el import en las páginas y se borra este archivo.
//
// IMPORTANTE: las reglas RN1 a RN5 que se simulan acá son solo para probar las
// pantallas. En el sistema real se validan una sola vez en la capa de servicio
// del backend (Spring Boot), que es la que responde 409 con el mensaje.

const DEMORA_MS = 300; // Simula el tiempo de respuesta de la red.
const MAXIMO_ACTIVIDADES = 3;

// Copias de los datos para no modificar los arreglos originales.
// Se reinician al recargar la página (F5).
const actividades = actividadesEjemplo.map((actividad) => ({ ...actividad }));
const inscripciones = inscripcionesEjemplo.map((inscripcion) => ({ ...inscripcion }));
let proximoIdInscripcion = inscripciones.length + 1;

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

// Edad cumplida a la fecha de hoy, a partir de "AAAA-MM-DD".
function calcularEdad(fechaNacimiento) {
  const hoy = new Date();
  const nacimiento = new Date(`${fechaNacimiento}T00:00:00`);
  let edad = hoy.getFullYear() - nacimiento.getFullYear();
  const diferenciaMeses = hoy.getMonth() - nacimiento.getMonth();
  if (diferenciaMeses < 0 || (diferenciaMeses === 0 && hoy.getDate() < nacimiento.getDate())) {
    edad -= 1;
  }
  return edad;
}

function inscripcionesActivasDe(socioId) {
  return inscripciones.filter((i) => i.socioId === socioId && i.estado === 'ACTIVA');
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

// Simula GET /api/socios/dni/{dni} (200 o 404)
export function buscarSocioPorDni(dni) {
  const socio = sociosEjemplo.find((s) => s.dni === String(dni));
  if (!socio) {
    return rechazar(404, 'No encontramos un socio con ese DNI');
  }
  return responder({ ...socio });
}

// Simula POST /api/inscripciones con cuerpo { socioId, actividadId } (201, 404 o 409)
export function inscribir(socioId, actividadId) {
  const socio = sociosEjemplo.find((s) => s.id === Number(socioId));
  const actividad = actividades.find((a) => a.id === Number(actividadId));
  if (!socio || !actividad) {
    return rechazar(404, 'El socio o la actividad no existen');
  }

  const activas = inscripcionesActivasDe(socio.id);
  const edad = calcularEdad(socio.fechaNacimiento);

  if (actividad.cupoDisponible <= 0) {
    return rechazar(409, 'La actividad no tiene cupo disponible', 'RN1');
  }
  if (activas.some((i) => i.actividadId === actividad.id)) {
    return rechazar(409, 'El socio ya está inscripto en esta actividad', 'RN2');
  }
  if (edad < actividad.edadMinima || edad > actividad.edadMaxima) {
    return rechazar(
      409,
      `La edad permitida para esta actividad es de ${actividad.edadMinima} a ${actividad.edadMaxima} años`,
      'RN3'
    );
  }
  if (socio.estadoCuota === 'MOROSO') {
    return rechazar(409, 'Debe regularizar su cuota en secretaría', 'RN4');
  }
  if (activas.length >= MAXIMO_ACTIVIDADES) {
    return rechazar(409, `Alcanzó el máximo de ${MAXIMO_ACTIVIDADES} actividades`, 'RN5');
  }

  const nueva = {
    id: proximoIdInscripcion,
    socioId: socio.id,
    actividadId: actividad.id,
    fechaInscripcion: new Date().toISOString().slice(0, 10),
    estado: 'ACTIVA',
  };
  proximoIdInscripcion += 1;
  inscripciones.push(nueva);
  actividad.cupoDisponible -= 1;

  return responder({ ...nueva, actividad: { ...actividad } });
}

// Simula GET /api/socios/{id}/inscripciones (200 o 404)
// Devuelve solo las inscripciones ACTIVAS, cada una con los datos de su actividad.
export function obtenerInscripcionesDeSocio(socioId) {
  const socio = sociosEjemplo.find((s) => s.id === Number(socioId));
  if (!socio) {
    return rechazar(404, 'El socio no existe');
  }
  const activas = inscripcionesActivasDe(socio.id).map((inscripcion) => ({
    id: inscripcion.id,
    fechaInscripcion: inscripcion.fechaInscripcion,
    estado: inscripcion.estado,
    actividad: { ...actividades.find((a) => a.id === inscripcion.actividadId) },
  }));
  return responder(activas);
}

// Simula PUT /api/inscripciones/{id}/baja (200 o 404)
// Baja lógica: la inscripción no se borra, cambia su estado a BAJA y se libera el cupo.
// RN4 no aplica acá: un socio MOROSO también puede darse de baja.
export function darDeBaja(inscripcionId) {
  const inscripcion = inscripciones.find((i) => i.id === Number(inscripcionId));
  if (!inscripcion) {
    return rechazar(404, 'La inscripción no existe');
  }
  if (inscripcion.estado === 'ACTIVA') {
    inscripcion.estado = 'BAJA';
    const actividad = actividades.find((a) => a.id === inscripcion.actividadId);
    actividad.cupoDisponible += 1;
  }
  return responder({ ...inscripcion });
}
