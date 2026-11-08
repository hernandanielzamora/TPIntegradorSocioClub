import axios from 'axios';
import { esDniValido } from '../utils/esDniValido';

// Un solo cliente HTTP; las pantallas no conocen URLs ni datos simulados.
const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: { Accept: 'application/json' },
});

function comprobar(condicion) {
  if (!condicion) {
    const error = new Error('Respuesta inesperada de la API');
    error.codigo = 'RESPUESTA_INVALIDA';
    throw error;
  }
}
const idValido = (id) => Number.isSafeInteger(id) && id > 0;
const texto = (valor) => typeof valor === 'string' && valor.trim().length > 0;
const fecha = (valor) => typeof valor === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(valor);
function actividadValida(a) {
  return a && idValido(a.id) && ['nombre', 'disciplina', 'profesor', 'dias', 'horario'].every((k) => texto(a[k])) &&
    Number.isInteger(a.edadMinima) && a.edadMinima >= 0 && Number.isInteger(a.edadMaxima) && a.edadMaxima >= a.edadMinima &&
    Number.isInteger(a.cupoMaximo) && a.cupoMaximo > 0 && Number.isInteger(a.cupoDisponible) &&
    a.cupoDisponible >= 0 && a.cupoDisponible <= a.cupoMaximo && typeof a.cuotaMensual === 'number' && Number.isFinite(a.cuotaMensual) && a.cuotaMensual >= 0;
}
function inscripcionValida(i) {
  return i && idValido(i.id) && idValido(i.socioId) && idValido(i.actividadId) && fecha(i.fechaInscripcion) &&
    ['ACTIVA', 'BAJA'].includes(i.estado) && actividadValida(i.actividad) && i.actividadId === i.actividad.id;
}

export async function buscarSocioPorDni(dni) {
  comprobar(esDniValido(dni));
  const { data: s } = await api.get(`/socios/dni/${encodeURIComponent(dni)}`);
  comprobar(s && idValido(s.id) && s.dni === String(dni) && texto(s.nombre) && texto(s.apellido) &&
    fecha(s.fechaNacimiento) && ['AL_DIA', 'MOROSO'].includes(s.estadoCuota) && ['ACTIVO', 'INACTIVO'].includes(s.estadoSocio));
  return s;
}
export async function obtenerActividades(disciplina) {
  const { data } = await api.get('/actividades', disciplina ? { params: { disciplina } } : undefined);
  comprobar(Array.isArray(data) && data.every(actividadValida) && new Set(data.map((a) => a.id)).size === data.length);
  return data;
}
export async function obtenerActividad(id) {
  comprobar(idValido(Number(id)));
  const { data } = await api.get(`/actividades/${Number(id)}`);
  comprobar(actividadValida(data) && data.id === Number(id));
  return data;
}
export async function obtenerInscripcionesDeSocio(socioId) {
  comprobar(idValido(socioId));
  const { data } = await api.get(`/socios/${socioId}/inscripciones`);
  comprobar(Array.isArray(data) && data.every((i) => inscripcionValida(i) && i.socioId === socioId) &&
    new Set(data.map((i) => i.id)).size === data.length);
  // La cuota solo incluye ACTIVAS incluso ante una respuesta que contenga historial.
  return data.filter((i) => i.estado === 'ACTIVA');
}
export async function inscribir(socioId, actividadId) {
  comprobar(idValido(socioId) && idValido(actividadId));
  const { data } = await api.post('/inscripciones', { socioId, actividadId });
  comprobar(inscripcionValida(data) && data.socioId === socioId && data.actividadId === actividadId && data.estado === 'ACTIVA');
  return data;
}
export async function darDeBaja(id) {
  comprobar(idValido(id));
  const { data } = await api.put(`/inscripciones/${id}/baja`);
  comprobar(inscripcionValida(data) && data.id === id && data.estado === 'BAJA');
  return data;
}
export default api;
