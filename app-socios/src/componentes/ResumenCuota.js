import PropTypes from 'prop-types';
import { calcularCuotaMensual, obtenerPorcentajeDescuento } from '../utils/calcularCuotaMensual';
import { formatearPesos } from '../utils/formatearPesos';

// Muestra la cuota mensual estimada del socio (RN6).
// Recibe por props las actividades activas y usa la función pura calcularCuotaMensual,
// la misma que se prueba con Jest.
function ResumenCuota({ actividades }) {
  const subtotal = actividades.reduce((suma, actividad) => suma + actividad.cuotaMensual, 0);
  const porcentaje = obtenerPorcentajeDescuento(actividades.length);
  const total = calcularCuotaMensual(actividades);
  const descuento = subtotal - total;

  let sugerencia = '';
  if (actividades.length === 1) {
    sugerencia = 'Si sumás una segunda actividad tenés 10 % de bonificación.';
  } else if (actividades.length === 2) {
    sugerencia = 'Con una tercera actividad la bonificación sube al 15 %.';
  }

  return (
    <aside className="resumen-cuota" aria-labelledby="titulo-resumen">
      <h2 id="titulo-resumen">Tu cuota mensual</h2>

      <ul className="resumen-lineas">
        {actividades.map((actividad) => (
          <li key={actividad.id}>
            <span>{actividad.nombre}</span>
            <span>{formatearPesos(actividad.cuotaMensual)}</span>
          </li>
        ))}
      </ul>

      <dl className="resumen-totales">
        <div>
          <dt>Subtotal</dt>
          <dd>{formatearPesos(subtotal)}</dd>
        </div>
        <div className={porcentaje > 0 ? 'con-descuento' : ''}>
          <dt>Bonificación {porcentaje > 0 ? `(${porcentaje} %)` : ''}</dt>
          <dd>{porcentaje > 0 ? `− ${formatearPesos(descuento)}` : 'Sin bonificación'}</dd>
        </div>
        <div className="resumen-total">
          <dt>Total estimado</dt>
          <dd>{formatearPesos(total)}</dd>
        </div>
      </dl>

      {sugerencia && <p className="resumen-sugerencia">{sugerencia}</p>}
    </aside>
  );
}

ResumenCuota.propTypes = {
  actividades: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      nombre: PropTypes.string.isRequired,
      cuotaMensual: PropTypes.number.isRequired,
    })
  ).isRequired,
};

export default ResumenCuota;
