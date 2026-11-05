import { Link, useParams } from 'react-router-dom';

// Ruta "/actividades/:id": detalle de una actividad.
// useParams lee la parte variable de la URL; en /actividades/3, id vale "3".
function Detalle() {
  const { id } = useParams();

  return (
    <section className="pagina">
      <Link to="/actividades" className="enlace-volver">
        ← Volver a actividades
      </Link>
      <h1>Actividad n.º {id}</h1>
      <p className="texto-suave">Acá se va a mostrar el detalle con el botón «Inscribirme».</p>
    </section>
  );
}

export default Detalle;
