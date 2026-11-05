import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { actividadPropType } from './tiposActividad';
import { formatearPesos } from '../utils/formatearPesos';

// Texto del cupo según cuántos lugares quedan.
function textoCupo(cupoDisponible, cupoMaximo) {
  if (cupoDisponible <= 0) {
    return 'Completo';
  }
  if (cupoDisponible === 1) {
    return `Queda 1 lugar de ${cupoMaximo}`;
  }
  return `Quedan ${cupoDisponible} lugares de ${cupoMaximo}`;
}

// Tarjeta de una actividad. Recibe los datos por props desde ListaActividades.
function TarjetaActividad({ actividad, onInscribir }) {
  const sinCupo = actividad.cupoDisponible <= 0;
  const pocosLugares = !sinCupo && actividad.cupoDisponible <= 3;

  // Clase aplicada según el estado: sin cupo, la tarjeta se ve en gris.
  const clases = sinCupo ? 'tarjeta-actividad sin-cupo' : 'tarjeta-actividad';

  return (
    <article className={clases}>
      <div className="tarjeta-cabecera">
        <span className="etiqueta-disciplina">{actividad.disciplina}</span>
        {/* Renderizado condicional: cada etiqueta aparece solo si se cumple la condición. */}
        {sinCupo && <span className="etiqueta etiqueta-sin-cupo">Sin cupo</span>}
        {pocosLugares && <span className="etiqueta etiqueta-pocos">Últimos lugares</span>}
      </div>

      <h3 className="tarjeta-titulo">
        <Link to={`/actividades/${actividad.id}`}>{actividad.nombre}</Link>
      </h3>

      <ul className="datos-actividad">
        <li>
          <span>Días</span> {actividad.dias} · {actividad.horario} h
        </li>
        <li>
          <span>Edades</span> {actividad.edadMinima} a {actividad.edadMaxima} años
        </li>
        <li>
          <span>Profesor/a</span> {actividad.profesor}
        </li>
        <li>
          <span>Cupo</span> {textoCupo(actividad.cupoDisponible, actividad.cupoMaximo)}
        </li>
      </ul>

      <div className="tarjeta-pie">
        <p className="precio">
          {formatearPesos(actividad.cuotaMensual)} <small>/ mes</small>
        </p>
        <button
          type="button"
          className="boton"
          disabled={sinCupo}
          onClick={() => onInscribir(actividad)}
        >
          Inscribirme
        </button>
      </div>
    </article>
  );
}

TarjetaActividad.propTypes = {
  actividad: actividadPropType.isRequired,
  onInscribir: PropTypes.func.isRequired,
};

export default TarjetaActividad;
