package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.Socio;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistencia de socios; será utilizada por servicios compartidos entre MVC y REST. */
public interface SocioRepository extends JpaRepository<Socio, Long> {
    /** Serializa las altas de un mismo socio para proteger RN2 y RN5. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Socio s where s.id = :id")
    Optional<Socio> buscarParaInscribir(@Param("id") Long id);

    /** El DNI único permite devolver cero o un socio. */
    Optional<Socio> findByDni(String dni);

    /** Busca una parte del apellido sin distinguir mayúsculas; Pageable limita y ordena. */
    Page<Socio> findByApellidoContainingIgnoreCase(String apellido, Pageable pageable);
}
