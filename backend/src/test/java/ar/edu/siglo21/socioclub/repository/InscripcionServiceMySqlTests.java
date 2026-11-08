package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.service.InscripcionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import static org.junit.jupiter.api.Assertions.*;

/** Dos transacciones reales: no sustituir por mocks para afirmar seguridad de concurrencia. */
@SpringBootTest(properties = {"spring.sql.init.mode=never", "spring.jpa.hibernate.ddl-auto=validate"})
@EnabledIf(value = "mysqlConfigurado", disabledReason = "MySQL no configurado; concurrencia no validada")
class InscripcionServiceMySqlTests {
    @Autowired InscripcionService servicio;
    @Autowired ar.edu.siglo21.socioclub.service.ConsultaSecretariaService consultas;
    @Autowired ar.edu.siglo21.socioclub.service.ActividadService actividadService;
    @Autowired ar.edu.siglo21.socioclub.service.ApiConsultaService api;
    @Autowired ar.edu.siglo21.socioclub.service.SocioService gestionSocios;
    @Autowired SocioRepository socios;
    @Autowired ActividadRepository actividades;
    @Autowired InscripcionRepository inscripciones;
    @Autowired JdbcTemplate jdbc;
    @Autowired PlatformTransactionManager transacciones;
    private final List<Long> sociosCreados = new ArrayList<>();
    private final List<Long> actividadesCreadas = new ArrayList<>();

    static boolean mysqlConfigurado() {
        return PersistenciaMySqlTests.mysqlConfigurado();
    }

    @DynamicPropertySource
    static void configurar(DynamicPropertyRegistry registry) {
        // Reutiliza URL exclusiva y credenciales obligatorias, sin fallback a desarrollo.
        PersistenciaMySqlTests.configurar(registry);
    }

