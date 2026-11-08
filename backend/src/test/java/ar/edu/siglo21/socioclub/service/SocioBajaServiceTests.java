package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.SocioFormulario;
import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocioBajaServiceTests {
    @Mock SocioRepository socios;
    @Mock ActividadRepository actividades;
    @Mock InscripcionRepository inscripciones;
    private SocioService servicio;
    private Socio socio;
    @BeforeEach void preparar() {
        servicio = new SocioService(socios, actividades, inscripciones);
        socio = new Socio("99000001", "Ana", "Prueba", LocalDate.of(2000, 1, 1),
                "ana@example.invalid", "PRUEBA", EstadoCuota.MOROSO);
        socio.setId(1L);
    }
    private void socioExiste() { when(socios.buscarParaInscribir(1L)).thenReturn(Optional.of(socio)); }
    private Inscripcion vinculo(long actividadId, EstadoInscripcion estado) {
        Actividad actividad = new Actividad(); actividad.setId(actividadId);
        return new Inscripcion(socio, actividad, LocalDate.of(2026, 1, 1), estado);
    }
    @Test void socioNuevoEsActivoIndependientementeDeCuota() {
        assertEquals(EstadoSocio.ACTIVO, socio.getEstadoSocio());
        assertEquals(EstadoCuota.MOROSO, socio.getEstadoCuota());
    }
    @Test void bajaActivasConservaDatosYBloqueaActividadesEnOrden() {
        socioExiste();
        Inscripcion primera = vinculo(3, EstadoInscripcion.ACTIVA);
        Inscripcion segunda = vinculo(2, EstadoInscripcion.ACTIVA);
        Inscripcion historica = vinculo(4, EstadoInscripcion.BAJA);
        socio.getInscripciones().addAll(List.of(primera, segunda, historica));
        when(inscripciones.findBySocioIdAndEstadoOrderByIdDesc(1L, EstadoInscripcion.ACTIVA))
                .thenReturn(List.of(primera, segunda));
        when(actividades.buscarParaInscribir(2L)).thenReturn(Optional.of(segunda.getActividad()));
        when(actividades.buscarParaInscribir(3L)).thenReturn(Optional.of(primera.getActividad()));
        servicio.darDeBaja(1L);
        assertEquals(EstadoSocio.INACTIVO, socio.getEstadoSocio());
        assertEquals(EstadoCuota.MOROSO, socio.getEstadoCuota());
        assertEquals("99000001", socio.getDni()); assertEquals("ana@example.invalid", socio.getEmail());
        for (Inscripcion i : List.of(primera, segunda, historica)) {
            assertEquals(EstadoInscripcion.BAJA, i.getEstado());
            assertEquals(LocalDate.of(2026, 1, 1), i.getFechaInscripcion());
            assertSame(socio, i.getSocio());
        }
        var orden = inOrder(socios, actividades, inscripciones);
        orden.verify(socios).buscarParaInscribir(1L);
        orden.verify(inscripciones).findBySocioIdAndEstadoOrderByIdDesc(1L, EstadoInscripcion.ACTIVA);
        orden.verify(actividades).buscarParaInscribir(2L);
        orden.verify(actividades).buscarParaInscribir(3L);
        verify(inscripciones, never()).delete(any()); verify(socios, never()).delete(any());
    }
    @Test void bajaSinInscripcionesYRepetidaEsIdempotente() {
        socioExiste();
        when(inscripciones.findBySocioIdAndEstadoOrderByIdDesc(1L, EstadoInscripcion.ACTIVA)).thenReturn(List.of());
        servicio.darDeBaja(1L); servicio.darDeBaja(1L);
        assertEquals(EstadoSocio.INACTIVO, socio.getEstadoSocio());
        verifyNoInteractions(actividades);
    }
    @Test void inexistenteNoConsultaNiModificaInscripciones() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.darDeBaja(99L));
        verifyNoInteractions(actividades, inscripciones);
    }
    @Test void faltaActividadSeDetectaAntesDeCambiarEstados() {
        socioExiste(); Inscripcion i = vinculo(2, EstadoInscripcion.ACTIVA);
        when(inscripciones.findBySocioIdAndEstadoOrderByIdDesc(1L, EstadoInscripcion.ACTIVA)).thenReturn(List.of(i));
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.darDeBaja(1L));
        assertEquals(EstadoSocio.ACTIVO, socio.getEstadoSocio());
        assertEquals(EstadoInscripcion.ACTIVA, i.getEstado());
        verify(inscripciones, never()).flush();
    }
    @Test void falloDeFlushSePropagaParaRollbackTransaccional() {
        socioExiste();
        doThrow(new org.springframework.dao.DataAccessResourceFailureException("fallo simulado"))
                .when(inscripciones).flush();
        assertThrows(org.springframework.dao.DataAccessException.class, () -> servicio.darDeBaja(1L));
        // Un mock no revierte objetos: el rollback real se prueba exclusivamente con MySQL.
    }
    @Test void editarDatosNoReactivaAlSocioInactivo() {
        socioExiste(); socio.setEstadoSocio(EstadoSocio.INACTIVO);
        when(socios.findByDni(socio.getDni())).thenReturn(Optional.of(socio));
        servicio.guardar(1L, SocioFormulario.desde(socio));
        assertEquals(EstadoSocio.INACTIVO, socio.getEstadoSocio());
    }
    @Test void selectorDeAltaExcluyeInactivosSinBorrarlos() {
        Socio otro = new Socio(); otro.setId(2L); otro.setEstadoSocio(EstadoSocio.INACTIVO);
        when(socios.findAll(any(Sort.class))).thenReturn(List.of(socio, otro));
        assertEquals(List.of(1L), servicio.opciones().stream().map(o -> o.id()).toList());
    }
}
