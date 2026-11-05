import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ListaActividades from '../componentes/ListaActividades';
import { obtenerActividades } from '../servicios/datosLocales';

const TODAS = 'Todas';

// Ruta "/actividades": catálogo con filtro por disciplina.
function Catalogo() {
  // Estado local de la página.
  const [actividades, setActividades] = useState([]);
  const [disciplina, setDisciplina] = useState(TODAS);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

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

  function manejarInscribir(actividad) {
    // Lleva al detalle de la actividad, donde se confirma la inscripción.
    navigate(`/actividades/${actividad.id}`);
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
