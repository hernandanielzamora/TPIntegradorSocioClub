package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socioclub.exception.ReglaNegocioException;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import static ar.edu.siglo21.socioclub.exception.ReglaNegocioException.Codigo.*;

/** Único punto de alta compartido por futuros controladores MVC y REST. */
@Service
public class InscripcionService {
    private static final int MAXIMO_ACTIVIDADES = 3;
    private final SocioRepository socios;
    private final ActividadRepository actividades;
    private final InscripcionRepository inscripciones;
    private final Clock reloj;

    public InscripcionService(SocioRepository socios, ActividadRepository actividades,
            InscripcionRepository inscripciones, Clock reloj) {
        this.socios = socios;
        this.actividades = actividades;
        this.inscripciones = inscripciones;
        this.reloj = reloj;
    }

    /** Bloquear siempre socio y después actividad; contar y guardar en la misma transacción. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Inscripcion inscribir(Long socioId, Long actividadId) {
        Objects.requireNonNull(socioId, "El id del socio es obligatorio");
        Objects.requireNonNull(actividadId, "El id de la actividad es obligatorio");
        Socio socio = socios.buscarParaInscribir(socioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", socioId));
        Actividad actividad = actividades.buscarParaInscribir(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", actividadId));
        LocalDate hoy = LocalDate.now(reloj);
        validarSocio(socio, actividad, hoy);
        validarInscripciones(socioId, actividad);
        // Una reinscripción es una fila nueva: no modifica ni reactiva el historial BAJA.
        return inscripciones.save(new Inscripcion(socio, actividad, hoy, EstadoInscripcion.ACTIVA));
    }

    private void validarSocio(Socio socio, Actividad actividad, LocalDate hoy) {
        if (socio.getEstadoCuota() == EstadoCuota.MOROSO) {
            throw new ReglaNegocioException(SOCIO_MOROSO, "Debe regularizar su cuota en secretaría");
        }
        int edad = Period.between(socio.getFechaNacimiento(), hoy).getYears();
        if (edad < actividad.getEdadMinima() || edad > actividad.getEdadMaxima()) {
            throw new ReglaNegocioException(EDAD_FUERA_DE_RANGO,
                    "La edad debe estar entre " + actividad.getEdadMinima()
                    + " y " + actividad.getEdadMaxima() + " años inclusive");
        }
    }

    private void validarInscripciones(Long socioId, Actividad actividad) {
        if (inscripciones.existsBySocioIdAndActividadIdAndEstado(
                socioId, actividad.getId(), EstadoInscripcion.ACTIVA)) {
            throw new ReglaNegocioException(INSCRIPCION_DUPLICADA,
                    "El socio ya está inscripto en esta actividad");
        }
        if (inscripciones.countBySocioIdAndEstado(socioId, EstadoInscripcion.ACTIVA) >= MAXIMO_ACTIVIDADES) {
            throw new ReglaNegocioException(ReglaNegocioException.Codigo.MAXIMO_ACTIVIDADES,
                    "Alcanzó el máximo de 3 actividades");
        }
        // READ_COMMITTED ve el alta que pudo confirmar otra transacción antes de liberar el bloqueo.
        if (inscripciones.contarActivasDeActividad(actividad.getId()) >= actividad.getCupoMaximo()) {
            throw new ReglaNegocioException(SIN_CUPO, "La actividad no tiene cupo disponible");
        }
    }
}
