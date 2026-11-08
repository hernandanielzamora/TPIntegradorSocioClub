import PropTypes from 'prop-types';
import TarjetaActividad from './TarjetaActividad';
import { actividadPropType } from './tiposActividad';

// Recibe el arreglo de actividades y dibuja una TarjetaActividad por cada una.
// La prop "key" le permite a React identificar cada tarjeta al redibujar la lista.
function ListaActividades({ actividades, onInscribir, bloqueado = false }) {
  if (actividades.length === 0) {
    return <p className="mensaje mensaje-info">No hay actividades para el filtro elegido.</p>;
  }

  return (
    <div className="grilla-actividades">
      {actividades.map((actividad) => (
        <TarjetaActividad key={actividad.id} actividad={actividad} onInscribir={onInscribir} bloqueado={bloqueado} />
      ))}
    </div>
  );
}

ListaActividades.propTypes = {
  actividades: PropTypes.arrayOf(actividadPropType).isRequired,
  onInscribir: PropTypes.func.isRequired,
  bloqueado: PropTypes.bool,
};

export default ListaActividades;
