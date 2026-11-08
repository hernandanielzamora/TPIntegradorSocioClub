import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import App from '../App';
import Catalogo from '../paginas/Catalogo';
import Detalle from '../paginas/Detalle';
import MisActividades from '../paginas/MisActividades';
import { SocioContext } from '../contexto/SocioContext';
import api from '../servicios/api';
import { actividad, socio, inscripcion } from './fixturesApi';

jest.mock('axios', () => ({ create: jest.fn(() => ({ get: jest.fn(), post: jest.fn(), put: jest.fn() })) }));
beforeEach(() => {
  jest.resetAllMocks();
  window.sessionStorage.clear();
  window.history.replaceState({}, '', '/');
  window.scrollTo = jest.fn();
  window.confirm = jest.fn(() => true);
});
function pagina(componente, ruta='/') {
  return render(<SocioContext.Provider value={{ socio }}><MemoryRouter initialEntries={[ruta]} future={{v7_startTransition:true,v7_relativeSplatPath:true}}>{componente}</MemoryRouter></SocioContext.Provider>);
}
function enviarDni(dni=socio.dni) {
  fireEvent.change(screen.getByLabelText('DNI'), { target:{ value:dni } });
  fireEvent.click(screen.getByRole('button', { name:'Ingresar' }));
}
test('DNI inválido no envía solicitudes', () => {
  render(<App/>); enviarDni('123');
  expect(screen.getByText(/Ingresá un DNI de 7 u 8/)).toBeInTheDocument();
  expect(api.get).not.toHaveBeenCalled();
});
test('DNI no encontrado muestra mensaje claro', async () => {
  api.get.mockRejectedValue({ response:{status:404} });
  render(<App/>); enviarDni();
  expect(await screen.findByRole('alert')).toHaveTextContent('No encontramos un socio con ese DNI.');
});
test('ingreso real navega al catálogo y guarda solo DNI; salir lo retira', async () => {
  api.get.mockImplementation((url) => Promise.resolve({data:url.includes('/dni/') ? socio : [actividad]}));
  render(<App/>); enviarDni();
  expect(await screen.findByText(actividad.nombre)).toBeInTheDocument();
  expect(window.sessionStorage.getItem('miclub-socio-dni')).toBe(socio.dni);
  expect(window.sessionStorage.getItem('miclub-socio')).toBeNull();
  fireEvent.click(screen.getByRole('button',{name:'Salir'}));
  expect(await screen.findByRole('button',{name:'Ingresar'})).toBeInTheDocument();
  await waitFor(() => expect(window.sessionStorage.getItem('miclub-socio-dni')).toBeNull());
});
test('sesión recargada vuelve a consultar al servidor, sin confiar en objetos guardados', async () => {
  sessionStorage.setItem('miclub-socio-dni',socio.dni);
  sessionStorage.setItem('miclub-socio',JSON.stringify({id:999,nombre:'Obsoleto'}));
  api.get.mockImplementation((url) => Promise.resolve({data:url.includes('/dni/') ? socio : []}));
  render(<App/>);
  expect(await screen.findByText(/No hay actividades/)).toBeInTheDocument();
  expect(api.get).toHaveBeenCalledWith(`/socios/dni/${socio.dni}`);
  expect(sessionStorage.getItem('miclub-socio')).toBeNull();
});
test('respuesta inesperada en ingreso no inicia sesión', async () => {
  api.get.mockResolvedValue({data:'<html>error</html>'});
  render(<App/>); enviarDni();
  expect(await screen.findByRole('alert')).toHaveTextContent(/datos inesperados/);
  expect(sessionStorage.getItem('miclub-socio-dni')).toBeNull();
});
test('catálogo vacío muestra estado vacío', async () => {
  api.get.mockResolvedValue({data:[]}); pagina(<Catalogo/>);
  expect(await screen.findByText(/No hay actividades/)).toBeInTheDocument();
});
test('servidor desconectado muestra error y no inventa catálogo', async () => {
  api.get.mockRejectedValue(new Error('Network Error')); pagina(<Catalogo/>);
  expect(await screen.findByRole('alert')).toHaveTextContent(/No se pudo conectar/);
});
test('catálogo cargado filtra sin perder las disciplinas disponibles', async () => {
  api.get.mockResolvedValue({data:[actividad,{...actividad,id:2,nombre:'Natación',disciplina:'Natación'}]});
  pagina(<Catalogo/>);
  await screen.findByText(actividad.nombre);
  fireEvent.change(screen.getByLabelText('Disciplina'),{target:{value:'Natación'}});
  expect(screen.queryByText(actividad.nombre)).not.toBeInTheDocument();
  expect(screen.getByRole('link',{name:'Natación'})).toBeInTheDocument();
});
test('alta real refresca cupos y bloquea doble clic mientras espera', async () => {
  let confirmar;
  api.get.mockResolvedValueOnce({data:[actividad]}).mockResolvedValueOnce({data:[{...actividad,cupoDisponible:3}]});
  api.post.mockImplementation(() => new Promise((resolve) => { confirmar=resolve; }));
  pagina(<Catalogo/>); await screen.findByText(actividad.nombre);
  const boton=screen.getByRole('button',{name:'Inscribirme'});
  fireEvent.click(boton); fireEvent.click(boton);
  expect(api.post).toHaveBeenCalledTimes(1); expect(boton).toBeDisabled();
  confirmar({data:inscripcion});
  expect(await screen.findByText(/Quedan 3 lugares/)).toBeInTheDocument();
  expect(screen.getByRole('status')).toHaveTextContent(/Te inscribiste/);
});
test.each([['RN1','cupo'],['RN2','Ya estás inscripto'],['RN3','edad'],['RN4','regularizar'],['RN5','máximo']])('conflicto %s usa la regla del backend', async (regla,mensaje) => {
  api.get.mockResolvedValue({data:[actividad]});
  api.post.mockRejectedValue({response:{status:409,data:{regla,error:'SQL no mostrar'}}});
  pagina(<Catalogo/>); await screen.findByText(actividad.nombre);
  fireEvent.click(screen.getByRole('button',{name:'Inscribirme'}));
  expect(await screen.findByRole('alert')).toHaveTextContent(mensaje);
  expect(screen.getByRole('alert')).not.toHaveTextContent('SQL');
});
test('detalle consume el identificador de la ruta', async () => {
  api.get.mockResolvedValue({data:actividad});
  pagina(<Routes><Route path='/actividades/:id' element={<Detalle/>}/></Routes>,'/actividades/1');
  expect(await screen.findByRole('heading',{name:actividad.nombre})).toBeInTheDocument();
  expect(api.get).toHaveBeenCalledWith('/actividades/1');
});
test('cancelar confirmación de baja no envía PUT', async () => {
  api.get.mockResolvedValue({data:[inscripcion]}); window.confirm.mockReturnValue(false);
  pagina(<MisActividades/>); await screen.findByRole('button',{name:'Darme de baja'});
  fireEvent.click(screen.getByRole('button',{name:'Darme de baja'}));
  expect(window.confirm).toHaveBeenCalled(); expect(api.put).not.toHaveBeenCalled();
});
test('baja confirmada refresca inscripciones y cuota a cero', async () => {
  api.get.mockResolvedValueOnce({data:[inscripcion]}).mockResolvedValueOnce({data:[]});
  api.put.mockResolvedValue({data:{...inscripcion,estado:'BAJA'}});
  pagina(<MisActividades/>); await screen.findByRole('button',{name:'Darme de baja'});
  fireEvent.click(screen.getByRole('button',{name:'Darme de baja'}));
  expect(await screen.findByText(/Todavía no estás inscripto/)).toBeInTheDocument();
  expect(api.put).toHaveBeenCalledWith('/inscripciones/20/baja');
  expect(screen.getByText('Total estimado').nextSibling).toHaveTextContent(/0,00/);
});
test('error de baja conserva la inscripción visible', async () => {
  api.get.mockResolvedValue({data:[inscripcion]}); api.put.mockRejectedValue({response:{status:503}});
  pagina(<MisActividades/>); await screen.findByRole('button',{name:'Darme de baja'});
  fireEvent.click(screen.getByRole('button',{name:'Darme de baja'}));
  expect(await screen.findByRole('alert')).toHaveTextContent(/servidor no pudo/);
  expect(screen.getByRole('button',{name:'Darme de baja'})).toBeEnabled();
});
