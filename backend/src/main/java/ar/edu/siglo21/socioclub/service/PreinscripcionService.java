package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.PreinscripcionDtos.*;
import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socioclub.model.Preinscripcion;
import ar.edu.siglo21.socioclub.repository.*;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDateTime;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class PreinscripcionService {
    private final PreinscripcionRepository solicitudes;
    private final ActividadRepository actividades;
    private final Clock reloj;
    public PreinscripcionService(PreinscripcionRepository solicitudes, ActividadRepository actividades, Clock reloj) {
        this.solicitudes = solicitudes; this.actividades = actividades; this.reloj = reloj;
    }
    @Transactional
    public Recepcion recibir(@Valid Solicitud datos) {
        var actividad = actividades.findById(datos.actividadId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", datos.actividadId()));
        var solicitud = new Preinscripcion(datos.nombre().strip(), datos.apellido().strip(),
                datos.dni(), datos.email().strip(), actividad, LocalDateTime.now(reloj));
        var guardada = solicitudes.saveAndFlush(solicitud);
        return new Recepcion(guardada.getId(), guardada.getFechaRecepcion());
    }
    public Page<Vista> listar(int pagina) {
        return solicitudes.findAll(PageRequest.of(Math.max(0, pagina), 10,
                Sort.by(Sort.Direction.DESC, "fechaRecepcion", "id")))
                .map(p -> new Vista(p.getId(), p.getNombre(), p.getApellido(), p.getDni(), p.getEmail(),
                        p.getActividad().getNombre(), p.getFechaRecepcion()));
    }
}
