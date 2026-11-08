package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.repository.*;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class SocioService {
    private final SocioRepository socios;
    private final ActividadRepository actividades;
    private final InscripcionRepository inscripciones;
    public SocioService(SocioRepository socios, ActividadRepository actividades, InscripcionRepository inscripciones) {
        this.socios = socios; this.actividades = actividades; this.inscripciones = inscripciones;
    }

    public Page<SocioVista> listar(String apellido, int pagina) {
        return socios.findByApellidoContainingIgnoreCase(apellido,
                PageRequest.of(Math.max(0, pagina), 10, Sort.by("apellido", "nombre", "id")))
                .map(s -> new SocioVista(s.getId(), SocioFormulario.desde(s), s.getEstadoSocio()));
    }

    public List<Opcion> opciones() {
        return socios.findAll(Sort.by("apellido", "nombre", "id")).stream()
                .filter(s -> s.getEstadoSocio() == EstadoSocio.ACTIVO)
                .map(s -> new Opcion(s.getId(), s.getApellido() + ", " + s.getNombre() + " · DNI " + s.getDni())).toList();
    }

    public SocioFormulario obtener(Long id) {
        return SocioFormulario.desde(socios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", id)));
    }

    public SocioBajaVista prepararBaja(Long id) {
        Socio s = socios.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Socio", id));
        return new SocioBajaVista(id, s.getApellido() + ", " + s.getNombre(), s.getEstadoSocio(),
                inscripciones.countBySocioIdAndEstado(id, EstadoInscripcion.ACTIVA));
    }

    /** Efecto idempotente y atómico: vigencia y todas las ACTIVAS se confirman o revierten juntas. */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void darDeBaja(Long id) {
        Socio s = socios.buscarParaInscribir(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", id));
        var activas = inscripciones.findBySocioIdAndEstadoOrderByIdDesc(id, EstadoInscripcion.ACTIVA);
        // Mismo protocolo que altas/bajas: socio primero, luego actividades sin ciclos de orden.
        var ids = activas.stream().map(i -> i.getActividad().getId()).distinct().sorted().toList();
        for (Long actividadId : ids) {
            actividades.buscarParaInscribir(actividadId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", actividadId));
        }
        activas.forEach(i -> i.setEstado(EstadoInscripcion.BAJA));
        s.setEstadoSocio(EstadoSocio.INACTIVO);
        // Entidades gestionadas: dirty checking; flush permite detectar fallos dentro de la transacción.
        inscripciones.flush();
        socios.flush();
    }

    @Transactional
    public void guardar(Long id, @Valid SocioFormulario f) {
        Socio s = id == null ? new Socio() : socios.buscarParaInscribir(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", id));
        if (socios.findByDni(f.dni()).filter(otro -> !otro.getId().equals(id)).isPresent()) {
            throw new ValidacionSecretariaException("Ya existe un socio con ese DNI");
        }
        s.setDni(f.dni()); s.setNombre(f.nombre()); s.setApellido(f.apellido());
        s.setFechaNacimiento(f.fechaNacimiento()); s.setEmail(f.email());
        s.setTelefono(f.telefono()); s.setEstadoCuota(f.estadoCuota());
        socios.saveAndFlush(s);
    }
}
