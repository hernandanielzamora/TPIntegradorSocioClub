import { useContext } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { SocioContext } from '../contexto/SocioContext';

// Encabezado común a todas las pantallas.
// Lee el socio desde el contexto: si ingresó, muestra el menú, su nombre y «Salir».
// NavLink agrega sola la clase "active" al enlace de la página actual.
function Encabezado() {
  const { socio, salir } = useContext(SocioContext);
  const navigate = useNavigate();

  function manejarSalir() {
    salir();
    navigate('/');
  }

  return (
    <header className="encabezado">
      <div className="encabezado-contenido">
        <Link to={socio ? '/actividades' : '/'} className="marca">
          <span className="marca-logo" aria-hidden="true">LA</span>
          <span className="marca-texto">
            <strong>Mi Club</strong>
            <small>Los Algarrobos</small>
          </span>
        </Link>

        {socio && (
          <div className="encabezado-socio">
            <nav aria-label="Principal">
              <ul className="navegacion">
                <li>
                  <NavLink to="/actividades">Actividades</NavLink>
                </li>
                <li>
                  <NavLink to="/mis-actividades">Mis actividades</NavLink>
                </li>
              </ul>
            </nav>

            <div className="usuario">
              <span className="usuario-nombre">
                {socio.nombre} {socio.apellido}
              </span>
              <button type="button" className="boton boton-secundario boton-chico" onClick={manejarSalir}>
                Salir
              </button>
            </div>
          </div>
        )}
      </div>
    </header>
  );
}

export default Encabezado;
