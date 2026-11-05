import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Encabezado from './componentes/Encabezado';
import Pie from './componentes/Pie';
import Ingreso from './paginas/Ingreso';
import Catalogo from './paginas/Catalogo';
import Detalle from './paginas/Detalle';
import MisActividades from './paginas/MisActividades';
import NoEncontrada from './paginas/NoEncontrada';
import './estilos/estilos.css';

// App arma la estructura general: encabezado, contenido según la ruta y pie.
// BrowserRouter lee la URL del navegador y Routes elige qué página mostrar.
// "future" activa el comportamiento de React Router 7 y evita avisos en la consola.
function App() {
  return (
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <div className="app">
        <Encabezado />
        <main className="contenido">
          <Routes>
            <Route path="/" element={<Ingreso />} />
            <Route path="/actividades" element={<Catalogo />} />
            <Route path="/actividades/:id" element={<Detalle />} />
            <Route path="/mis-actividades" element={<MisActividades />} />
            <Route path="*" element={<NoEncontrada />} />
          </Routes>
        </main>
        <Pie />
      </div>
    </BrowserRouter>
  );
}

export default App;
