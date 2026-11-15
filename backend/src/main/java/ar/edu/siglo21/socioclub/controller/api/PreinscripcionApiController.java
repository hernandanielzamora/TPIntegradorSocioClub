package ar.edu.siglo21.socioclub.controller.api;

import ar.edu.siglo21.socioclub.dto.PreinscripcionDtos.*;
import ar.edu.siglo21.socioclub.service.PreinscripcionService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/** RF3, adicional a los nueve endpoints mínimos del Anexo A. */
@RestController
@RequestMapping(value = "/api/preinscripciones", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreinscripcionApiController {
    private final PreinscripcionService servicio;
    public PreinscripcionApiController(PreinscripcionService servicio) { this.servicio = servicio; }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Recepcion> recibir(@Valid @RequestBody Solicitud datos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio.recibir(datos));
    }
}
