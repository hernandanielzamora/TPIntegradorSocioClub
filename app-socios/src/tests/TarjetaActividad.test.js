import { fireEvent, render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import TarjetaActividad from '../componentes/TarjetaActividad';

// Actividad de prueba con los datos de Vóley mixto del enunciado.
const voley = {
  id: 2,
  nombre: 'Vóley mixto',
  disciplina: 'Vóley',
  dias: 'Mar y Jue',
  horario: '19:00',
  edadMinima: 13,
  edadMaxima: 60,
  cupoMaximo: 24,
  cupoDisponible: 9,
  cuotaMensual: 20000,
  profesor: 'Laura Ceballos',
};

// La tarjeta tiene un <Link> de React Router, por eso se dibuja dentro de un
// MemoryRouter (un router que funciona en memoria, sin navegador).
// "future" activa el comportamiento de la versión 7 y evita avisos en la consola.
function dibujarTarjeta(actividad, onInscribir = jest.fn()) {
  render(
    <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <TarjetaActividad actividad={actividad} onInscribir={onInscribir} />
    </MemoryRouter>
  );
  return onInscribir;
}

describe('TarjetaActividad', () => {
  test('muestra el nombre de la actividad', () => {
    dibujarTarjeta(voley);
    expect(screen.getByText('Vóley mixto')).toBeInTheDocument();
  });

  test('con cupo 0 muestra «Sin cupo» y el botón está deshabilitado', () => {
    const sinLugares = { ...voley, cupoDisponible: 0 };
    const onInscribir = dibujarTarjeta(sinLugares);

    expect(screen.getByText('Sin cupo')).toBeInTheDocument();

    const boton = screen.getByRole('button', { name: 'Inscribirme' });
    expect(boton).toBeDisabled();

    // Un botón deshabilitado no ejecuta su onClick.
    fireEvent.click(boton);
    expect(onInscribir).not.toHaveBeenCalled();
  });

  test('con cupo disponible el botón está habilitado y avisa al hacer clic', () => {
    const onInscribir = dibujarTarjeta(voley);

    expect(screen.queryByText('Sin cupo')).not.toBeInTheDocument();

    const boton = screen.getByRole('button', { name: 'Inscribirme' });
    expect(boton).toBeEnabled();

    fireEvent.click(boton);
    expect(onInscribir).toHaveBeenCalledTimes(1);
    expect(onInscribir).toHaveBeenCalledWith(voley);
  });

  test('con cupo 0 la tarjeta tiene la clase «sin-cupo» (se ve en gris)', () => {
    dibujarTarjeta({ ...voley, cupoDisponible: 0 });
    expect(screen.getByRole('article')).toHaveClass('sin-cupo');
  });
});
