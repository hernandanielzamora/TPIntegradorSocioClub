package ar.edu.siglo21.socioclub.dto;

import ar.edu.siglo21.socioclub.model.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/** Formularios con campos permitidos y vistas planas: nunca se enlazan entidades desde HTTP. */
public final class SecretariaDtos {
    private SecretariaDtos() { }

    public record SocioFormulario(
            @NotBlank @Pattern(regexp = "[0-9]{7,8}", message = "DNI de 7 u 8 dígitos sin puntos") String dni,
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 80) String apellido,
            @NotNull @Past @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Size(max = 30) String telefono,
            @NotNull EstadoCuota estadoCuota) {
        public static SocioFormulario desde(Socio s) {
            return new SocioFormulario(s.getDni(), s.getNombre(), s.getApellido(),
                    s.getFechaNacimiento(), s.getEmail(), s.getTelefono(), s.getEstadoCuota());
        }
    }

    public record ActividadFormulario(
            @NotBlank @Size(max = 100) String nombre,
            @NotBlank @Size(max = 100) String disciplina,
            @NotBlank @Size(max = 100) String profesor,
            @NotBlank @Size(max = 50) String dias,
            @NotBlank @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Horario H:mm o HH:mm") String horario,
            @NotNull @PositiveOrZero Integer edadMinima,
            @NotNull @PositiveOrZero Integer edadMaxima,
            @NotNull @Positive Integer cupoMaximo,
            @NotNull @DecimalMin("0.00") @Digits(integer = 8, fraction = 2) BigDecimal cuotaMensual) {
        @AssertTrue(message = "La edad máxima debe ser mayor o igual a la mínima")
        public boolean isRangoEdadesValido() {
            return edadMinima == null || edadMaxima == null || edadMinima <= edadMaxima;
        }
        public static ActividadFormulario desde(Actividad a) {
            return new ActividadFormulario(a.getNombre(), a.getDisciplina(), a.getProfesor(), a.getDias(),
                    a.getHorario(), a.getEdadMinima(), a.getEdadMaxima(), a.getCupoMaximo(), a.getCuotaMensual());
        }
    }

    public record InscripcionFormulario(@NotNull @Positive Long socioId, @NotNull @Positive Long actividadId) { }
    public record SocioVista(Long id, SocioFormulario datos) { }
    public record ActividadVista(Long id, ActividadFormulario datos, long activas, long disponibles) { }
    public record InscripcionVista(Long id, String socio, String actividad, LocalDate fecha, EstadoInscripcion estado) { }
    public record Opcion(Long id, String etiqueta) { }
    public record Resumen(long socios, long actividades, long activas) { }
}
