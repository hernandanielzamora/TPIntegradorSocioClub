import PropTypes from 'prop-types';
import { actividadPropType } from './tiposActividad';
import { formatearPesos } from '../utils/formatearPesos';

// Muestra todos los datos de una actividad y el botón «Inscribirme».
function DetalleActividad({ actividad, onInscribir }) {
  const sinCupo = actividad.cupoDisponible <= 0;
  const ocupados = actividad.cupoMaximo - actividad.cupoDisponible;
  const porcentajeOcupado = Math.round((ocupados / actividad.cupoMaximo) * 100);

  let textoDisponibles = `${actividad.cupoDisponible} lugares disponibles`;
  if (sinCupo) {
    textoDisponibles = 'Sin cupo';
  } else if (actividad.cupoDisponible === 1) {
    textoDisponibles = '1 lugar disponible';
  }

  return (
    <article className={sinCupo ? 'detalle-actividad sin-cupo' : 'detalle-actividad'}>
      <header className="detalle-cabecera">
        <span className="etiqueta-disciplina">{actividad.disciplina}</span>
        <h1>{actividad.nombre}</h1>
        <p className="texto-suave">A cargo de {actividad.profesor}</p>
      </header>

      <dl className="detalle-datos">
        <div>
          <dt>Días</dt>
          <dd>{actividad.dias}</dd>
        </div>
        <div>
          <dt>Horario</dt>
          <dd>{actividad.horario} h</dd>
        </div>
        <div>
          <dt>Edades</dt>
          <dd>
            {actividad.edadMinima} a {actividad.edadMaxima} años
          </dd>
        </div>
        <div>
          <dt>Cuota mensual</dt>
          <dd>{formatearPesos(actividad.cuotaMensual)}</dd>
        </div>
      </dl>

      <section className="detalle-cupo" aria-label="Cupo de la actividad">
        <p className="cupo-texto">
          <strong>{textoDisponibles}</strong>
          <span>
            {ocupados} de {actividad.cupoMaximo} ocupados
          </span>
        </p>
        <div
          className="barra-cupo"
          role="progressbar"
          aria-label="Lugares ocupados"
          aria-valuemin={0}
          aria-valuemax={actividad.cupoMaximo}
          aria-valuenow={ocupados}
        >
          <div className="barra-cupo-relleno" style={{ width: `${porcentajeOcupado}%` }} />
        </div>
      </section>

      <button
        type="button"
        className="boton boton-grande"
        disabled={sinCupo}
        onClick={() => onInscribir(actividad)}
      >
        Inscribirme
      </button>
    </article>
  );
}

DetalleActividad.propTypes = {
  actividad: actividadPropType.isRequired,
  onInscribir: PropTypes.func.isRequired,
};

export default DetalleActividad;
