package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.model.EstadoInscripcion;
import ar.edu.siglo21.socioclub.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ConsultaSecretariaService {
    private final SocioRepository socios;
    private final ActividadRepository actividades;
    private final InscripcionRepository inscripciones;
    public ConsultaSecretariaService(SocioRepository socios, ActividadRepository actividades,
            InscripcionRepository inscripciones) {
        this.socios = socios; this.actividades = actividades; this.inscripciones = inscripciones;
    }

    public Resumen resumen() {
        return new Resumen(socios.count(), actividades.count(), inscripciones.countByEstado(EstadoInscripcion.ACTIVA));
    }

    public Page<InscripcionVista> listar(Long actividadId, EstadoInscripcion estado, int pagina) {
        return inscripciones.buscarSecretaria(actividadId, estado,
                PageRequest.of(Math.max(0, pagina), 10, Sort.by(Sort.Direction.DESC, "id")))
                .map(i -> new InscripcionVista(i.getId(),
                        i.getSocio().getApellido() + ", " + i.getSocio().getNombre() + " · DNI " + i.getSocio().getDni(),
                        i.getActividad().getNombre(), i.getFechaInscripcion(), i.getEstado()));
    }
}
