package ar.edu.siglo21.socioclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Actividad del catálogo con sus límites y cuota mensual, sin cupo disponible almacenado. */
@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String disciplina;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String profesor;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = false, length = 50)
    private String dias;

    @NotBlank
    @Pattern(regexp = "([01]?[0-9]|2[0-3]):[0-5][0-9]", message = "Usar horario H:mm o HH:mm")
    @Column(nullable = false, length = 5)
    private String horario;

    @NotNull
    @PositiveOrZero
    @Column(name = "edad_minima", nullable = false)
    private Integer edadMinima;

    @NotNull
    @PositiveOrZero
    @Column(name = "edad_maxima", nullable = false)
    private Integer edadMaxima;

    @NotNull
    @Positive
    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    @Column(name = "cuota_mensual", nullable = false, precision = 10, scale = 2)
    private BigDecimal cuotaMensual;

    @OneToMany(mappedBy = "actividad")
    private List<Inscripcion> inscripciones = new ArrayList<>();

    /** Constructor requerido por JPA. */
    public Actividad() {
    }

    /** Crea un registro sin identidad persistida. */
    public Actividad(String nombre, String disciplina, String profesor, String dias, String horario, Integer edadMinima, Integer edadMaxima, Integer cupoMaximo, BigDecimal cuotaMensual) {
        this.nombre = nombre;
        this.disciplina = disciplina;
        this.profesor = profesor;
        this.dias = dias;
        this.horario = horario;
        this.edadMinima = edadMinima;
        this.edadMaxima = edadMaxima;
        this.cupoMaximo = cupoMaximo;
        this.cuotaMensual = cuotaMensual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getDias() {
        return dias;
    }

    public void setDias(String dias) {
        this.dias = dias;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public Integer getEdadMinima() {
        return edadMinima;
    }

    public void setEdadMinima(Integer edadMinima) {
        this.edadMinima = edadMinima;
    }

    public Integer getEdadMaxima() {
        return edadMaxima;
    }

    public void setEdadMaxima(Integer edadMaxima) {
        this.edadMaxima = edadMaxima;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public BigDecimal getCuotaMensual() {
        return cuotaMensual;
    }

    public void setCuotaMensual(BigDecimal cuotaMensual) {
        this.cuotaMensual = cuotaMensual;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }

    /** Comprueba la coherencia del rango, sin evaluar la edad de ningún socio (RN3 pendiente). */
    @AssertTrue(message = "La edad máxima debe ser mayor o igual a la mínima")
    public boolean isRangoEdadesValido() {
        return edadMinima == null || edadMaxima == null || edadMinima <= edadMaxima;
    }
}
