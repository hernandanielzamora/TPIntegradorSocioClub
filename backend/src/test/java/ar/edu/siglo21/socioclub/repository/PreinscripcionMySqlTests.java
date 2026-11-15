package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.dto.PreinscripcionDtos.Solicitud;
import ar.edu.siglo21.socioclub.model.Actividad;
import ar.edu.siglo21.socioclub.service.PreinscripcionService;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnabledIf(value = "mysqlConfigurado", disabledReason = "MySQL aislado no configurado")
@Transactional
class PreinscripcionMySqlTests {
    @Autowired PreinscripcionService servicio;
    @Autowired ActividadRepository actividades;
    @Autowired PreinscripcionRepository solicitudes;
    @Autowired JdbcTemplate jdbc;
    static boolean mysqlConfigurado() { return PersistenciaMySqlTests.mysqlConfigurado(); }
    @DynamicPropertySource static void configurar(DynamicPropertyRegistry registry) { PersistenciaMySqlTests.configurar(registry); }
    @Test void recepcionPersistidaListadoYPlazasSinCambios() {
        assertEquals("socioclub_test",jdbc.queryForObject("SELECT DATABASE()",String.class));
        long socios=jdbc.queryForObject("SELECT COUNT(*) FROM socio",Long.class);
        long inscripciones=jdbc.queryForObject("SELECT COUNT(*) FROM inscripcion",Long.class);
        var a=actividades.saveAndFlush(new Actividad("Solicitud de ensayo","Prueba","Docente","Lun","10:00",8,99,1,BigDecimal.TEN));
        var respuesta=servicio.recibir(new Solicitud(" Ana "," Ensayo ","87654321","ana@example.invalid",a.getId()));
        assertNotNull(respuesta.id());
        var guardada=solicitudes.findById(respuesta.id()).orElseThrow();
        assertEquals("Ana",guardada.getNombre());assertEquals(a.getId(),guardada.getActividad().getId());
        assertTrue(servicio.listar(0).stream().anyMatch(p -> p.id().equals(respuesta.id())));
        assertEquals(socios,jdbc.queryForObject("SELECT COUNT(*) FROM socio",Long.class));
        assertEquals(inscripciones,jdbc.queryForObject("SELECT COUNT(*) FROM inscripcion",Long.class));
    }
}
