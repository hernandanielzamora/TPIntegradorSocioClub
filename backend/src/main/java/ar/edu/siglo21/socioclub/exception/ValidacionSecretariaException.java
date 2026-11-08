package ar.edu.siglo21.socioclub.exception;

/** Conflicto esperado de la administración, distinto de las reglas de alta RN1–RN5. */
public class ValidacionSecretariaException extends RuntimeException {
    public ValidacionSecretariaException(String mensaje) { super(mensaje); }
}
