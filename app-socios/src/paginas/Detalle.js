import { useContext, useEffect, useRef, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import DetalleActividad from '../componentes/DetalleActividad';
import { SocioContext } from '../contexto/SocioContext';
import { inscribir, obtenerActividad } from '../servicios/api';
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
  const [procesando, setProcesando] = useState(false);
  const enCurso = useRef(false);

  // El efecto depende de [id]: se vuelve a ejecutar si cambia la URL.
  useEffect(() => {
    setCargando(true);
    setError('');
    setActividad(null);
    setAviso(null);
    let vigente = true;
    obtenerActividad(id)
      .then((datos) => { if (vigente) setActividad(datos); })
      .catch((err) => { if (vigente) setError(mensajeDeError(err)); })
      .finally(() => { if (vigente) setCargando(false); });
    return () => { vigente = false; };
  }, [id]);

  async function manejarInscribir() {
    if (enCurso.current) return;
    enCurso.current = true;
    setProcesando(true);
    let registrada = false;
    setAviso(null);
    try {
      await inscribir(socio.id, actividad.id);
      registrada = true;
      setAviso({ tipo: 'exito', texto: `¡Listo! Te inscribiste en ${actividad.nombre}.` });
      // Se vuelve a pedir la actividad para mostrar el cupo actualizado.
      const actualizada = await obtenerActividad(id);
      setActividad(actualizada);
    } catch (err) {
      // Si una regla rechaza la inscripción (409) se muestra su mensaje.
      setAviso({ tipo: 'error', texto: registrada ? 'La inscripción se registró, pero no pudimos actualizar los cupos. Recargá la pantalla.' : mensajeDeError(err) });
    } finally {
      enCurso.current = false;
      setProcesando(false);
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

      {socio.estadoSocio === 'INACTIVO' && <p role="alert">El socio está inactivo. Consultá en Secretaría.</p>}
      {!cargando && !error && actividad && (
        <DetalleActividad actividad={actividad} onInscribir={manejarInscribir} bloqueado={procesando || socio.estadoSocio === 'INACTIVO'} />
      )}
    </section>
  );
}

export default Detalle;
