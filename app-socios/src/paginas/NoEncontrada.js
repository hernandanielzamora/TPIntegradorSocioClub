import { Link } from 'react-router-dom';

// Ruta "*": se muestra cuando la URL no coincide con ninguna ruta.
function NoEncontrada() {
  return (
    <section className="pagina pagina-centrada">
      <h1>Página no encontrada</h1>
      <p className="texto-suave">La dirección que escribiste no existe en Mi Club.</p>
      <Link to="/" className="boton">
        Ir al inicio
      </Link>
    </section>
  );
}

export default NoEncontrada;
