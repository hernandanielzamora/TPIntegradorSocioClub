import { useContext } from 'react';
import { Navigate } from 'react-router-dom';
import PropTypes from 'prop-types';
import { SocioContext } from '../contexto/SocioContext';

// Envuelve las pantallas que necesitan un socio ingresado.
// Si nadie ingresó con su DNI, redirige a la pantalla de ingreso ("/").
function RutaProtegida({ children }) {
  const { socio } = useContext(SocioContext);

  if (!socio) {
    return <Navigate to="/" replace />;
  }

  return children;
}

RutaProtegida.propTypes = {
  children: PropTypes.node.isRequired,
};

export default RutaProtegida;
