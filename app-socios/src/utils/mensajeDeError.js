// Mensajes propios: nunca mostrar SQL, trazas o textos tecnicos recibidos del servidor.
export function mensajeDeError(error) {
  if (error?.codigo === 'RESPUESTA_INVALIDA') return 'El servidor devolvió datos inesperados. Intentá nuevamente.';
  const estado = error?.response?.status;
  const datos = error?.response?.data;
  if (estado === 409) {
    if (datos?.codigo === 'SOCIO_INACTIVO') return 'El socio está inactivo. Consultá en Secretaría.';
    const reglas = {
      RN1: 'La actividad no tiene cupo disponible.',
      RN2: 'Ya estás inscripto en esta actividad.',
      RN3: 'Tu edad está fuera del rango permitido para esta actividad.',
      RN4: 'Debe regularizar su cuota en secretaría.',
      RN5: 'Alcanzaste el máximo de tres actividades activas.',
    };
    return reglas[datos?.regla] || 'La operación entra en conflicto con los datos actuales. Actualizá la pantalla.';
  }
  if (estado === 400) return 'Revisá los datos enviados.';
  if (estado === 404) return 'No encontramos el socio, la actividad o la inscripción solicitada.';
  if (estado) return 'El servidor no pudo completar la operación. Intentá más tarde.';
  return 'No se pudo conectar con el servidor. Probá de nuevo.';
}
export default mensajeDeError;
