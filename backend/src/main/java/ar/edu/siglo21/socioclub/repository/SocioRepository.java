package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.Socio;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistencia de socios; será utilizada por servicios compartidos entre MVC y REST. */
public interface SocioRepository extends JpaRepository<Socio, Long> {
    /** El DNI único permite devolver cero o un socio. */
    Optional<Socio> findByDni(String dni);

    /** Busca una parte del apellido sin distinguir mayúsculas; Pageable limita y ordena. */
    Page<Socio> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
}
