package ar.edu.siglo21.socioclub.controller.api;

import ar.edu.siglo21.socioclub.controller.mvc.PreinscripcionController;
import ar.edu.siglo21.socioclub.dto.PreinscripcionDtos.*;
import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socioclub.service.PreinscripcionService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({PreinscripcionApiController.class, PreinscripcionController.class})
class PreinscripcionControllerTests {
    @Autowired MockMvc mvc;
    @MockitoBean PreinscripcionService servicio;
    private static final String JSON = """
        {"nombre":"Ana","apellido":"Ensayo","dni":"87654321","email":"ana@example.invalid","actividadId":8}
        """;
    @Test void recepcion201SinDatosPersonalesEnRespuesta() throws Exception {
        when(servicio.recibir(any())).thenReturn(new Recepcion(5L, LocalDateTime.of(2026,11,15,12,0)));
        mvc.perform(post("/api/preinscripciones").contentType("application/json").content(JSON))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.email").doesNotExist()).andExpect(jsonPath("$.dni").doesNotExist());
    }
    @Test void dniInvalido400NoLlegaAlServicio() throws Exception {
        mvc.perform(post("/api/preinscripciones").contentType("application/json")
                .content(JSON.replace("87654321", "87.654.321")))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.detalles[0].campo").value("dni"));
        verifyNoInteractions(servicio);
    }
    @Test void camposObligatorios400() throws Exception {
        mvc.perform(post("/api/preinscripciones").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(servicio);
    }
    @Test void actividadInexistente404() throws Exception {
        when(servicio.recibir(any())).thenThrow(new RecursoNoEncontradoException("Actividad",8L));
        mvc.perform(post("/api/preinscripciones").contentType("application/json").content(JSON))
                .andExpect(status().isNotFound());
    }
    @Test void falloPersistencia503SinSql() throws Exception {
        when(servicio.recibir(any())).thenThrow(new org.springframework.dao.DataAccessResourceFailureException("SQL privado"));
        mvc.perform(post("/api/preinscripciones").contentType("application/json").content(JSON))
                .andExpect(status().isServiceUnavailable()).andExpect(content().string(not(containsString("SQL privado"))));
    }
    @Test void listadoSecretariaEscapaTextoYPresentaDatos() throws Exception {
        when(servicio.listar(0)).thenReturn(new PageImpl<>(List.of(new Vista(5L,"<script>","Ensayo","87654321",
                "ana@example.invalid","Yoga",LocalDateTime.of(2026,11,15,12,0)))));
        mvc.perform(get("/secretaria/preinscripciones")).andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;script&gt;")))
                .andExpect(content().string(containsString("ana@example.invalid")))
                .andExpect(content().string(containsString("Preinscripciones recibidas")));
    }
    @Test void noExisteListadoPublicoDeDatosPersonales() throws Exception {
        mvc.perform(get("/api/preinscripciones")).andExpect(status().isMethodNotAllowed());
    }
}
