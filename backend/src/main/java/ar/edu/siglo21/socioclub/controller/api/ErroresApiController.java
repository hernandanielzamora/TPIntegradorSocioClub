package ar.edu.siglo21.socioclub.controller.api;

import ar.edu.siglo21.socioclub.dto.ApiDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.*;

/** Respuestas JSON seguras limitadas al controlador REST: no cambia los errores HTML de MVC. */
@RestControllerAdvice(assignableTypes = ApiController.class)
public class ErroresApiController {
    @ExceptionHandler(SocioInactivoException.class)
    public ResponseEntity<ErrorResponse> inactivo() {
        return ResponseEntity.status(409).body(new ErrorResponse(new SocioInactivoException().getMessage(),
                null, List.of(), "SOCIO_INACTIVO"));
    }
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponse> regla(ReglaNegocioException e) {
        String regla = switch (e.getCodigo()) {
            case SIN_CUPO -> "RN1";
            case INSCRIPCION_DUPLICADA -> "RN2";
            case EDAD_FUERA_DE_RANGO -> "RN3";
            case SOCIO_MOROSO -> "RN4";
            case MAXIMO_ACTIVIDADES -> "RN5";
        };
        return ResponseEntity.status(409).body(new ErrorResponse(e.getMessage(), regla, List.of()));
    }
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> noEncontrado() { return error(404, "El recurso solicitado no existe"); }
    @ExceptionHandler(ValidacionSecretariaException.class)
    public ResponseEntity<ErrorResponse> conflicto(ValidacionSecretariaException e) { return error(409, e.getMessage()); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad() { return error(409, "La operación entra en conflicto con los datos existentes"); }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> acceso() { return error(503, "No se pudo completar la operación; intentá nuevamente"); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> formulario(MethodArgumentNotValidException e) {
        List<CampoError> detalles = e.getBindingResult().getFieldErrors().stream()
                .map(f -> new CampoError(f.getField(), f.getDefaultMessage())).toList();
        return ResponseEntity.badRequest().body(new ErrorResponse("Revisá los datos enviados", null, detalles));
    }
    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            HandlerMethodValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> invalido() { return error(400, "El cuerpo, los identificadores o los filtros son inválidos"); }
    private ResponseEntity<ErrorResponse> error(int estado, String mensaje) {
        return ResponseEntity.status(estado).body(new ErrorResponse(mensaje, null, List.of()));
    }
}
