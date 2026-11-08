package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.Actividad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso al catálogo de actividades, sin reglas de inscripción. */
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    /** Filtra por disciplina y devuelve una página del catálogo. */
    Page<Actividad> findByDisciplina(String disciplina, Pageable pageable);
}
