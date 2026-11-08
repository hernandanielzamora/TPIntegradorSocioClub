package ar.edu.siglo21.socioclub.controller.api;

import ar.edu.siglo21.socioclub.dto.ApiDtos.*;
import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.service.ApiConsultaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.net.URI;
import java.util.List;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/** Nueve endpoints del Anexo A y baja voluntaria de socios; sin acceso a repositorios o entidades. */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ApiController {
    private final ApiConsultaService servicio;
    public ApiController(ApiConsultaService servicio) { this.servicio = servicio; }

    @GetMapping("/actividades")
    public List<ActividadResponse> actividades(@RequestParam(required = false) @Size(max = 100) String disciplina) {
        return servicio.actividades(disciplina);
    }
    @GetMapping("/actividades/{id}")
    public ActividadResponse actividad(@PathVariable @Positive Long id) { return servicio.actividad(id); }

    @PostMapping(value = "/actividades", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActividadResponse> crear(@Valid @RequestBody ActividadFormulario formulario) {
        ActividadResponse resultado = servicio.guardarActividad(null, formulario);
        return ResponseEntity.created(URI.create("/api/actividades/" + resultado.id())).body(resultado);
    }
    @PutMapping(value = "/actividades/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ActividadResponse editar(@PathVariable @Positive Long id, @Valid @RequestBody ActividadFormulario formulario) {
        return servicio.guardarActividad(id, formulario);
    }
    @DeleteMapping("/actividades/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Long id) {
        servicio.eliminarActividad(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/socios/dni/{dni}")
    public SocioResponse socio(@PathVariable @Pattern(regexp = "[0-9]{7,8}") String dni) {
        return servicio.socioPorDni(dni);
    }
    @GetMapping("/socios/{id}/inscripciones")
    public List<InscripcionResponse> inscripciones(@PathVariable @Positive Long id) {
        return servicio.inscripciones(id);
    }
    @PutMapping("/socios/{id}/baja")
    public SocioResponse bajaSocio(@PathVariable @Positive Long id) { return servicio.bajaSocio(id); }
    @PostMapping(value = "/inscripciones", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InscripcionResponse> inscribir(@Valid @RequestBody InscripcionFormulario formulario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(servicio.inscribir(formulario.socioId(), formulario.actividadId()));
    }
    @PutMapping("/inscripciones/{id}/baja")
    public InscripcionResponse baja(@PathVariable @Positive Long id) { return servicio.baja(id); }
}
