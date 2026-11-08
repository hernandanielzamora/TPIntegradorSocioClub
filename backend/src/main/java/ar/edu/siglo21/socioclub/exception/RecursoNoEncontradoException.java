package ar.edu.siglo21.socioclub.exception;

/** Diferencia una referencia inexistente de un rechazo por reglas de inscripción. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " no encontrado: " + id);
    }
}
