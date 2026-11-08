package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecretariaServiceTests {
    @Mock SocioRepository socios;
    @Mock ActividadRepository actividades;
    @Mock InscripcionRepository inscripciones;
    private SocioService socioService;
    private ActividadService actividadService;
    private InscripcionService inscripcionService;

    @BeforeEach void preparar() {
        socioService = new SocioService(socios, actividades, inscripciones);
        actividadService = new ActividadService(actividades, inscripciones);
        inscripcionService = new InscripcionService(socios, actividades, inscripciones, Clock.systemDefaultZone());
    }

    @Test void dniDuplicadoNoGuarda() {
        Socio existente = socio(); existente.setId(1L);
        when(socios.findByDni("99000001")).thenReturn(Optional.of(existente));
        assertThrows(ValidacionSecretariaException.class,
                () -> socioService.guardar(null, SocioFormulario.desde(existente)));
        verify(socios, never()).saveAndFlush(any());
    }

    @Test void editarMismoDniConBloqueo() {
        Socio existente = socio(); existente.setId(1L);
        when(socios.buscarParaInscribir(1L)).thenReturn(Optional.of(existente));
        when(socios.findByDni("99000001")).thenReturn(Optional.of(existente));
        socioService.guardar(1L, SocioFormulario.desde(existente));
        verify(socios).saveAndFlush(existente);
    }

    @Test void altaSocioSoloCopiaCamposPermitidos() {
        socioService.guardar(null, SocioFormulario.desde(socio()));
        verify(socios).saveAndFlush(argThat(s -> s.getId() == null && s.getDni().equals("99000001")
                && s.getEstadoCuota() == EstadoCuota.AL_DIA && s.getInscripciones().isEmpty()));
    }

    @Test void reducirCupoPorDebajoDeActivasNoGuarda() {
        Actividad a = actividad(); a.setId(2L);
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(a));
        when(inscripciones.contarActivasDeActividad(2L)).thenReturn(2L);
        assertThrows(ValidacionSecretariaException.class,
                () -> actividadService.guardar(2L, ActividadFormulario.desde(a)));
        verify(actividades, never()).saveAndFlush(any());
    }

    @Test void cupoIgualAActivasEsValido() {
        Actividad a = actividad(); a.setId(2L);
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(a));
        when(inscripciones.contarActivasDeActividad(2L)).thenReturn(1L);
        actividadService.guardar(2L, ActividadFormulario.desde(a));
        verify(actividades).saveAndFlush(a);
    }

    @Test void noEliminaActividadConActivas() {
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(actividad()));
        when(inscripciones.contarActivasDeActividad(2L)).thenReturn(1L);
        assertThrows(ValidacionSecretariaException.class, () -> actividadService.eliminar(2L));
        verify(actividades, never()).delete(any());
    }

    @Test void noEliminaHistorialDeBajas() {
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(actividad()));
        when(inscripciones.existsByActividadId(2L)).thenReturn(true);
        assertThrows(ValidacionSecretariaException.class, () -> actividadService.eliminar(2L));
        verify(actividades, never()).delete(any());
    }

    @Test void eliminaSoloActividadSinHistorial() {
        Actividad a = actividad();
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(a));
        actividadService.eliminar(2L);
        verify(actividades).delete(a);
        verify(actividades).flush();
    }

    @Test void bajaConservaVinculoYFechaAunqueSocioSeaMoroso() {
        Socio s = socio(); s.setId(1L); s.setEstadoCuota(EstadoCuota.MOROSO);
        Actividad a = actividad(); a.setId(2L);
        LocalDate fecha = LocalDate.of(2026, 1, 1);
        Inscripcion i = new Inscripcion(s, a, fecha, EstadoInscripcion.ACTIVA);
        i.setId(3L);
        when(inscripciones.findById(3L)).thenReturn(Optional.of(i));
        when(socios.buscarParaInscribir(1L)).thenReturn(Optional.of(s));
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(a));
        inscripcionService.darDeBaja(3L);
        inscripcionService.darDeBaja(3L);
        assertEquals(EstadoInscripcion.BAJA, i.getEstado());
        assertSame(s, i.getSocio()); assertSame(a, i.getActividad());
        assertEquals(fecha, i.getFechaInscripcion());
        var orden = inOrder(socios, actividades);
        orden.verify(socios).buscarParaInscribir(1L);
        orden.verify(actividades).buscarParaInscribir(2L);
        verify(inscripciones, never()).delete(any());
    }

    @Test void bajaInexistenteNoBloqueaPadres() {
        assertThrows(RecursoNoEncontradoException.class, () -> inscripcionService.darDeBaja(99L));
        verifyNoInteractions(socios, actividades);
    }

    private Socio socio() {
        return new Socio("99000001", "Ana", "Prueba", LocalDate.of(2000, 1, 1),
                "ana@example.invalid", "PRUEBA", EstadoCuota.AL_DIA);
    }
    private Actividad actividad() {
        return new Actividad("Yoga", "Bienestar", "Ana", "Lunes", "9:00", 18, 99, 1, BigDecimal.TEN);
    }
}
