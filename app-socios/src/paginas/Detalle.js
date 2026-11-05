import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import DetalleActividad from '../componentes/DetalleActividad';
import { obtenerActividad } from '../servicios/datosLocales';

// Ruta "/actividades/:id": detalle de una actividad.
function Detalle() {
  // useParams lee la parte variable de la URL; en /actividades/3, id vale "3".
  const { id } = useParams();
  const [actividad, setActividad] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');

  // El efecto depende de [id]: se vuelve a ejecutar si cambia la URL.
  useEffect(() => {
    setCargando(true);
    setError('');
    obtenerActividad(id)
      .then((datos) => setActividad(datos))
      .catch((err) => setError(err.response?.data?.error || 'No se pudo cargar la actividad.'))
      .finally(() => setCargando(false));
  }, [id]);

  function manejarInscribir() {
    setMensaje('Para inscribirte primero ingresá con tu DNI.');
  }

  return (
    <section className="pagina">
      <Link to="/actividades" className="enlace-volver">
        ← Volver a actividades
      </Link>

      {cargando && <p className="mensaje mensaje-info">Cargando actividad…</p>}

      {error && (
        <p className="mensaje mensaje-error" role="alert">
          {error}
        </p>
      )}

      {mensaje && (
        <p className="mensaje mensaje-info" role="status">
          {mensaje} <Link to="/">Ir al ingreso</Link>
        </p>
      )}

      {!cargando && actividad && (
        <DetalleActividad actividad={actividad} onInscribir={manejarInscribir} />
      )}
    </section>
  );
}

export default Detalle;
