import { createContext, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import { buscarSocioPorDni } from '../servicios/api';
import { esDniValido } from '../utils/esDniValido';
import { mensajeDeError } from '../utils/mensajeDeError';

export const SocioContext = createContext(null);
const CLAVE = 'miclub-socio-dni';
function leerDni() {
  try {
    window.sessionStorage.removeItem('miclub-socio'); // Retirar objetos personales de la version local anterior.
    const dni = window.sessionStorage.getItem(CLAVE);
    return esDniValido(dni) ? dni : null;
  } catch { return null; }
}
export function SocioProvider({ children }) {
  const [dniInicial] = useState(leerDni);
  const [socio, setSocio] = useState(null);
  const [cargandoSesion, setCargandoSesion] = useState(Boolean(dniInicial));
  const [errorSesion, setErrorSesion] = useState('');
  useEffect(() => {
    let vigente = true;
    if (dniInicial) {
      buscarSocioPorDni(dniInicial).then((s) => { if (vigente) setSocio(s); })
        .catch((e) => { if (vigente) setErrorSesion(mensajeDeError(e)); })
        .finally(() => { if (vigente) setCargandoSesion(false); });
    }
    return () => { vigente = false; };
  }, [dniInicial]);
  useEffect(() => {
    if (cargandoSesion) return;
    try {
      if (socio) window.sessionStorage.setItem(CLAVE, socio.dni);
      else window.sessionStorage.removeItem(CLAVE);
    } catch { /* La identificacion en memoria sigue disponible. */ }
  }, [socio, cargandoSesion]);
  function ingresar(s) { setErrorSesion(''); setSocio(s); }
  function salir() { setErrorSesion(''); setSocio(null); }
  return <SocioContext.Provider value={{ socio, ingresar, salir, cargandoSesion, errorSesion }}>{children}</SocioContext.Provider>;
}
SocioProvider.propTypes = { children: PropTypes.node.isRequired };
