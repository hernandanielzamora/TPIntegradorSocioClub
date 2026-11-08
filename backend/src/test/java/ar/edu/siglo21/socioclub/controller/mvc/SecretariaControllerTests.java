package ar.edu.siglo21.socioclub.controller.mvc;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.service.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Renderiza Thymeleaf con MockMvc y servicios simulados, sin tocar MySQL. */
@WebMvcTest(SecretariaController.class)
class SecretariaControllerTests {
    @Autowired MockMvc mvc;
    @MockitoBean SocioService socios;
    @MockitoBean ActividadService actividades;
    @MockitoBean InscripcionService inscripciones;
    @MockitoBean ConsultaSecretariaService consultas;

    @BeforeEach
    void preparar() {
        when(socios.opciones()).thenReturn(List.of(new Opcion(1L, "Ana Prueba · DNI 99000001")));
        when(actividades.opciones()).thenReturn(List.of(new Opcion(2L, "Yoga")));
    }

    @Test void panelConResumen() throws Exception {
        when(consultas.resumen()).thenReturn(new Resumen(21, 8, 18));
        mvc.perform(get("/secretaria")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Socios registrados")))
                .andExpect(content().string(containsString("21")));
    }

    @Test void sociosConDatosPersonalesBusquedaYPaginacion() throws Exception {
        var f = new SocioFormulario("99000001", "Ana", "Prueba", LocalDate.of(2000, 1, 1),
                "ana@example.invalid", "PRUEBA", EstadoCuota.MOROSO);
        when(socios.listar("pru", 1)).thenReturn(new PageImpl<>(List.of(new SocioVista(1L, f)),
                PageRequest.of(1, 10), 21));
        mvc.perform(get("/secretaria/socios").param("apellido", "pru").param("pagina", "1"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("ana@example.invalid")))
                .andExpect(content().string(containsString("MOROSO")))
                .andExpect(content().string(containsString("apellido=pru")));
        verify(socios).listar("pru", 1);
    }

    @Test void sociosVacios() throws Exception {
        when(socios.listar("", 0)).thenReturn(Page.empty());
        mvc.perform(get("/secretaria/socios")).andExpect(status().isOk())
                .andExpect(content().string(containsString("No se encontraron socios")));
    }

    @Test void actividadesConCupoYFiltro() throws Exception {
        var f = actividad();
        when(actividades.listar("Bienestar", 0)).thenReturn(new PageImpl<>(List.of(new ActividadVista(2L, f, 14, 1))));
        mvc.perform(get("/secretaria/actividades").param("disciplina", "Bienestar"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("1 disponibles")))
                .andExpect(content().string(containsString("14 activas / 15 plazas")));
    }

    @Test void formularioInscripcionConOpciones() throws Exception {
        mvc.perform(get("/secretaria/inscripciones/nueva")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Yoga")))
                .andExpect(content().string(containsString("99000001")));
    }

    @Test void inscripcionValidaPRG() throws Exception {
        mvc.perform(post("/secretaria/inscripciones").param("socioId", "1").param("actividadId", "2"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/secretaria/inscripciones"))
                .andExpect(flash().attribute("exito", "Inscripción realizada correctamente"));
        verify(inscripciones).inscribir(1L, 2L);
    }

    @ParameterizedTest
    @EnumSource(ReglaNegocioException.Codigo.class)
    void rechazosRN1aRN5ConservanFormulario(ReglaNegocioException.Codigo codigo) throws Exception {
        when(inscripciones.inscribir(1L, 2L)).thenThrow(new ReglaNegocioException(codigo, "Rechazo " + codigo));
        mvc.perform(post("/secretaria/inscripciones").param("socioId", "1").param("actividadId", "2"))
                .andExpect(status().isOk()).andExpect(view().name("secretaria/inscripcion-formulario"))
                .andExpect(model().attributeHasErrors("formulario"))
                .andExpect(content().string(containsString("Rechazo " + codigo)))
                .andExpect(content().string(containsString("Yoga")));
    }

    @Test void idsInvalidosNoLlamanServicio() throws Exception {
        mvc.perform(post("/secretaria/inscripciones").param("socioId", "-1").param("actividadId", "2"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("formulario", "socioId"));
        mvc.perform(post("/secretaria/inscripciones").param("socioId", "1").param("actividadId", "abc"))
                .andExpect(status().isOk()).andExpect(model().attributeHasFieldErrors("formulario", "actividadId"))
                .andExpect(content().string(containsString("Ingresá un número entero válido")));
        verifyNoInteractions(inscripciones);
    }

    @Test void referenciasNoEncontradasEnFormulario() throws Exception {
        when(inscripciones.inscribir(1L, 2L)).thenThrow(new RecursoNoEncontradoException("Socio", 1L));
        mvc.perform(post("/secretaria/inscripciones").param("socioId", "1").param("actividadId", "2"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Socio no encontrado")));
    }

    @Test void consultaEstadoYActividadConHistorial() throws Exception {
        when(consultas.listar(2L, EstadoInscripcion.BAJA, 0)).thenReturn(new PageImpl<>(List.of(
                new InscripcionVista(3L, "Ana", "Yoga", LocalDate.of(2026, 1, 1), EstadoInscripcion.BAJA))));
        mvc.perform(get("/secretaria/inscripciones").param("actividadId", "2").param("estado", "BAJA"))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Historial conservado")));
        verify(consultas).listar(2L, EstadoInscripcion.BAJA, 0);
    }

    @Test void bajaPostConConfirmacion() throws Exception {
        mvc.perform(post("/secretaria/inscripciones/3/baja")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/secretaria/inscripciones")).andExpect(flash().attributeExists("exito"));
        verify(inscripciones).darDeBaja(3L);
        mvc.perform(get("/secretaria/inscripciones/3/baja")).andExpect(status().isMethodNotAllowed());
    }

    @Test void formsNuevosYEdicion() throws Exception {
        mvc.perform(get("/secretaria/socios/nuevo")).andExpect(status().isOk());
        mvc.perform(get("/secretaria/actividades/nueva")).andExpect(status().isOk());
        when(actividades.obtener(2L)).thenReturn(actividad());
        mvc.perform(get("/secretaria/actividades/2/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Yoga")));
    }

    @Test void socioAltaYEdicionConPRG() throws Exception {
        for (String ruta : List.of("/secretaria/socios", "/secretaria/socios/1")) {
            mvc.perform(post(ruta).param("dni", "99000001").param("nombre", "Ana").param("apellido", "Prueba")
                    .param("fechaNacimiento", "2000-01-01").param("email", "ana@example.invalid")
                    .param("telefono", "PRUEBA").param("estadoCuota", "AL_DIA"))
                    .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/secretaria/socios"));
        }
        verify(socios).guardar(isNull(), any());
        verify(socios).guardar(eq(1L), any());
    }

    @Test void socioInvalidoNoGuarda() throws Exception {
        mvc.perform(post("/secretaria/socios").param("dni", "abc").param("email", "no-es-email"))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("formulario"));
        verify(socios, never()).guardar(any(), any());
    }

    @Test void actividadAltaYEdicionConPRG() throws Exception {
        for (String ruta : List.of("/secretaria/actividades", "/secretaria/actividades/2")) {
            mvc.perform(post(ruta).param("nombre", "Yoga").param("disciplina", "Bienestar").param("profesor", "Ana")
                    .param("dias", "Lunes").param("horario", "9:00").param("edadMinima", "18")
                    .param("edadMaxima", "99").param("cupoMaximo", "15").param("cuotaMensual", "100.25"))
                    .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/secretaria/actividades"));
        }
        verify(actividades).guardar(isNull(), any());
        verify(actividades).guardar(eq(2L), any());
    }

    @Test void actividadConRangoInvalidoNoGuarda() throws Exception {
        mvc.perform(post("/secretaria/actividades").param("nombre", "Yoga").param("disciplina", "Bienestar")
                .param("profesor", "Ana").param("dias", "Lunes").param("horario", "9:00").param("edadMinima", "20")
                .param("edadMaxima", "18").param("cupoMaximo", "15").param("cuotaMensual", "100"))
                .andExpect(status().isOk()).andExpect(model().attributeHasErrors("formulario"))
                .andExpect(content().string(containsString("La edad máxima")));
        verify(actividades, never()).guardar(any(), any());
    }

    @Test void actividadConActivosNoSeElimina() throws Exception {
        doThrow(new ValidacionSecretariaException("Tiene inscripciones activas")).when(actividades).eliminar(2L);
        mvc.perform(post("/secretaria/actividades/2/eliminar")).andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("error", "Tiene inscripciones activas"));
    }

    @Test void recursosInexistentesYFiltrosInvalidos() throws Exception {
        when(socios.obtener(999L)).thenThrow(new RecursoNoEncontradoException("Socio", 999L));
        mvc.perform(get("/secretaria/socios/999/editar")).andExpect(status().isNotFound());
        mvc.perform(get("/secretaria/inscripciones").param("estado", "INVENTADO"))
                .andExpect(status().isBadRequest());
    }

    @Test void errorSQLNoSeExpone() throws Exception {
        when(inscripciones.inscribir(1L, 2L)).thenThrow(new org.springframework.dao.DataAccessResourceFailureException("SQL privado"));
        mvc.perform(post("/secretaria/inscripciones").param("socioId", "1").param("actividadId", "2"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.not(containsString("SQL privado"))));
    }

    private ActividadFormulario actividad() {
        return new ActividadFormulario("Yoga", "Bienestar", "Ana", "Lunes", "9:00", 18, 99, 15, BigDecimal.TEN);
    }
}
