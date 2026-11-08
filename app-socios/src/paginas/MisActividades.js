import { useContext, useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import ResumenCuota from '../componentes/ResumenCuota';
import { SocioContext } from '../contexto/SocioContext';
import { darDeBaja, obtenerInscripcionesDeSocio } from '../servicios/api';
import { formatearPesos } from '../utils/formatearPesos';
import { mensajeDeError } from '../utils/mensajeDeError';

// "2026-03-05" → "05/03/2026"
function formatearFecha(fechaIso) {
  const [anio, mes, dia] = fechaIso.split('-');
  return `${dia}/${mes}/${anio}`;
}

// Ruta "/mis-actividades": inscripciones activas del socio, baja y cuota estimada.
function MisActividades() {
  const { socio } = useContext(SocioContext);
  const [inscripciones, setInscripciones] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState(null); // { tipo: 'exito' | 'error', texto }
  const [idEnProceso, setIdEnProceso] = useState(null);
  const enCurso = useRef(false);

  // Se cargan las inscripciones del socio. Depende de [socio.id]: si cambiara
  // el socio, el efecto se vuelve a ejecutar y trae las del nuevo socio.
  useEffect(() => {
    let vigente = true;
    setCargando(true);
    setError('');
    obtenerInscripcionesDeSocio(socio.id)
      .then((datos) => { if (vigente) setInscripciones(datos); })
      .catch((err) => { if (vigente) setError(mensajeDeError(err)); })
      .finally(() => { if (vigente) setCargando(false); });
    return () => { vigente = false; };
  }, [socio.id]);

  async function manejarBaja(inscripcion) {
    if (enCurso.current || !window.confirm(`¿Confirmás darte de baja de ${inscripcion.actividad.nombre}?`)) return;
    enCurso.current = true;
    setAviso(null);
    setIdEnProceso(inscripcion.id);
    let confirmada = false;
    try {
      await darDeBaja(inscripcion.id);
      confirmada = true;
      // Se vuelve a pedir la lista: la inscripción dada de baja ya no aparece.
      const actualizadas = await obtenerInscripcionesDeSocio(socio.id);
      setInscripciones(actualizadas);
      setAviso({ tipo: 'exito', texto: `Te diste de baja de ${inscripcion.actividad.nombre}.` });
    } catch (err) {
      setAviso({ tipo: 'error', texto: confirmada ? 'La baja se registró, pero no pudimos actualizar la lista. Recargá la pantalla.' : mensajeDeError(err) });
    } finally {
      enCurso.current = false;
      setIdEnProceso(null);
    }
    // El mensaje está arriba de la lista: se sube la página para que se vea.
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // ResumenCuota necesita solo las actividades de cada inscripción.
  const actividades = inscripciones.map((inscripcion) => inscripcion.actividad);

  return (
    <section className="pagina">
      <h1>Mis actividades</h1>
      <p className="texto-suave">
        Hola, {socio.nombre}. Estas son las actividades a las que estás inscripto.
      </p>

      {/* RN4: un socio MOROSO puede consultar y darse de baja, pero no anotarse. */}
      {socio.estadoCuota === 'MOROSO' && (
        <p className="mensaje mensaje-alerta">
          Tu cuota figura impaga. Podés consultar y darte de baja, pero para anotarte en nuevas
          actividades tenés que regularizarla en secretaría.
        </p>
      )}

      {aviso && (
        <p className={`mensaje mensaje-${aviso.tipo}`} role={aviso.tipo === 'error' ? 'alert' : 'status'}>
          {aviso.texto}
        </p>
      )}

      {cargando && <p className="mensaje mensaje-info">Cargando tus actividades…</p>}

      {error && (
        <p className="mensaje mensaje-error" role="alert">
          {error}
        </p>
      )}

      {/* Renderizado condicional: mensaje cuando el socio no tiene actividades. */}
      {!cargando && !error && inscripciones.length === 0 && (
        <div className="sin-actividades">
          <p>Todavía no estás inscripto en ninguna actividad.</p>
          <Link to="/actividades" className="boton">
            Ver actividades
          </Link>
        </div>
      )}

      {!cargando && !error && inscripciones.length > 0 && (
        <div className="mis-actividades">
          <ul className="lista-inscripciones">
            {inscripciones.map((inscripcion) => (
              <li key={inscripcion.id} className="inscripcion">
                <div className="inscripcion-datos">
                  <span className="etiqueta-disciplina">{inscripcion.actividad.disciplina}</span>
                  <h2 className="inscripcion-titulo">
                    <Link to={`/actividades/${inscripcion.actividad.id}`}>
                      {inscripcion.actividad.nombre}
                    </Link>
                  </h2>
                  <p>
                    {inscripcion.actividad.dias} · {inscripcion.actividad.horario} h ·{' '}
                    {inscripcion.actividad.profesor}
                  </p>
                  <p className="texto-suave">
                    Inscripto desde el {formatearFecha(inscripcion.fechaInscripcion)} ·{' '}
                    {formatearPesos(inscripcion.actividad.cuotaMensual)} por mes
                  </p>
                </div>
                <button
                  type="button"
                  className="boton boton-secundario boton-baja"
                  disabled={idEnProceso !== null}
                  onClick={() => manejarBaja(inscripcion)}
                >
                  {idEnProceso === inscripcion.id ? 'Procesando…' : 'Darme de baja'}
                </button>
              </li>
            ))}
          </ul>
          <ResumenCuota actividades={actividades} />
        </div>
      )}
      {!cargando && !error && inscripciones.length === 0 && <ResumenCuota actividades={actividades} />}
    </section>
  );
}

export default MisActividades;
