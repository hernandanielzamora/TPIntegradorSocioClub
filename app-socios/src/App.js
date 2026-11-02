import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Encabezado from './componentes/Encabezado';
import Pie from './componentes/Pie';
import Inicio from './paginas/Inicio';
import './estilos/estilos.css';

function App() {
  return (
    <BrowserRouter>
      <Encabezado />
      <main>
        <Routes>
          <Route path="/" element={<Inicio />} />
        </Routes>
      </main>
      <Pie />
    </BrowserRouter>
  );
}

export default App;
