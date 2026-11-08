package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static ar.edu.siglo21.socioclub.exception.ReglaNegocioException.Codigo.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/** Lógica del servicio con fecha fija; no acredita bloqueos ni persistencia MySQL. */
@ExtendWith(MockitoExtension.class)
class InscripcionServiceTests {
    @Mock SocioRepository socios;
    @Mock ActividadRepository actividades;
    @Mock InscripcionRepository inscripciones;
    private InscripcionService servicio;
    private Socio socio;
    private Actividad actividad;
    private final LocalDate hoy = LocalDate.of(2026, 11, 8);

    @BeforeEach
    void preparar() {
        servicio = new InscripcionService(socios, actividades, inscripciones,
                Clock.fixed(hoy.atStartOfDay(ZoneOffset.UTC).toInstant(), ZoneOffset.UTC));
        socio = new Socio("99000001", "Prueba", "Servicio", hoy.minusYears(20),
                "prueba@example.invalid", "PRUEBA", EstadoCuota.AL_DIA);
        socio.setId(1L);
        actividad = new Actividad("Ensayo", "Prueba", "Docente", "Lun", "10:00",
                18, 65, 15, BigDecimal.TEN);
        actividad.setId(2L);
        // Los rechazos tempranos no llegan a todas las consultas.
        lenient().when(socios.buscarParaInscribir(1L)).thenReturn(Optional.of(socio));
        lenient().when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(actividad));
        lenient().when(inscripciones.save(any(Inscripcion.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void inscripcionValidaConCupoDisponible() {
        Inscripcion resultado = servicio.inscribir(1L, 2L);
        assertSame(socio, resultado.getSocio());
        assertSame(actividad, resultado.getActividad());
        assertEquals(hoy, resultado.getFechaInscripcion());
        assertEquals(EstadoInscripcion.ACTIVA, resultado.getEstado());
        var orden = inOrder(socios, actividades, inscripciones);
        orden.verify(socios).buscarParaInscribir(1L);
        orden.verify(actividades).buscarParaInscribir(2L);
        orden.verify(inscripciones).existsBySocioIdAndActividadIdAndEstado(1L, 2L, EstadoInscripcion.ACTIVA);
        orden.verify(inscripciones).countBySocioIdAndEstado(1L, EstadoInscripcion.ACTIVA);
        orden.verify(inscripciones).contarActivasDeActividad(2L);
        orden.verify(inscripciones).save(resultado);
    }

    @Test
    void aceptaUltimoCupo() {
        when(inscripciones.contarActivasDeActividad(2L)).thenReturn(14L);
        assertEquals(EstadoInscripcion.ACTIVA, servicio.inscribir(1L, 2L).getEstado());
    }

    @ParameterizedTest
    @ValueSource(longs = {15, 16})
    void rechazaCupoCompletoOYaExcedido(long activas) {
        when(inscripciones.contarActivasDeActividad(2L)).thenReturn(activas);
        rechaza(SIN_CUPO);
    }

    @Test
    void rechazaActivaDuplicada() {
        when(inscripciones.existsBySocioIdAndActividadIdAndEstado(1L, 2L, EstadoInscripcion.ACTIVA))
                .thenReturn(true);
        rechaza(INSCRIPCION_DUPLICADA);
    }

    @Test
    void reinscripcionNoReactivaNiModificaLaBaja() {
        Inscripcion baja = new Inscripcion(socio, actividad, hoy.minusDays(10), EstadoInscripcion.BAJA);
        baja.setId(40L);
        socio.getInscripciones().add(baja);
        Inscripcion alta = servicio.inscribir(1L, 2L);
        assertNotSame(baja, alta);
        assertNull(alta.getId());
        assertEquals(EstadoInscripcion.BAJA, baja.getEstado());
        verify(inscripciones).countBySocioIdAndEstado(1L, EstadoInscripcion.ACTIVA);
    }

    @Test
    void rechazaUnDiaAntesDelCumpleaniosMinimo() {
        socio.setFechaNacimiento(hoy.minusYears(18).plusDays(1));
        rechaza(EDAD_FUERA_DE_RANGO);
    }

    @Test
    void aceptaElDiaDelCumpleaniosMinimo() {
        socio.setFechaNacimiento(hoy.minusYears(18));
        assertNotNull(servicio.inscribir(1L, 2L));
    }

    @Test
    void aceptaDespuesDelCumpleaniosMinimo() {
        socio.setFechaNacimiento(hoy.minusYears(18).minusDays(1));
        assertNotNull(servicio.inscribir(1L, 2L));
    }

    @Test
    void aceptaEdadMaximaInclusiva() {
        socio.setFechaNacimiento(hoy.minusYears(66).plusDays(1));
        assertNotNull(servicio.inscribir(1L, 2L));
    }

    @Test
    void rechazaCumpleaniosQueSuperaEdadMaxima() {
        socio.setFechaNacimiento(hoy.minusYears(66));
        rechaza(EDAD_FUERA_DE_RANGO);
    }

    @Test
    void cumpleaniosDeFebreroBisiestoUsaAniosCompletos() {
        servicio = new InscripcionService(socios, actividades, inscripciones,
                Clock.fixed(Instant.parse("2026-02-28T12:00:00Z"), ZoneOffset.UTC));
        socio.setFechaNacimiento(LocalDate.of(2008, 2, 29));
        rechaza(EDAD_FUERA_DE_RANGO); // Period cumple 18 el 1 de marzo en un año no bisiesto.
        servicio = new InscripcionService(socios, actividades, inscripciones,
                Clock.fixed(Instant.parse("2026-03-01T12:00:00Z"), ZoneOffset.UTC));
        assertNotNull(servicio.inscribir(1L, 2L));
    }

    @Test
    void rechazaSocioMoroso() {
        socio.setEstadoCuota(EstadoCuota.MOROSO);
        rechaza(SOCIO_MOROSO);
    }

    @Test
    void aceptaTerceraActividadContandoSoloActivas() {
        when(inscripciones.countBySocioIdAndEstado(1L, EstadoInscripcion.ACTIVA)).thenReturn(2L);
        assertNotNull(servicio.inscribir(1L, 2L));
    }

    @ParameterizedTest
    @ValueSource(longs = {3, 4})
    void rechazaCuartaActividadOConteoYaExcedido(long activas) {
        when(inscripciones.countBySocioIdAndEstado(1L, EstadoInscripcion.ACTIVA)).thenReturn(activas);
        rechaza(MAXIMO_ACTIVIDADES);
    }

    @Test
    void rechazaSocioInexistenteSinGuardar() {
        when(socios.buscarParaInscribir(1L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.inscribir(1L, 2L));
        verifyNoInteractions(inscripciones);
        verify(actividades, never()).buscarParaInscribir(anyLong());
    }

    @Test
    void rechazaActividadInexistenteSinGuardar() {
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.inscribir(1L, 2L));
        verifyNoInteractions(inscripciones);
    }

    @Test
    void idsObligatoriosAntesDeConsultar() {
        assertThrows(NullPointerException.class, () -> servicio.inscribir(null, 2L));
        assertThrows(NullPointerException.class, () -> servicio.inscribir(1L, null));
        verifyNoInteractions(socios, actividades, inscripciones);
    }

    private void rechaza(ReglaNegocioException.Codigo codigo) {
        ReglaNegocioException error = assertThrows(ReglaNegocioException.class,
                () -> servicio.inscribir(1L, 2L));
        assertEquals(codigo, error.getCodigo());
        assertFalse(error.getMessage().isBlank());
        verify(inscripciones, never()).save(any());
    }
}
