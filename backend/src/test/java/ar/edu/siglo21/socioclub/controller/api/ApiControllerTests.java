package ar.edu.siglo21.socioclub.controller.api;

import ar.edu.siglo21.socioclub.dto.ApiDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.service.ApiConsultaService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
class ApiControllerTests {
    @Autowired MockMvc mvc;
    @MockitoBean ApiConsultaService servicio;
    private static final String ACTIVIDAD = """
        {"nombre":"Yoga","disciplina":"Bienestar","profesor":"Ana","dias":"Lunes","horario":"9:00",
         "edadMinima":18,"edadMaxima":99,"cupoMaximo":15,"cuotaMensual":100.25}
        """;
    private ActividadResponse actividad() {
        return new ActividadResponse(2L, "Yoga", "Bienestar", "Ana", "Lunes", "9:00", 18, 99, 15,
                new BigDecimal("100.25"), 1);
    }
    private InscripcionResponse inscripcion(EstadoInscripcion estado) {
        return new InscripcionResponse(3L, 1L, 2L, LocalDate.of(2026, 11, 8), estado, actividad());
    }

    @Test void listaConFiltroYJsonParaReact() throws Exception {
        when(servicio.actividades("Bienestar")).thenReturn(List.of(actividad()));
        mvc.perform(get("/api/actividades").param("disciplina", "Bienestar"))
                .andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].cupoDisponible").value(1))
                .andExpect(jsonPath("$[0].cuotaMensual").value(100.25))
                .andExpect(jsonPath("$[0].inscripciones").doesNotExist());
    }
    @Test void bajaSocio200YReintentoIdempotente() throws Exception {
        when(servicio.bajaSocio(1L)).thenReturn(new SocioResponse(1L, "99000001", "Ana", "Prueba",
                LocalDate.of(2000, 1, 1), EstadoCuota.AL_DIA, EstadoSocio.INACTIVO));
        for (int i = 0; i < 2; i++) {
            mvc.perform(put("/api/socios/1/baja")).andExpect(status().isOk())
                    .andExpect(jsonPath("$.estadoSocio").value("INACTIVO"))
                    .andExpect(jsonPath("$.estadoCuota").value("AL_DIA"));
        }
    }
    @Test void bajaSocioInexistente404() throws Exception {
        when(servicio.bajaSocio(99L)).thenThrow(new RecursoNoEncontradoException("Socio", 99L));
        mvc.perform(put("/api/socios/99/baja")).andExpect(status().isNotFound());
    }
    @Test void bajaSocioIdInvalido400() throws Exception {
        mvc.perform(put("/api/socios/-1/baja")).andExpect(status().isBadRequest());
        verifyNoInteractions(servicio);
    }
    @Test void inactivo409SinInventarRN() throws Exception {
        when(servicio.inscribir(1L, 2L)).thenThrow(new SocioInactivoException());
        mvc.perform(post("/api/inscripciones").contentType("application/json").content("{\"socioId\":1,\"actividadId\":2}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.codigo").value("SOCIO_INACTIVO"))
                .andExpect(jsonPath("$.regla").isEmpty());
    }
    @Test void listaVaciaEsArray() throws Exception {
        when(servicio.actividades(null)).thenReturn(List.of());
        mvc.perform(get("/api/actividades")).andExpect(status().isOk()).andExpect(content().json("[]"));
    }
    @Test void detalleActividad() throws Exception {
        when(servicio.actividad(2L)).thenReturn(actividad());
        mvc.perform(get("/api/actividades/2")).andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Yoga"));
    }
    @Test void recursoNoExiste() throws Exception {
        when(servicio.actividad(99L)).thenThrow(new RecursoNoEncontradoException("Actividad", 99L));
        mvc.perform(get("/api/actividades/99")).andExpect(status().isNotFound()).andExpect(jsonPath("$.error").isString());
    }
    @Test void socioPorDniSinDatosDeContactoNiRelaciones() throws Exception {
        when(servicio.socioPorDni("99000001")).thenReturn(new SocioResponse(1L, "99000001", "Ana", "Prueba",
                LocalDate.of(2000, 1, 1), EstadoCuota.MOROSO, EstadoSocio.ACTIVO));
        mvc.perform(get("/api/socios/dni/99000001")).andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoCuota").value("MOROSO"))
                .andExpect(jsonPath("$.email").doesNotExist()).andExpect(jsonPath("$.inscripciones").doesNotExist());
    }
    @Test void socioPorDniInexistente() throws Exception {
        when(servicio.socioPorDni("99000001")).thenThrow(new RecursoNoEncontradoException("Socio", null));
        mvc.perform(get("/api/socios/dni/99000001")).andExpect(status().isNotFound());
    }
    @Test void activasConActividadAnidada() throws Exception {
        when(servicio.inscripciones(1L)).thenReturn(List.of(inscripcion(EstadoInscripcion.ACTIVA)));
        mvc.perform(get("/api/socios/1/inscripciones")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].actividad.nombre").value("Yoga"))
                .andExpect(jsonPath("$[0].actividad.socios").doesNotExist());
    }
    @Test void socioInexistenteEnConsulta() throws Exception {
        when(servicio.inscripciones(99L)).thenThrow(new RecursoNoEncontradoException("Socio", 99L));
        mvc.perform(get("/api/socios/99/inscripciones")).andExpect(status().isNotFound());
    }
    @Test void actividadCreadaConLocation() throws Exception {
        when(servicio.guardarActividad(isNull(), any())).thenReturn(actividad());
        mvc.perform(post("/api/actividades").contentType("application/json").content(ACTIVIDAD))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/actividades/2"))
                .andExpect(jsonPath("$.id").value(2));
    }
    @Test void actualizaActividad() throws Exception {
        when(servicio.guardarActividad(eq(2L), any())).thenReturn(actividad());
        mvc.perform(put("/api/actividades/2").contentType("application/json").content(ACTIVIDAD))
                .andExpect(status().isOk());
    }
    @Test void eliminaSinCuerpo() throws Exception {
        mvc.perform(delete("/api/actividades/2")).andExpect(status().isNoContent()).andExpect(content().string(""));
        verify(servicio).eliminarActividad(2L);
    }
    @Test void noEliminaConHistorial() throws Exception {
        doThrow(new ValidacionSecretariaException("La actividad tiene historial")).when(servicio).eliminarActividad(2L);
        mvc.perform(delete("/api/actividades/2")).andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La actividad tiene historial"));
    }
    @Test void inscripcionValida201() throws Exception {
        when(servicio.inscribir(1L, 2L)).thenReturn(inscripcion(EstadoInscripcion.ACTIVA));
        mvc.perform(post("/api/inscripciones").contentType("application/json").content("{\"socioId\":1,\"actividadId\":2}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.estado").value("ACTIVA"));
    }
    @ParameterizedTest @EnumSource(ReglaNegocioException.Codigo.class)
    void regla409ConCodigoDelAnexo(ReglaNegocioException.Codigo codigo) throws Exception {
        String regla = switch (codigo) {
            case SIN_CUPO -> "RN1"; case INSCRIPCION_DUPLICADA -> "RN2"; case EDAD_FUERA_DE_RANGO -> "RN3";
            case SOCIO_MOROSO -> "RN4"; case MAXIMO_ACTIVIDADES -> "RN5";
        };
        when(servicio.inscribir(1L, 2L)).thenThrow(new ReglaNegocioException(codigo, "Rechazo esperado"));
        mvc.perform(post("/api/inscripciones").contentType("application/json").content("{\"socioId\":1,\"actividadId\":2}"))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.regla").value(regla))
                .andExpect(jsonPath("$.error").value("Rechazo esperado"));
    }
    @Test void bajaConservaEstado() throws Exception {
        when(servicio.baja(3L)).thenReturn(inscripcion(EstadoInscripcion.BAJA));
        mvc.perform(put("/api/inscripciones/3/baja")).andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("BAJA"));
    }
    @Test void bajaInexistente() throws Exception {
        when(servicio.baja(99L)).thenThrow(new RecursoNoEncontradoException("Inscripción", 99L));
        mvc.perform(put("/api/inscripciones/99/baja")).andExpect(status().isNotFound());
    }
    @ParameterizedTest @ValueSource(strings = {"{}", "{\"socioId\":-1,\"actividadId\":2}", "{malformado"})
    void requestsInvalidos400NoInscriben(String json) throws Exception {
        mvc.perform(post("/api/inscripciones").contentType("application/json").content(json))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        verifyNoInteractions(servicio);
    }
    @Test void actividadRangoInvalido400() throws Exception {
        mvc.perform(post("/api/actividades").contentType("application/json").content(ACTIVIDAD.replace("\"edadMaxima\":99", "\"edadMaxima\":10")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(servicio);
    }
    @ParameterizedTest @ValueSource(strings = {"/api/actividades/-1", "/api/actividades/abc", "/api/socios/dni/123"})
    void identificadoresInvalidos400(String ruta) throws Exception {
        mvc.perform(get(ruta)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.error").isString());
        verifyNoInteractions(servicio);
    }
    @Test void errorTecnicoNoRevelaSQL() throws Exception {
        when(servicio.actividad(2L)).thenThrow(new org.springframework.dao.DataAccessResourceFailureException("SQL secreto"));
        mvc.perform(get("/api/actividades/2")).andExpect(status().isServiceUnavailable())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("SQL secreto"))));
    }
    @Test void corsOrigenLocalPermitido() throws Exception {
        mvc.perform(options("/api/inscripciones").header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST").header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
    @Test void corsOrigenNoPermitido() throws Exception {
        mvc.perform(options("/api/inscripciones").header("Origin", "https://ajeno.invalid")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isForbidden()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
