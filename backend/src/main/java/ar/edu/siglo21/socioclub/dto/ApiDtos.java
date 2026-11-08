package ar.edu.siglo21.socioclub.dto;

import ar.edu.siglo21.socioclub.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Contrato JSON explícito, sin proxies ni referencias inversas JPA. */
public final class ApiDtos {
    private ApiDtos() { }
    public record ActividadResponse(Long id, String nombre, String disciplina, String profesor,
            String dias, String horario, Integer edadMinima, Integer edadMaxima,
            Integer cupoMaximo, BigDecimal cuotaMensual, long cupoDisponible) { }
    public record SocioResponse(Long id, String dni, String nombre, String apellido,
            LocalDate fechaNacimiento, EstadoCuota estadoCuota, EstadoSocio estadoSocio) { }
    public record InscripcionResponse(Long id, Long socioId, Long actividadId, LocalDate fechaInscripcion,
            EstadoInscripcion estado, ActividadResponse actividad) { }
    public record CampoError(String campo, String mensaje) { }
    public record ErrorResponse(String error, String regla, List<CampoError> detalles, String codigo) {
        public ErrorResponse(String error, String regla, List<CampoError> detalles) {
            this(error, regla, detalles, null);
        }
    }
}