    @BeforeEach
    void comprobarBaseVacia() {
        comprobarCatalogo();
        for (String tabla : List.of("socio", "actividad", "inscripcion")) {
            assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM " + tabla, Long.class),
                    "Usar socioclub_test vacía; no se borran datos para preparar pruebas");
        }
    }

    private void comprobarCatalogo() {
        assertEquals("socioclub_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
    }

    private TransactionTemplate transaccion() {
        TransactionTemplate tx = new TransactionTemplate(transacciones);
        tx.setTimeout(10);
        return tx;
    }

    private void preparar(int cupo, boolean dosActivasPrevias) {
        transaccion().executeWithoutResult(estado -> {
            comprobarCatalogo();
            for (int i = 0; i < 2; i++) {
                Socio socio = socios.saveAndFlush(new Socio("9800000" + i, "Ensayo", "Concurrencia",
                        LocalDate.of(2000, 1, 1), "concurrencia@example.invalid", "PRUEBA", EstadoCuota.AL_DIA));
                sociosCreados.add(socio.getId());
            }
            for (int i = 0; i < 4; i++) {
                Actividad actividad = actividades.saveAndFlush(new Actividad("Ensayo " + i, "Prueba",
                        "Docente", "Lun", "10:00", 18, 99, cupo, BigDecimal.TEN));
                actividadesCreadas.add(actividad.getId());
                if (dosActivasPrevias && i < 2) {
                    inscripciones.saveAndFlush(new Inscripcion(socios.findById(sociosCreados.getFirst()).orElseThrow(),
                            actividad, LocalDate.now(), EstadoInscripcion.ACTIVA));
                }
            }
        });
    }

    @Test
    void bajaSocioPersisteLiberaCuposYConservaHistorial() {
        preparar(1, true);
        Long socioId = sociosCreados.getFirst();
        transaccion().executeWithoutResult(estado -> {
            comprobarCatalogo();
            inscripciones.saveAndFlush(new Inscripcion(socios.findById(socioId).orElseThrow(),
                    actividades.findById(actividadesCreadas.get(2)).orElseThrow(),
                    LocalDate.of(2025, 1, 1), EstadoInscripcion.BAJA));
        });
        var original = socios.findById(socioId).orElseThrow();
        var baja = api.bajaSocio(socioId);
        assertEquals(EstadoSocio.INACTIVO, baja.estadoSocio());
        assertEquals(original.getEstadoCuota(), baja.estadoCuota());
        assertEquals(original.getDni(), baja.dni());
        assertEquals(3, inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.BAJA));
        assertEquals(0, inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA));
        for (Long id : actividadesCreadas) { assertEquals(0, inscripciones.contarActivasDeActividad(id)); }
        gestionSocios.darDeBaja(socioId);
        assertEquals(3, inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.BAJA));
        assertEquals(1L, jdbc.queryForObject("SELECT COUNT(*) FROM inscripcion WHERE socio_id = ? AND fecha_inscripcion = '2025-01-01'", Long.class, socioId));
        assertThrows(ar.edu.siglo21.socioclub.exception.SocioInactivoException.class,
                () -> servicio.inscribir(socioId, actividadesCreadas.get(3)));
    }

    @Test
    void falloSQLAntesDelCommitRevierteVigenciaYBajasYaFlusheadas() {
        preparar(1, true);
        Long socioId = sociosCreados.getFirst();
        var tx = transaccion();
        tx.setIsolationLevel(org.springframework.transaction.TransactionDefinition.ISOLATION_READ_COMMITTED);
        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () -> tx.executeWithoutResult(estado -> {
            comprobarCatalogo();
            gestionSocios.darDeBaja(socioId); // Flush real en la misma transacción, todavía sin commit.
            jdbc.update("INSERT INTO inscripcion (socio_id, actividad_id, fecha_inscripcion, estado) VALUES (?, ?, CURRENT_DATE, 'ACTIVA')",
                    Long.MAX_VALUE, actividadesCreadas.get(3));
        }));
        assertEquals(EstadoSocio.ACTIVO, socios.findById(socioId).orElseThrow().getEstadoSocio());
        assertEquals(2, inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA));
        assertEquals(0, inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.BAJA));
    }

    @Test
    void altaYBajaSocioSimultaneasNoDejanInactivoConActivas() throws Exception {
        preparar(1, false);
        Long socioId = sociosCreados.getFirst();
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        CountDownLatch listos = new CountDownLatch(2);
        CountDownLatch salida = new CountDownLatch(1);
        try {
            Future<String> alta = ejecutor.submit(() -> {
                listos.countDown();
                if (!salida.await(5, TimeUnit.SECONDS)) { throw new IllegalStateException("Sin salida"); }
                try { servicio.inscribir(socioId, actividadesCreadas.getFirst()); return "ALTA"; }
                catch (ar.edu.siglo21.socioclub.exception.SocioInactivoException e) { return "INACTIVO"; }
            });
            Future<?> baja = ejecutor.submit(() -> {
                listos.countDown();
                if (!salida.await(5, TimeUnit.SECONDS)) { throw new IllegalStateException("Sin salida"); }
                gestionSocios.darDeBaja(socioId);
                return null;
            });
            assertTrue(listos.await(5, TimeUnit.SECONDS)); salida.countDown();
            assertTrue(List.of("ALTA", "INACTIVO").contains(alta.get(20, TimeUnit.SECONDS)));
            baja.get(20, TimeUnit.SECONDS);
        } finally {
            salida.countDown(); ejecutor.shutdownNow();
            assertTrue(ejecutor.awaitTermination(20, TimeUnit.SECONDS));
        }
        assertEquals(EstadoSocio.INACTIVO, socios.findById(socioId).orElseThrow().getEstadoSocio());
        assertEquals(0, inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA));
    }

    @Test
    void contratoApiConsultaAltaBajaYActividadPersistida() {
        preparar(1, false);
        var socio = api.socioPorDni("98000000");
        assertEquals(sociosCreados.getFirst(), socio.id());
        var creada = api.guardarActividad(null,
                new ar.edu.siglo21.socioclub.dto.SecretariaDtos.ActividadFormulario(
                        "API Ensayo", "API", "Docente", "Lun", "9:00", 18, 99, 1, java.math.BigDecimal.TEN));
        actividadesCreadas.add(creada.id());
        assertEquals(1, api.actividades("API").size());
        assertEquals(1, creada.cupoDisponible());
        var alta = api.inscribir(socio.id(), actividadesCreadas.getFirst());
        assertEquals(0, alta.actividad().cupoDisponible());
        assertEquals(1, api.inscripciones(socio.id()).size());
        var baja = api.baja(alta.id());
        assertEquals(EstadoInscripcion.BAJA, baja.estado());
        assertEquals(1, baja.actividad().cupoDisponible());
        assertTrue(api.inscripciones(socio.id()).isEmpty());
        api.eliminarActividad(creada.id());
        assertFalse(actividades.existsById(creada.id()));
    }

    @Test
    void consultaYBajaPersistidasParaSocioMoroso() {
        preparar(1, false);
        Long socioId = sociosCreados.getFirst();
        Long actividadId = actividadesCreadas.getFirst();
        Long id = servicio.inscribir(socioId, actividadId).getId();
        transaccion().executeWithoutResult(estado -> {
            comprobarCatalogo();
            Socio socio = socios.findById(socioId).orElseThrow();
            socio.setEstadoCuota(EstadoCuota.MOROSO);
        });
        servicio.darDeBaja(id);
        servicio.darDeBaja(id);
        assertEquals(0, consultas.listar(actividadId, EstadoInscripcion.ACTIVA, 0).getTotalElements());
        var bajas = consultas.listar(actividadId, EstadoInscripcion.BAJA, 0);
        assertEquals(1, bajas.getTotalElements());
        assertEquals(EstadoInscripcion.BAJA, bajas.getContent().getFirst().estado());
        assertEquals(0, inscripciones.contarActivasDeActividad(actividadId));
        assertEquals(0, consultas.resumen().activas());
    }

    @Test
    void administracionProtegeCupoHistorialYPermiteEliminarSinVinculos() {
        preparar(10, false);
        Long actividadId = actividadesCreadas.getFirst();
        Long inscripcionId = servicio.inscribir(sociosCreados.get(0), actividadId).getId();
        servicio.inscribir(sociosCreados.get(1), actividadId);
        var original = actividadService.obtener(actividadId);
        var reducido = new ar.edu.siglo21.socioclub.dto.SecretariaDtos.ActividadFormulario(
                original.nombre(), original.disciplina(), original.profesor(), original.dias(),
                original.horario(), original.edadMinima(), original.edadMaxima(), 1, original.cuotaMensual());
        assertThrows(ar.edu.siglo21.socioclub.exception.ValidacionSecretariaException.class,
                () -> actividadService.guardar(actividadId, reducido));
        assertEquals(10, actividadService.obtener(actividadId).cupoMaximo());
        assertThrows(ar.edu.siglo21.socioclub.exception.ValidacionSecretariaException.class,
                () -> actividadService.eliminar(actividadId));
        servicio.darDeBaja(inscripcionId);
        Long segunda = inscripciones.findBySocioIdAndEstado(sociosCreados.get(1), EstadoInscripcion.ACTIVA,
                org.springframework.data.domain.PageRequest.of(0, 10)).getContent().getFirst().getId();
        servicio.darDeBaja(segunda);
        assertThrows(ar.edu.siglo21.socioclub.exception.ValidacionSecretariaException.class,
                () -> actividadService.eliminar(actividadId));
        actividadService.eliminar(actividadesCreadas.get(3));
        assertFalse(actividades.existsById(actividadesCreadas.get(3)));
    }

    @Test
    void bajaNoOcupaCupoNiImpideTerceraActividadYConservaHistorial() {
        preparar(1, true);
        transaccion().executeWithoutResult(estado -> {
            comprobarCatalogo();
            inscripciones.saveAndFlush(new Inscripcion(
                    socios.findById(sociosCreados.getFirst()).orElseThrow(),
                    actividades.findById(actividadesCreadas.get(2)).orElseThrow(),
                    LocalDate.now().minusDays(1), EstadoInscripcion.BAJA));
        });
        servicio.inscribir(sociosCreados.getFirst(), actividadesCreadas.get(2));
        assertEquals(3, inscripciones.countBySocioIdAndEstado(sociosCreados.getFirst(), EstadoInscripcion.ACTIVA));
        assertEquals(1, inscripciones.countBySocioIdAndEstado(sociosCreados.getFirst(), EstadoInscripcion.BAJA));
        assertEquals(1, inscripciones.contarActivasDeActividad(actividadesCreadas.get(2)));
    }

    @Test
    void dosSociosCompitenPorUltimoCupo() throws Exception {
        preparar(1, false);
        competir(sociosCreados.get(0), actividadesCreadas.get(0),
                sociosCreados.get(1), actividadesCreadas.get(0), "SIN_CUPO");
        assertEquals(1, inscripciones.contarActivasDeActividad(actividadesCreadas.getFirst()));
    }

    @Test
    void mismoSocioNoDuplicaInscripcionSimultanea() throws Exception {
        preparar(10, false);
        competir(sociosCreados.get(0), actividadesCreadas.get(0),
                sociosCreados.get(0), actividadesCreadas.get(0), "INSCRIPCION_DUPLICADA");
        assertEquals(1, inscripciones.countBySocioIdAndEstado(sociosCreados.getFirst(), EstadoInscripcion.ACTIVA));
    }

    @Test
    void mismoSocioNoSuperaTresEnActividadesDistintas() throws Exception {
        preparar(10, true);
        competir(sociosCreados.get(0), actividadesCreadas.get(2),
                sociosCreados.get(0), actividadesCreadas.get(3), "MAXIMO_ACTIVIDADES");
        assertEquals(3, inscripciones.countBySocioIdAndEstado(sociosCreados.getFirst(), EstadoInscripcion.ACTIVA));
    }

    private void competir(Long socio1, Long actividad1, Long socio2, Long actividad2, String rechazo) throws Exception {
        ExecutorService ejecutor = Executors.newFixedThreadPool(2);
        CountDownLatch listos = new CountDownLatch(2);
        CountDownLatch salida = new CountDownLatch(1);
        try {
            Future<String> primero = ejecutor.submit(() -> alta(socio1, actividad1, listos, salida));
            Future<String> segundo = ejecutor.submit(() -> alta(socio2, actividad2, listos, salida));
            assertTrue(listos.await(5, TimeUnit.SECONDS), "Ambos trabajadores deben estar listos");
            salida.countDown();
            List<String> resultados = List.of(primero.get(20, TimeUnit.SECONDS), segundo.get(20, TimeUnit.SECONDS));
            assertEquals(1, resultados.stream().filter("OK"::equals).count());
            assertEquals(1, resultados.stream().filter(rechazo::equals).count());
        } finally {
            salida.countDown();
            ejecutor.shutdownNow();
            assertTrue(ejecutor.awaitTermination(20, TimeUnit.SECONDS), "Los trabajadores deben finalizar");
        }
    }

    private String alta(Long socio, Long actividad, CountDownLatch listos, CountDownLatch salida)
            throws InterruptedException {
        listos.countDown();
        if (!salida.await(5, TimeUnit.SECONDS)) { throw new IllegalStateException("No se habilitó la salida"); }
        try {
            servicio.inscribir(socio, actividad);
            return "OK";
        } catch (ReglaNegocioException e) {
            return e.getCodigo().name();
        }
    }

    @AfterEach
    void retirarSoloFixturesPropias() {
        if (sociosCreados.isEmpty() && actividadesCreadas.isEmpty()) { return; }
        transaccion().executeWithoutResult(estado -> {
            comprobarCatalogo();
            // La concurrencia necesita commits visibles entre conexiones; limpiar solo IDs propios.
            for (Long id : sociosCreados) {
                jdbc.update("DELETE FROM inscripcion WHERE socio_id = ?", id);
                jdbc.update("DELETE FROM socio WHERE id = ?", id);
            }
            for (Long id : actividadesCreadas) {
                jdbc.update("DELETE FROM actividad WHERE id = ?", id);
            }
        });
    }
}
