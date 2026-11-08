package ar.edu.siglo21.socioclub.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Valida integridad en memoria; no acredita persistencia ni reglas RN1–RN5. */
class IntegridadModeloTests {
    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void preparar() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrar() {
        factory.close();
    }

    private Socio socio() {
        return new Socio("30111222", "Socio", "Prueba", LocalDate.of(2001, 5, 15),
                "socio@example.invalid", "PRUEBA", EstadoCuota.AL_DIA);
    }

    private Actividad actividad() {
        return new Actividad("Yoga", "Bienestar", "Profesora", "Mar y Jue", "9:00",
                16, 99, 15, new BigDecimal("17000.00"));
    }

    @Test
    void aceptaDatosDelCatalogoYFechasSinHora() {
        assertTrue(validator.validate(socio()).isEmpty());
        assertTrue(validator.validate(actividad()).isEmpty());
        assertTrue(validator.validate(new Inscripcion(socio(), actividad(),
                LocalDate.now(), EstadoInscripcion.ACTIVA)).isEmpty());
    }

    @Test
    void rechazaDniIncorrectoYDatosPersonalesInvalidos() {
        Socio socio = socio();
        for (String dni : new String[]{"30.111.222", "abcdefg", "123456", "123456789", ""}) {
            socio.setDni(dni);
            assertFalse(validator.validateProperty(socio, "dni").isEmpty(), dni);
        }
        socio.setDni("1234567");
        assertTrue(validator.validateProperty(socio, "dni").isEmpty());
        socio.setEmail("sin-arroba");
        socio.setFechaNacimiento(LocalDate.now().plusDays(1));
        socio.setNombre(" ");
        socio.setEstadoCuota(null);
        assertEquals(4, validator.validate(socio).size());
    }

    @Test
    void rechazaRangosIncoherentesYCuposNoPositivos() {
        Actividad actividad = actividad();
        actividad.setEdadMinima(100);
        assertFalse(validator.validate(actividad).isEmpty());
        actividad.setEdadMinima(16);
        actividad.setCupoMaximo(0);
        assertFalse(validator.validateProperty(actividad, "cupoMaximo").isEmpty());
        actividad.setHorario("24:00");
        assertFalse(validator.validateProperty(actividad, "horario").isEmpty());
    }

    @Test
    void conservaPrecisionMonetariaYRechazaMasDeDosDecimales() {
        Actividad actividad = actividad();
        actividad.setCuotaMensual(new BigDecimal("12345.67"));
        assertTrue(validator.validate(actividad).isEmpty());
        assertEquals(new BigDecimal("12345.67"), actividad.getCuotaMensual());
        for (String monto : new String[]{"-0.01", "1.001", "100000000.00"}) {
            actividad.setCuotaMensual(new BigDecimal(monto));
            assertFalse(validator.validateProperty(actividad, "cuotaMensual").isEmpty());
        }
    }

    @Test
    void exigeRelacionesFechaYEstadoEnLaInscripcion() {
        Inscripcion inscripcion = new Inscripcion();
        assertEquals(4, validator.validate(inscripcion).size());
        inscripcion.setSocio(socio());
        inscripcion.setActividad(actividad());
        inscripcion.setEstado(EstadoInscripcion.BAJA);
        inscripcion.setFechaInscripcion(LocalDate.now().plusDays(1));
        assertFalse(validator.validate(inscripcion).isEmpty());
        inscripcion.setFechaInscripcion(LocalDate.now());
        assertTrue(validator.validate(inscripcion).isEmpty());
    }
}
