// Ruta "/": pantalla de ingreso.
// En el paso del contexto se agrega el formulario con el DNI.
function Ingreso() {
  return (
    <section className="pagina">
      <h1>Ingresá a Mi Club</h1>
      <p className="texto-suave">
        Desde acá vas a poder entrar con tu DNI para ver tus actividades, tu cuota y anotarte.
      </p>
    </section>
  );
}

export default Ingreso;
