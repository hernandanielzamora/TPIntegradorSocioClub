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
