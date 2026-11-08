package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.ApiDtos.*;
import ar.edu.siglo21.socioclub.dto.SecretariaDtos.ActividadFormulario;
import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

/** Adapta a JSON dentro de la transacción y delega escrituras/reglas a servicios compartidos. */
@Service
@Transactional(readOnly = true)
public class ApiConsultaService {
    private final SocioRepository socios;
    private final ActividadRepository actividades;
    private final InscripcionRepository inscripciones;
    private final ActividadService administracion;
    private final InscripcionService altas;
    private final SocioService gestionSocios;
    public ApiConsultaService(SocioRepository socios, ActividadRepository actividades,
            InscripcionRepository inscripciones, ActividadService administracion, InscripcionService altas,
            SocioService gestionSocios) {
        this.socios = socios; this.actividades = actividades; this.inscripciones = inscripciones;
        this.administracion = administracion; this.altas = altas;
        this.gestionSocios = gestionSocios;
    }

    public List<ActividadResponse> actividades(String disciplina) {
        // El Anexo A pide un array con todas las actividades, no una página de Spring Data.
        List<Actividad> lista = disciplina == null || disciplina.isBlank()
                ? actividades.findAll(Sort.by("nombre", "id"))
                : actividades.findByDisciplina(disciplina, Sort.by("nombre", "id"));
        return lista.stream().map(this::actividadDto).toList();
    }

    public ActividadResponse actividad(Long id) {
        return actividadDto(actividades.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", id)));
    }

    public SocioResponse socioPorDni(String dni) {
        Socio s = socios.findByDni(dni)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", null));
        return new SocioResponse(s.getId(), s.getDni(), s.getNombre(), s.getApellido(),
                s.getFechaNacimiento(), s.getEstadoCuota(), s.getEstadoSocio());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public SocioResponse bajaSocio(Long id) {
        gestionSocios.darDeBaja(id);
        Socio s = socios.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Socio", id));
        return new SocioResponse(s.getId(), s.getDni(), s.getNombre(), s.getApellido(),
                s.getFechaNacimiento(), s.getEstadoCuota(), s.getEstadoSocio());
    }

    public List<InscripcionResponse> inscripciones(Long socioId) {
        if (!socios.existsById(socioId)) { throw new RecursoNoEncontradoException("Socio", socioId); }
        return inscripciones.findBySocioIdAndEstadoOrderByIdDesc(socioId, EstadoInscripcion.ACTIVA)
                .stream().map(this::inscripcionDto).toList();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ActividadResponse guardarActividad(Long id, ActividadFormulario formulario) {
        Long guardada = administracion.guardar(id, formulario);
        return actividad(guardada);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void eliminarActividad(Long id) { administracion.eliminar(id); }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InscripcionResponse inscribir(Long socioId, Long actividadId) {
        return inscripcionDto(altas.inscribir(socioId, actividadId));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public InscripcionResponse baja(Long id) {
        altas.darDeBaja(id);
        return inscripcionDto(inscripciones.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inscripción", id)));
    }

    private ActividadResponse actividadDto(Actividad a) {
        return new ActividadResponse(a.getId(), a.getNombre(), a.getDisciplina(), a.getProfesor(),
                a.getDias(), a.getHorario(), a.getEdadMinima(), a.getEdadMaxima(), a.getCupoMaximo(),
                a.getCuotaMensual(), Math.max(0, a.getCupoMaximo() - inscripciones.contarActivasDeActividad(a.getId())));
    }

    private InscripcionResponse inscripcionDto(Inscripcion i) {
        return new InscripcionResponse(i.getId(), i.getSocio().getId(), i.getActividad().getId(),
                i.getFechaInscripcion(), i.getEstado(), actividadDto(i.getActividad()));
    }
}
