package ar.edu.siglo21.socioclub.exception;

/** Mejora voluntaria independiente de RN1–RN5. */
public class SocioInactivoException extends ValidacionSecretariaException {
    public SocioInactivoException() { super("El socio está inactivo y no puede realizar nuevas inscripciones"); }
}
