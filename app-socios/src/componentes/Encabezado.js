import { Link, NavLink } from 'react-router-dom';

// Encabezado común a todas las pantallas.
// NavLink agrega sola la clase "active" al enlace de la página actual,
// y en el CSS se usa para resaltarlo.
function Encabezado() {
  return (
    <header className="encabezado">
      <div className="encabezado-contenido">
        <Link to="/" className="marca">
          <span className="marca-logo" aria-hidden="true">LA</span>
          <span className="marca-texto">
            <strong>Mi Club</strong>
            <small>Los Algarrobos</small>
          </span>
        </Link>

        <nav aria-label="Principal">
          <ul className="navegacion">
            <li>
              <NavLink to="/" end>
                Ingreso
              </NavLink>
            </li>
            <li>
              <NavLink to="/actividades">Actividades</NavLink>
            </li>
            <li>
              <NavLink to="/mis-actividades">Mis actividades</NavLink>
            </li>
          </ul>
        </nav>
      </div>
    </header>
  );
}

export default Encabezado;
