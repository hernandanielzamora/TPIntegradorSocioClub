package ar.edu.siglo21.socioclub.service;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.Socio;
import ar.edu.siglo21.socioclub.repository.SocioRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Transactional(readOnly = true)
public class SocioService {
    private final SocioRepository socios;
    public SocioService(SocioRepository socios) { this.socios = socios; }

    public Page<SocioVista> listar(String apellido, int pagina) {
        return socios.findByApellidoContainingIgnoreCase(apellido,
                PageRequest.of(Math.max(0, pagina), 10, Sort.by("apellido", "nombre", "id")))
                .map(s -> new SocioVista(s.getId(), SocioFormulario.desde(s)));
    }

    public List<Opcion> opciones() {
        return socios.findAll(Sort.by("apellido", "nombre", "id")).stream()
                .map(s -> new Opcion(s.getId(), s.getApellido() + ", " + s.getNombre() + " · DNI " + s.getDni())).toList();
    }

    public SocioFormulario obtener(Long id) {
        return SocioFormulario.desde(socios.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", id)));
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
