import api, { buscarSocioPorDni, obtenerActividades, obtenerActividad, obtenerInscripcionesDeSocio, inscribir, darDeBaja } from '../servicios/api';
import { mensajeDeError } from '../utils/mensajeDeError';
import { actividad, socio, inscripcion } from './fixturesApi';

jest.mock('axios', () => ({ create: jest.fn(() => ({ get: jest.fn(), post: jest.fn(), put: jest.fn() })) }));
beforeEach(() => jest.clearAllMocks());

test('consulta DNI real y conserva vigencia independiente de cuota', async () => {
  api.get.mockResolvedValue({ data: socio });
  expect(await buscarSocioPorDni(socio.dni)).toEqual(socio);
  expect(api.get).toHaveBeenCalledWith(`/socios/dni/${socio.dni}`);
});
test('catálogo con filtro y detalle usan el cliente único', async () => {
  api.get.mockResolvedValueOnce({ data: [actividad] }).mockResolvedValueOnce({ data: actividad });
  expect(await obtenerActividades('Yoga')).toEqual([actividad]);
  expect(api.get).toHaveBeenCalledWith('/actividades', { params: { disciplina: 'Yoga' } });
  expect(await obtenerActividad('1')).toEqual(actividad);
});
test.each([null, '<html>Error</html>', {}, [{ ...actividad, cuotaMensual: '10.25' }], [{ ...actividad, cupoDisponible: -1 }]])('rechaza catálogo inesperado %j', async (data) => {
  api.get.mockResolvedValue({ data });
  await expect(obtenerActividades()).rejects.toHaveProperty('codigo', 'RESPUESTA_INVALIDA');
});
test('catálogo vacío es válido', async () => {
  api.get.mockResolvedValue({ data: [] });
  expect(await obtenerActividades()).toEqual([]);
});
test('no cuenta bajas históricas como inscripciones activas', async () => {
  api.get.mockResolvedValue({ data: [inscripcion, { ...inscripcion, id: 2, estado: 'BAJA' }] });
  expect(await obtenerInscripcionesDeSocio(socio.id)).toEqual([inscripcion]);
});
test('rechaza inscripciones de otra persona o con actividad incongruente', async () => {
  api.get.mockResolvedValue({ data: [{ ...inscripcion, socioId: 50 }] });
  await expect(obtenerInscripcionesDeSocio(socio.id)).rejects.toHaveProperty('codigo', 'RESPUESTA_INVALIDA');
});
test('POST alta y PUT baja respetan el contrato del backend', async () => {
  api.post.mockResolvedValue({ data: inscripcion });
  api.put.mockResolvedValue({ data: { ...inscripcion, estado: 'BAJA' } });
  await inscribir(socio.id, actividad.id);
  await darDeBaja(inscripcion.id);
  expect(api.post).toHaveBeenCalledWith('/inscripciones', { socioId: socio.id, actividadId: actividad.id });
  expect(api.put).toHaveBeenCalledWith(`/inscripciones/${inscripcion.id}/baja`);
});
test('rechaza un alta con respuesta incompleta', async () => {
  api.post.mockResolvedValue({ data: { id: 1 } });
  await expect(inscribir(socio.id, actividad.id)).rejects.toHaveProperty('codigo', 'RESPUESTA_INVALIDA');
});
test.each([400,404,409,500,503])('no muestra texto técnico para HTTP %i', (status) => {
  const mensaje = mensajeDeError({ response: { status, data: { error: 'SQLException contraseña interna' } } });
  expect(mensaje).not.toMatch(/SQLException|contraseña/);
});
test('identifica SOCIO_INACTIVO por código', () => {
  expect(mensajeDeError({ response: { status:409, data:{ codigo:'SOCIO_INACTIVO' } } })).toMatch(/inactivo/);
});
