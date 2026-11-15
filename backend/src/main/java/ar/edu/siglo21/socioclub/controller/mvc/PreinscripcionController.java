package ar.edu.siglo21.socioclub.controller.mvc;

import ar.edu.siglo21.socioclub.service.PreinscripcionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PreinscripcionController {
    private final PreinscripcionService servicio;
    public PreinscripcionController(PreinscripcionService servicio) { this.servicio = servicio; }
    @GetMapping("/secretaria/preinscripciones")
    public String listar(@RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("pagina", servicio.listar(pagina));
        return "secretaria/preinscripciones";
    }
}
