package ar.edu.siglo21.socioclub.controller.mvc;

import ar.edu.siglo21.socioclub.exception.RecursoNoEncontradoException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Mensajes públicos seguros: nunca mostrar SQL, contraseñas o stack traces en las vistas. */
@ControllerAdvice(assignableTypes = SecretariaController.class)
public class ErroresSecretariaController {
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String noEncontrado(RecursoNoEncontradoException e, Model model, HttpServletResponse respuesta) {
        respuesta.setStatus(404);
        model.addAttribute("mensaje", e.getMessage());
        return "secretaria/error";
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String entradaInvalida(Model model, HttpServletResponse respuesta) {
        respuesta.setStatus(400);
        model.addAttribute("mensaje", "La dirección o los filtros contienen un valor inválido");
        return "secretaria/error";
    }
    @ExceptionHandler(DataAccessException.class)
    public String persistencia(Model model, HttpServletResponse respuesta) {
        respuesta.setStatus(503);
        model.addAttribute("mensaje", "No se pudo completar la operación. Intentá nuevamente en unos momentos");
        return "secretaria/error";
    }
}
