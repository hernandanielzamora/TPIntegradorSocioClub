package ar.edu.siglo21.socioclub.exception;

/** Rechazo esperado del negocio; el código permite identificarlo sin comparar mensajes. */
public class ReglaNegocioException extends RuntimeException {
    public enum Codigo {
        SIN_CUPO, INSCRIPCION_DUPLICADA, EDAD_FUERA_DE_RANGO, SOCIO_MOROSO, MAXIMO_ACTIVIDADES
    }

    private final Codigo codigo;

    public ReglaNegocioException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
