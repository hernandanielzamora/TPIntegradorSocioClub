package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.persistence.EntityManager;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas reales optativas, con rollback por método, exclusivamente en socioclub_test.
 * MYSQL_TEST_DB_URL o MYSQL_TEST_ENABLED=true activan la suite; una conexión inválida falla.
 * Requiere tablas de ensayo preparadas manualmente; la cuenta no necesita permisos DDL.
 */
@SpringBootTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@EnabledIf(value = "mysqlConfigurado", disabledReason = "Sin MYSQL_TEST_DB_URL ni MYSQL_TEST_ENABLED=true; MySQL no validado")
@Transactional
class PersistenciaMySqlTests {
    @Autowired private SocioRepository socios;
    @Autowired private ActividadRepository actividades;
    @Autowired private InscripcionRepository inscripciones;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private DataSource dataSource;
    @Autowired private EntityManager entityManager;

    /** Comprueba la base efectiva antes de que cada prueba escriba datos. */
    @BeforeEach
    void comprobarBaseDeEnsayo() {
        assertEquals("socioclub_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
    }

    /** No omite pruebas cuando se proporcionó una URL de ensayo, aunque la conexión falle. */
    static boolean mysqlConfigurado() {
        String url = System.getenv("MYSQL_TEST_DB_URL");
        String habilitado = System.getenv("MYSQL_TEST_ENABLED");
        if (habilitado != null && !habilitado.isBlank()
                && !habilitado.equalsIgnoreCase("true") && !habilitado.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("MYSQL_TEST_ENABLED debe ser true o false");
        }
        return (url != null && !url.isBlank()) || Boolean.parseBoolean(habilitado);
    }

    @DynamicPropertySource
    static void configurar(DynamicPropertyRegistry registry) {
        String url = Objects.requireNonNull(System.getenv("MYSQL_TEST_DB_URL"),
                "Configurar MYSQL_TEST_DB_URL para una base de ensayo");
        if (!url.matches("jdbc:mysql://[^/]+/socioclub_test(\\?.*)?")) {
            throw new IllegalArgumentException("Las pruebas solo admiten la base socioclub_test");
        }
        registry.add("spring.datasource.url", () -> url);
        // Una cuenta de ensayo puede carecer de permisos sobre socioclub.
        String usuario = System.getenv("MYSQL_TEST_DB_USERNAME");
        String clave = System.getenv("MYSQL_TEST_DB_PASSWORD");
        if (usuario != null && !usuario.isBlank()) {
            registry.add("spring.datasource.username", () -> usuario);
        }
        if (clave != null) {
            registry.add("spring.datasource.password", () -> clave);
        }
    }

    private Socio guardarSocio(String dni, String apellido) {
        return socios.saveAndFlush(new Socio(dni, "Prueba", apellido,
                LocalDate.of(2000, 1, 1), "prueba@example.invalid", "PRUEBA", EstadoCuota.AL_DIA));
    }

    private Actividad guardarActividad() {
        return actividades.saveAndFlush(new Actividad("Ensayo", "Disciplina de ensayo",
                "Docente", "Sáb", "10:00", 8, 99, 16, new BigDecimal("9000.25")));
    }

    @Test
    void consultaDniApellidoPaginadoYDisciplina() {
        guardarSocio("99000001", "ApellidoEnsayoUno");
        guardarSocio("99000002", "ApellidoEnsayoDos");
        Actividad actividad = guardarActividad();
        assertTrue(socios.findByDni("99000001").isPresent());
        var pagina = socios.findByApellidoContainingIgnoreCase("APELLIDOENSAYO",
                PageRequest.of(0, 1, Sort.by("dni")));
        assertEquals(2, pagina.getTotalElements());
        assertEquals(1, pagina.getContent().size());
        assertEquals("99000001", pagina.getContent().getFirst().getDni());
        assertTrue(actividades.findByDisciplina("Disciplina de ensayo",
                PageRequest.of(0, 10)).getContent().stream()
                .anyMatch(a -> a.getId().equals(actividad.getId())));
    }

    @Test
    void conservaBajasYReinscripcionesYCuentaSoloActivas() {
        Socio socio = guardarSocio("99000003", "Historial");
        Actividad actividad = guardarActividad();
        inscripciones.saveAndFlush(new Inscripcion(socio, actividad,
                LocalDate.of(2026, 1, 1), EstadoInscripcion.BAJA));
        inscripciones.saveAndFlush(new Inscripcion(socio, actividad,
                LocalDate.now(), EstadoInscripcion.ACTIVA));
        assertEquals(1, inscripciones.contarActivasDeActividad(actividad.getId()));
        assertEquals(1, inscripciones.countBySocioIdAndEstado(socio.getId(), EstadoInscripcion.ACTIVA));
        assertEquals(1, inscripciones.findBySocioIdAndEstado(socio.getId(),
                EstadoInscripcion.ACTIVA, PageRequest.of(0, 10)).getTotalElements());
        assertEquals(1, inscripciones.findByActividadIdAndEstado(actividad.getId(),
                EstadoInscripcion.ACTIVA, PageRequest.of(0, 10)).getTotalElements());
        assertTrue(inscripciones.existsBySocioIdAndActividadIdAndEstado(
                socio.getId(), actividad.getId(), EstadoInscripcion.ACTIVA));
        assertEquals(2, jdbc.queryForObject(
                "SELECT COUNT(*) FROM inscripcion WHERE socio_id = ? AND actividad_id = ?",
                Long.class, socio.getId(), actividad.getId()));
    }

    @Test
    void persisteMontosFechasYRelacionesEnMySql() {
        Socio socio = guardarSocio("99000004", "Tipos");
        Actividad actividad = guardarActividad();
        Inscripcion inscripcion = inscripciones.saveAndFlush(new Inscripcion(socio, actividad,
                LocalDate.now(), EstadoInscripcion.ACTIVA));
        Long socioId = socio.getId();
        Long actividadId = actividad.getId();
        Long inscripcionId = inscripcion.getId();
        // Evita confundir objetos del contexto de persistencia con datos recuperados de MySQL.
        entityManager.clear();
        Inscripcion recuperada = inscripciones.findById(inscripcionId).orElseThrow();
        assertEquals(socioId, recuperada.getSocio().getId());
        assertEquals("Tipos", recuperada.getSocio().getApellido());
        assertEquals(actividadId, recuperada.getActividad().getId());
        assertEquals(new BigDecimal("9000.25"), recuperada.getActividad().getCuotaMensual());
        assertEquals(EstadoInscripcion.ACTIVA, recuperada.getEstado());
        assertEquals(EstadoCuota.AL_DIA, recuperada.getSocio().getEstadoCuota());
        assertEquals(LocalDate.now(), recuperada.getFechaInscripcion());
        assertEquals("socioclub_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
        assertEquals(new BigDecimal("9000.25"), jdbc.queryForObject(
                "SELECT cuota_mensual FROM actividad WHERE id = ?", BigDecimal.class, actividad.getId()));
        assertEquals(LocalDate.of(2000, 1, 1), jdbc.queryForObject(
                "SELECT fecha_nacimiento FROM socio WHERE id = ?", java.sql.Date.class, socio.getId()).toLocalDate());
        assertEquals(socio.getId(), jdbc.queryForObject(
                "SELECT socio_id FROM inscripcion WHERE id = ?", Long.class, inscripcion.getId()));
    }

    @Test
    void rechazaDniDuplicadoEnLaBase() {
        guardarSocio("99000005", "Unico");
        assertThrows(DataIntegrityViolationException.class,
                () -> guardarSocio("99000005", "Duplicado"));
    }

    @Test
    void rechazaClaveForaneaInexistente() {
        Actividad actividad = guardarActividad();
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("""
                INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado)
                VALUES (?, ?, CURRENT_DATE, 'ACTIVA')
                """, Long.MAX_VALUE, actividad.getId()));
    }

    @Test
    void rechazaEstadosYMontosInvalidosDesdeSql() {
        Actividad actividad = guardarActividad();
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                "UPDATE actividad SET cuota_mensual = -1 WHERE id = ?", actividad.getId()));
        Socio socio = guardarSocio("99000006", "Estado");
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update(
                "UPDATE socio SET estado_cuota = 'INVENTADO' WHERE id = ?", socio.getId()));
    }

    @Test
    void seedRepetibleConservaDatosYNoReactivaBajas() throws Exception {
        // Se exige una base de ensayo vacía; nunca se borra información para prepararla.
        assertEquals(0, socios.count(), "Usar socioclub_test sin registros previos");
        assertEquals(0, actividades.count(), "Usar socioclub_test sin registros previos");
        // El test administra la transacción para poder revertir toda la carga de ejemplo.
        String seed = Files.readString(Path.of("../database/seed.sql"), StandardCharsets.UTF_8)
                .replace("START TRANSACTION;", "").replace("COMMIT;", "");
        // El script compartido nunca debe cambiar el catálogo configurado por la prueba.
        assertFalse(java.util.regex.Pattern.compile("(?im)^\\s*USE\\s+").matcher(seed).find(),
                "seed.sql no debe seleccionar una base: se ejecuta en socioclub_test");
        var carga = new ResourceDatabasePopulator(
                new ByteArrayResource(seed.getBytes(StandardCharsets.UTF_8)));
        carga.setSqlScriptEncoding("UTF-8");
        carga.execute(dataSource);
        carga.execute(dataSource);
        assertEquals(8, actividades.count());
        assertEquals(21, socios.count());
        assertEquals(19, inscripciones.count());
        assertEquals(2, jdbc.queryForObject(
                "SELECT COUNT(*) FROM socio WHERE estado_cuota = 'MOROSO'", Long.class));
        Long yogaId = jdbc.queryForObject(
                "SELECT id FROM actividad WHERE nombre = 'Yoga'", Long.class);
        assertEquals(14, inscripciones.contarActivasDeActividad(yogaId));
        Long inscripcionId = jdbc.queryForObject(
                "SELECT MIN(id) FROM inscripcion WHERE actividad_id = ?", Long.class, yogaId);
        jdbc.update("UPDATE inscripcion SET estado = 'BAJA' WHERE id = ?", inscripcionId);
        jdbc.update("UPDATE socio SET nombre = 'Edicion conservada' WHERE dni = '30111222'");
        carga.execute(dataSource);
        assertEquals(19, inscripciones.count());
        assertEquals(13, inscripciones.contarActivasDeActividad(yogaId));
        assertEquals("Edicion conservada", jdbc.queryForObject(
                "SELECT nombre FROM socio WHERE dni = '30111222'", String.class));
    }
}
