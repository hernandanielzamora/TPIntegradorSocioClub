package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.Actividad;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso al catálogo de actividades, sin reglas de inscripción. */
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    java.util.List<Actividad> findByDisciplina(String disciplina, org.springframework.data.domain.Sort sort);
    /** Serializa las altas de una actividad para reservar el último cupo sin sobrepasarlo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Actividad a where a.id = :id")
    Optional<Actividad> buscarParaInscribir(@Param("id") Long id);

    /** Filtra por disciplina y devuelve una página del catálogo. */
    Page<Actividad> findByDisciplina(String disciplina, Pageable pageable);
}
