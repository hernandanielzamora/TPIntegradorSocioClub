package ar.edu.siglo21.socioclub.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public final class PreinscripcionDtos {
    private PreinscripcionDtos() { }
    public record Solicitud(
            @NotBlank @Size(max = 80) String nombre,
            @NotBlank @Size(max = 80) String apellido,
            @NotBlank @Pattern(regexp = "[0-9]{7,8}", message = "Ingresá 7 u 8 dígitos sin puntos") String dni,
            @NotBlank @Email @Size(max = 150) String email,
            @NotNull @Positive Long actividadId) { }
    /** Respuesta pública mínima: no expone datos de contacto ni enlaces a listados privados. */
    public record Recepcion(Long id, LocalDateTime fechaRecepcion) { }
    public record Vista(Long id, String nombre, String apellido, String dni, String email,
            String actividad, LocalDateTime fechaRecepcion) { }
}
