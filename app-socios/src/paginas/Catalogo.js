import { useContext, useEffect, useState } from 'react';
import ListaActividades from '../componentes/ListaActividades';
import { SocioContext } from '../contexto/SocioContext';
import { inscribir, obtenerActividades } from '../servicios/datosLocales';
import { mensajeDeError } from '../utils/mensajeDeError';

const TODAS = 'Todas';

// Ruta "/actividades": catálogo con filtro por disciplina.
function Catalogo() {
  // El socio viene del contexto, no por props.
  const { socio } = useContext(SocioContext);

  // Estado local de la página.
  const [actividades, setActividades] = useState([]);
  const [disciplina, setDisciplina] = useState(TODAS);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState(null); // { tipo: 'exito' | 'error', texto }

  // useEffect con arreglo de dependencias vacío [] se ejecuta una sola vez,
  // cuando la página aparece en pantalla. Ahí se cargan las actividades.
  useEffect(() => {
    obtenerActividades()
      .then((datos) => setActividades(datos))
      .catch(() => setError('No se pudieron cargar las actividades. Probá de nuevo más tarde.'))
      .finally(() => setCargando(false));
  }, []);

  // Disciplinas sin repetir (Set elimina duplicados) para las opciones del filtro.
  const disciplinas = [TODAS, ...new Set(actividades.map((a) => a.disciplina))];

  // Cada vez que cambia el estado "disciplina", React vuelve a dibujar la página
  // y esta lista se recalcula con el nuevo filtro.
  const actividadesFiltradas =
    disciplina === TODAS ? actividades : actividades.filter((a) => a.disciplina === disciplina);

  // Se ejecuta al tocar «Inscribirme» en una tarjeta (la tarjeta avisa con la prop onInscribir).
  async function manejarInscribir(actividad) {
    setAviso(null);
    try {
      await inscribir(socio.id, actividad.id);
      setAviso({ tipo: 'exito', texto: `¡Listo! Te inscribiste en ${actividad.nombre}.` });
      // Se vuelven a pedir las actividades para mostrar el cupo actualizado.
      const actualizadas = await obtenerActividades();
      setActividades(actualizadas);
    } catch (err) {
      // Si una regla rechaza la inscripción (409) se muestra su mensaje.
      setAviso({ tipo: 'error', texto: mensajeDeError(err) });
    }
    // El mensaje está arriba de la lista: se sube la página para que se vea.
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  return (
    <section className="pagina">
      <div className="pagina-encabezado">
        <div>
          <h1>Actividades</h1>
          <p className="texto-suave">Elegí una disciplina y mirá el cupo disponible de cada actividad.</p>
        </div>

        <div className="filtro">
          <label htmlFor="filtro-disciplina">Disciplina</label>
          <select
            id="filtro-disciplina"
            value={disciplina}
            onChange={(evento) => setDisciplina(evento.target.value)}
          >
            {disciplinas.map((opcion) => (
              <option key={opcion} value={opcion}>
                {opcion}
              </option>
            ))}
          </select>
        </div>
      </div>

      {aviso && (
        <p className={`mensaje mensaje-${aviso.tipo}`} role={aviso.tipo === 'error' ? 'alert' : 'status'}>
          {aviso.texto}
        </p>
      )}

      {cargando && <p className="mensaje mensaje-info">Cargando actividades…</p>}

      {error && (
        <p className="mensaje mensaje-error" role="alert">
          {error}
        </p>
      )}

      {!cargando && !error && (
        <>
          <p className="resultado-filtro">
            {actividadesFiltradas.length === 1
              ? '1 actividad'
              : `${actividadesFiltradas.length} actividades`}
          </p>
          <ListaActividades actividades={actividadesFiltradas} onInscribir={manejarInscribir} />
        </>
      )}
    </section>
  );
}

export default Catalogo;
