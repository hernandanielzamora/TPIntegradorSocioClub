// Pie común a todas las pantallas.
function Pie() {
  const anioActual = new Date().getFullYear();

  return (
    <footer className="pie">
      <div className="pie-contenido">
        <p>
          <strong>Club Social y Deportivo Los Algarrobos</strong> · Zona norte, Córdoba
        </p>
        <p>SocioClub · Trabajo Práctico Integrador · Universidad Siglo 21 · {anioActual}</p>
      </div>
    </footer>
  );
}

export default Pie;
