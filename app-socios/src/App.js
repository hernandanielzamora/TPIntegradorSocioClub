import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Encabezado from './componentes/Encabezado';
import Pie from './componentes/Pie';
import RutaProtegida from './componentes/RutaProtegida';
import { SocioProvider } from './contexto/SocioContext';
import Ingreso from './paginas/Ingreso';
import Catalogo from './paginas/Catalogo';
import Detalle from './paginas/Detalle';
import MisActividades from './paginas/MisActividades';
import NoEncontrada from './paginas/NoEncontrada';
import './estilos/estilos.css';

// App arma la estructura general: encabezado, contenido según la ruta y pie.
// SocioProvider comparte el socio que ingresó con todas las pantallas.
// BrowserRouter lee la URL del navegador y Routes elige qué página mostrar.
// "future" activa el comportamiento de React Router 7 y evita avisos en la consola.
function App() {
  return (
    <SocioProvider>
      <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <div className="app">
          <Encabezado />
          <main className="contenido">
            <Routes>
              <Route path="/" element={<Ingreso />} />
              <Route
                path="/actividades"
                element={
                  <RutaProtegida>
                    <Catalogo />
                  </RutaProtegida>
                }
              />
              <Route
                path="/actividades/:id"
                element={
                  <RutaProtegida>
                    <Detalle />
                  </RutaProtegida>
                }
              />
              <Route
                path="/mis-actividades"
                element={
                  <RutaProtegida>
                    <MisActividades />
                  </RutaProtegida>
                }
              />
              <Route path="*" element={<NoEncontrada />} />
            </Routes>
          </main>
          <Pie />
        </div>
      </BrowserRouter>
    </SocioProvider>
  );
}

export default App;
