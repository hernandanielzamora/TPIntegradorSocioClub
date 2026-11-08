package ar.edu.siglo21.socioclub.repository;

import ar.edu.siglo21.socioclub.model.Inscripcion;
import ar.edu.siglo21.socioclub.model.EstadoInscripcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Consultas de inscripciones; no aplica todavía RN1–RN5 ni controla concurrencia. */
public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    /** Para consultas de vigentes, pasar EstadoInscripcion.ACTIVA; BAJA permite revisar historial. */
    Page<Inscripcion> findBySocioIdAndEstado(Long socioId, EstadoInscripcion estado, Pageable pageable);

    /** Lista paginada por actividad; el estado evita incluir bajas en la nómina vigente. */
    Page<Inscripcion> findByActividadIdAndEstado(Long actividadId, EstadoInscripcion estado, Pageable pageable);

    /** Detecta un vínculo del estado indicado; no impide por sí solo altas concurrentes duplicadas. */
    boolean existsBySocioIdAndActividadIdAndEstado(Long socioId, Long actividadId, EstadoInscripcion estado);

    /** Con ACTIVA cuenta los vínculos vigentes del socio, como apoyo a la futura RN5. */
    long countBySocioIdAndEstado(Long socioId, EstadoInscripcion estado);

    /** JPQL usa entidades y atributos Java; las bajas no ocupan plazas. */
    @Query("""
            select count(i) from Inscripcion i
            where i.actividad.id = :actividadId
              and i.estado = ar.edu.siglo21.socioclub.model.EstadoInscripcion.ACTIVA
            """)
    long contarActivasDeActividad(@Param("actividadId") Long actividadId);
}
