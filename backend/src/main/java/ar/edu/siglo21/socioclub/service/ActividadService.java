package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.Actividad;
import ar.edu.siglo21.socioclub.repository.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class ActividadService {
    private final ActividadRepository actividades;
    private final InscripcionRepository inscripciones;
    public ActividadService(ActividadRepository actividades, InscripcionRepository inscripciones) {
        this.actividades = actividades; this.inscripciones = inscripciones;
    }

    public Page<ActividadVista> listar(String disciplina, int pagina) {
        var pageable = PageRequest.of(Math.max(0, pagina), 10, Sort.by("nombre", "id"));
        var resultado = disciplina.isBlank() ? actividades.findAll(pageable)
                : actividades.findByDisciplina(disciplina, pageable);
        return resultado.map(a -> {
            long activas = inscripciones.contarActivasDeActividad(a.getId());
            return new ActividadVista(a.getId(), ActividadFormulario.desde(a), activas,
                    Math.max(0, a.getCupoMaximo() - activas));
        });
    }

    public List<Opcion> opciones() {
        return actividades.findAll(Sort.by("nombre", "id")).stream()
                .map(a -> new Opcion(a.getId(), a.getNombre() + " · " + a.getDias() + " " + a.getHorario())).toList();
    }

    public ActividadFormulario obtener(Long id) {
        return ActividadFormulario.desde(actividades.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", id)));
    }

    @Transactional
    public void guardar(Long id, @Valid ActividadFormulario f) {
        Actividad a = id == null ? new Actividad() : bloqueada(id);
        if (id != null && f.cupoMaximo() < inscripciones.contarActivasDeActividad(id)) {
            throw new ValidacionSecretariaException("El cupo no puede ser menor que la cantidad de inscripciones activas");
        }
        a.setNombre(f.nombre()); a.setDisciplina(f.disciplina()); a.setProfesor(f.profesor());
        a.setDias(f.dias()); a.setHorario(f.horario()); a.setEdadMinima(f.edadMinima());
        a.setEdadMaxima(f.edadMaxima()); a.setCupoMaximo(f.cupoMaximo()); a.setCuotaMensual(f.cuotaMensual());
        actividades.saveAndFlush(a);
    }

    @Transactional
    public void eliminar(Long id) {
        Actividad a = bloqueada(id);
        if (inscripciones.contarActivasDeActividad(id) > 0) {
            throw new ValidacionSecretariaException("No se puede eliminar una actividad con inscripciones activas");
        }
        // La FK y el historial de bajas se preservan; no se eliminan inscripciones en cascada.
        if (inscripciones.existsByActividadId(id)) {
            throw new ValidacionSecretariaException("La actividad tiene historial de inscripciones y debe conservarse");
        }
        actividades.delete(a);
        actividades.flush();
    }

    private Actividad bloqueada(Long id) {
        return actividades.buscarParaInscribir(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", id));
    }
}
