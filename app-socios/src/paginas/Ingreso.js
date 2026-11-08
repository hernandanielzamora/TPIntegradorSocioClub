import { useContext, useRef, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { SocioContext } from '../contexto/SocioContext';
import { buscarSocioPorDni } from '../servicios/api';
import { esDniValido } from '../utils/esDniValido';
import { mensajeDeError } from '../utils/mensajeDeError';

// Ruta "/": el socio escribe su DNI y la app lo busca.
function Ingreso() {
  const { socio, ingresar, cargandoSesion, errorSesion } = useContext(SocioContext);
  const envioEnCurso = useRef(false);
  const [dni, setDni] = useState('');
  const [errorDni, setErrorDni] = useState('');
  const [errorBusqueda, setErrorBusqueda] = useState('');
  const [buscando, setBuscando] = useState(false);
  const navigate = useNavigate();

  if (cargandoSesion) return <p role="status">Recuperando identificación…</p>;
  // El DNI identifica para esta demostración; no autentica al usuario.
  if (socio) {
    return <Navigate to="/actividades" replace />;
  }

  function manejarCambio(evento) {
    setDni(evento.target.value);
    setErrorDni('');
    setErrorBusqueda('');
  }

  function manejarEnvio(evento) {
    // Evita que el formulario recargue la página (comportamiento por defecto).
    evento.preventDefault();
    if (envioEnCurso.current) return;

    const dniLimpio = dni.trim();

    // RN7: si el DNI no es válido, no se busca y se muestra el error junto al campo.
    if (!esDniValido(dniLimpio)) {
      setErrorDni('Ingresá un DNI de 7 u 8 números, sin puntos ni espacios.');
      return;
    }

    envioEnCurso.current = true;
    setBuscando(true);
    buscarSocioPorDni(dniLimpio)
      .then((socioEncontrado) => {
        ingresar(socioEncontrado); // Se guarda en el contexto para todas las pantallas.
        navigate('/actividades');
      })
      .catch((error) => {
        setErrorBusqueda(error?.response?.status === 404 ? 'No encontramos un socio con ese DNI.' : mensajeDeError(error));
      })
      .finally(() => { envioEnCurso.current = false; setBuscando(false); });
  }

  return (
    <section className="pagina pagina-ingreso">
      <div className="tarjeta-ingreso">
        <h1>Ingresá a Mi Club</h1>
        <p className="texto-suave">
          Escribí tu DNI para ver tus actividades, tu cuota mensual y anotarte en las que quieras.
        </p>

        <p className="texto-suave">Identificación académica por DNI; no es una autenticación segura.</p>
        {errorSesion && <p role="alert">{errorSesion}</p>}
        <form onSubmit={manejarEnvio} noValidate>
          <div className="campo">
            <label htmlFor="dni">DNI</label>
            <input
              id="dni"
              name="dni"
              type="text"
              inputMode="numeric"
              autoComplete="off"
              placeholder="Ej.: 30111222"
              value={dni}
              onChange={manejarCambio}
              aria-invalid={errorDni ? 'true' : 'false'}
              aria-describedby={errorDni ? 'dni-error' : undefined}
              className={errorDni ? 'campo-con-error' : ''}
            />
            {errorDni && (
              <p id="dni-error" className="error-campo">
                {errorDni}
              </p>
            )}
          </div>

          {errorBusqueda && (
            <p className="mensaje mensaje-error" role="alert">
              {errorBusqueda}
            </p>
          )}

          <button type="submit" className="boton boton-grande boton-ancho" disabled={buscando}>
            {buscando ? 'Buscando…' : 'Ingresar'}
          </button>
        </form>
      </div>
    </section>
  );
}

export default Ingreso;
