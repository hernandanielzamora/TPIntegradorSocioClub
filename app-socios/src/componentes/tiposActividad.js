import PropTypes from 'prop-types';

// Forma que debe tener un objeto actividad. La usan los PropTypes de
// TarjetaActividad, ListaActividades y DetalleActividad. Si un componente
// recibe datos con otra forma, React muestra una advertencia en la consola.
export const actividadPropType = PropTypes.shape({
  id: PropTypes.number.isRequired,
  nombre: PropTypes.string.isRequired,
  disciplina: PropTypes.string.isRequired,
  dias: PropTypes.string.isRequired,
  horario: PropTypes.string.isRequired,
  edadMinima: PropTypes.number.isRequired,
  edadMaxima: PropTypes.number.isRequired,
  cupoMaximo: PropTypes.number.isRequired,
  cupoDisponible: PropTypes.number.isRequired,
  cuotaMensual: PropTypes.number.isRequired,
  profesor: PropTypes.string.isRequired,
});
