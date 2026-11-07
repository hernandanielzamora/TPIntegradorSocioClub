import { useContext, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import DetalleActividad from '../componentes/DetalleActividad';
import { SocioContext } from '../contexto/SocioContext';
import { inscribir, obtenerActividad } from '../servicios/datosLocales';
import { mensajeDeError } from '../utils/mensajeDeError';

// Ruta "/actividades/:id": detalle de una actividad.
function Detalle() {
  // useParams lee la parte variable de la URL; en /actividades/3, id vale "3".
  const { id } = useParams();
  const { socio } = useContext(SocioContext);
  const [actividad, setActividad] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState(null); // { tipo: 'exito' | 'error', texto }

  // El efecto depende de [id]: se vuelve a ejecutar si cambia la URL.
  useEffect(() => {
    setCargando(true);
    setError('');
    obtenerActividad(id)
      .then((datos) => setActividad(datos))
      .catch((err) => setError(mensajeDeError(err)))
      .finally(() => setCargando(false));
  }, [id]);

  async function manejarInscribir() {
    setAviso(null);
    try {
      await inscribir(socio.id, actividad.id);
      setAviso({ tipo: 'exito', texto: `¡Listo! Te inscribiste en ${actividad.nombre}.` });
      // Se vuelve a pedir la actividad para mostrar el cupo actualizado.
      const actualizada = await obtenerActividad(id);
      setActividad(actualizada);
    } catch (err) {
      // Si una regla rechaza la inscripción (409) se muestra su mensaje.
      setAviso({ tipo: 'error', texto: mensajeDeError(err) });
    }
    // El mensaje está arriba del detalle: se sube la página para que se vea.
    window.scrollTo({ top: 0, behavior: 'smooth' });
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

      {aviso && (
        <p className={`mensaje mensaje-${aviso.tipo}`} role={aviso.tipo === 'error' ? 'alert' : 'status'}>
          {aviso.texto}{' '}
          {aviso.tipo === 'exito' && <Link to="/mis-actividades">Ver mis actividades</Link>}
        </p>
      )}

      {!cargando && actividad && (
        <DetalleActividad actividad={actividad} onInscribir={manejarInscribir} />
      )}
    </section>
  );
}

export default Detalle;
