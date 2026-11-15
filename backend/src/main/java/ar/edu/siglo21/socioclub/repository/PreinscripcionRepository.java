package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.Preinscripcion;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;

public interface PreinscripcionRepository extends JpaRepository<Preinscripcion, Long> {
    @Override
    @EntityGraph(attributePaths = "actividad")
    Page<Preinscripcion> findAll(Pageable pageable);
    boolean existsByActividadId(Long actividadId);
}
