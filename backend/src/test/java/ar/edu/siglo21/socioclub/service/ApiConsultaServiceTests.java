package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.ActividadFormulario;
import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiConsultaServiceTests {
    @Mock SocioRepository socios;
    @Mock ActividadRepository actividades;
    @Mock InscripcionRepository inscripciones;
    @Mock ActividadService administracion;
    @Mock InscripcionService altas;
    @Mock SocioService gestionSocios;
    private ApiConsultaService api;
    @BeforeEach void preparar() { api = new ApiConsultaService(socios, actividades, inscripciones, administracion, altas, gestionSocios); }
    private Actividad actividad() {
        Actividad a = new Actividad("Yoga", "Bienestar", "Ana", "Lun", "9:00", 18, 99, 15, BigDecimal.TEN);
        a.setId(2L); return a;
    }
    private Inscripcion inscripcion(EstadoInscripcion estado) {
        Socio s = new Socio(); s.setId(1L);
        Inscripcion i = new Inscripcion(s, actividad(), LocalDate.of(2026, 11, 8), estado);
        i.setId(3L); return i;
    }
    @Test void filtraCatalogoYCupoDisponibleSinExponerEntidades() {
        when(actividades.findByDisciplina(eq("Bienestar"), any(Sort.class))).thenReturn(List.of(actividad()));
        when(inscripciones.contarActivasDeActividad(2L)).thenReturn(14L);
        var lista = api.actividades("Bienestar");
        assertEquals(1, lista.size()); assertEquals(1, lista.getFirst().cupoDisponible());
        assertEquals("Yoga", lista.getFirst().nombre());
        verify(actividades, never()).findAll(any(Sort.class));
    }
    @Test void actividadInexistente404AntesDeContar() {
        assertThrows(RecursoNoEncontradoException.class, () -> api.actividad(99L));
        verifyNoInteractions(inscripciones);
    }
    @Test void socioInexistenteNoSeConfundeConListaVacia() {
        assertThrows(RecursoNoEncontradoException.class, () -> api.inscripciones(99L));
        verifyNoInteractions(inscripciones);
    }
    @Test void consultaSoloSolicitaActivasYMapeaActividad() {
        when(socios.existsById(1L)).thenReturn(true);
        when(inscripciones.findBySocioIdAndEstadoOrderByIdDesc(1L, EstadoInscripcion.ACTIVA))
                .thenReturn(List.of(inscripcion(EstadoInscripcion.ACTIVA)));
        assertEquals("Yoga", api.inscripciones(1L).getFirst().actividad().nombre());
    }
    @Test void altaDelegaRNalServicioExistente() {
        when(altas.inscribir(1L, 2L)).thenReturn(inscripcion(EstadoInscripcion.ACTIVA));
        assertEquals(EstadoInscripcion.ACTIVA, api.inscribir(1L, 2L).estado());
        verify(altas).inscribir(1L, 2L);
        verify(inscripciones, never()).save(any());
    }
    @Test void bajaDelegaYLeeDTOActualizado() {
        when(inscripciones.findById(3L)).thenReturn(Optional.of(inscripcion(EstadoInscripcion.BAJA)));
        assertEquals(EstadoInscripcion.BAJA, api.baja(3L).estado());
        var orden = inOrder(altas, inscripciones);
        orden.verify(altas).darDeBaja(3L); orden.verify(inscripciones).findById(3L);
    }
    @Test void crearActividadReutilizaAdministracionYRecuperaId() {
        var formulario = ActividadFormulario.desde(actividad());
        when(administracion.guardar(null, formulario)).thenReturn(2L);
        when(actividades.findById(2L)).thenReturn(Optional.of(actividad()));
        assertEquals(2L, api.guardarActividad(null, formulario).id());
        verify(administracion).guardar(null, formulario);
    }
}
