// Saca el texto que hay que mostrarle al socio a partir de un error.
// Cuando la API rechaza una operación responde, por ejemplo:
//   409 → { "error": "La actividad no tiene cupo disponible", "regla": "RN1" }
// Axios guarda ese cuerpo en error.response.data.
// El operador ?. evita un error si alguna parte no existe (por ejemplo, sin conexión).
export function mensajeDeError(error) {
  return error?.response?.data?.error || 'No se pudo conectar con el servidor. Probá de nuevo.';
}

export default mensajeDeError;
