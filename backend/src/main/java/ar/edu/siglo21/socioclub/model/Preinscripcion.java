package ar.edu.siglo21.socioclub.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Solicitud pública recibida; no crea socios ni consume plazas. */
@Entity
@Table(name = "preinscripcion")
public class Preinscripcion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80) private String nombre;
    @Column(nullable = false, length = 80) private String apellido;
    @Column(nullable = false, length = 8) private String dni;
    @Column(nullable = false, length = 150) private String email;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;
    @Column(name = "fecha_recepcion", nullable = false)
    private LocalDateTime fechaRecepcion;

    protected Preinscripcion() { }
    public Preinscripcion(String nombre, String apellido, String dni, String email,
            Actividad actividad, LocalDateTime fechaRecepcion) {
        this.nombre = nombre; this.apellido = apellido; this.dni = dni;
        this.email = email; this.actividad = actividad; this.fechaRecepcion = fechaRecepcion;
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public String getEmail() { return email; }
    public Actividad getActividad() { return actividad; }
    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
}
