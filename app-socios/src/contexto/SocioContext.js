import { createContext, useEffect, useState } from 'react';
import PropTypes from 'prop-types';

// Contexto del socio que ingresó a la app.
// Cualquier componente que esté adentro de <SocioProvider> puede leer el socio
// con useContext(SocioContext), sin pasarlo por props de componente en componente.
export const SocioContext = createContext(null);

const CLAVE_GUARDADO = 'miclub-socio';

// sessionStorage (objeto del navegador) guarda el socio mientras la pestaña
// esté abierta, así no se pierde el ingreso al recargar la página.
function leerSocioGuardado() {
  try {
    const guardado = window.sessionStorage.getItem(CLAVE_GUARDADO);
    return guardado ? JSON.parse(guardado) : null;
  } catch (error) {
    return null;
  }
}

export function SocioProvider({ children }) {
  // Estado con el socio que ingresó (null si nadie ingresó).
  const [socio, setSocio] = useState(leerSocioGuardado);

  // Cada vez que cambia el socio (dependencia [socio]) se actualiza lo guardado.
  useEffect(() => {
    try {
      if (socio) {
        window.sessionStorage.setItem(CLAVE_GUARDADO, JSON.stringify(socio));
      } else {
        window.sessionStorage.removeItem(CLAVE_GUARDADO);
      }
    } catch (error) {
      // Si el navegador no permite guardar, la app sigue funcionando igual.
    }
  }, [socio]);

  function ingresar(socioEncontrado) {
    setSocio(socioEncontrado);
  }

  function salir() {
    setSocio(null);
  }

  // value es lo que reciben los componentes que usan useContext(SocioContext).
  return (
    <SocioContext.Provider value={{ socio, ingresar, salir }}>{children}</SocioContext.Provider>
  );
}

SocioProvider.propTypes = {
  children: PropTypes.node.isRequired,
};
