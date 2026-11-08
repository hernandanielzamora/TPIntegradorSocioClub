package ar.edu.siglo21.socioclub.controller.mvc;

import ar.edu.siglo21.socioclub.dto.SecretariaDtos.*;
import ar.edu.siglo21.socioclub.exception.*;
import ar.edu.siglo21.socioclub.model.*;
import ar.edu.siglo21.socioclub.service.*;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Adapta formularios y vistas; las reglas y el acceso a persistencia permanecen en servicios. */
@Controller
@RequestMapping("/secretaria")
public class SecretariaController {
    private final SocioService socios;
    private final ActividadService actividades;
    private final InscripcionService inscripciones;
    private final ConsultaSecretariaService consultas;
    public SecretariaController(SocioService socios, ActividadService actividades,
            InscripcionService inscripciones, ConsultaSecretariaService consultas) {
        this.socios = socios; this.actividades = actividades;
        this.inscripciones = inscripciones; this.consultas = consultas;
    }

    @ModelAttribute("estadosCuota") public EstadoCuota[] estadosCuota() { return EstadoCuota.values(); }
    @ModelAttribute("estadosInscripcion") public EstadoInscripcion[] estadosInscripcion() { return EstadoInscripcion.values(); }

    @GetMapping({"", "/"})
    public String panel(Model model) {
        model.addAttribute("resumen", consultas.resumen());
        return "secretaria/inicio";
    }

    @GetMapping("/socios")
    public String socios(@RequestParam(defaultValue = "") String apellido,
            @RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("pagina", socios.listar(apellido, pagina));
        model.addAttribute("apellido", apellido);
        return "secretaria/socios";
    }

    @GetMapping("/socios/nuevo")
    public String nuevoSocio(Model model) {
        model.addAttribute("formulario", new SocioFormulario("", "", "", null, "", "", EstadoCuota.AL_DIA));
        return formularioSocio(null, model);
    }

    @GetMapping("/socios/{id}/editar")
    public String editarSocio(@PathVariable Long id, Model model) {
        model.addAttribute("formulario", socios.obtener(id));
        return formularioSocio(id, model);
    }

    @PostMapping({"/socios", "/socios/{id}"})
    public String guardarSocio(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("formulario") SocioFormulario formulario,
            BindingResult errores, Model model, RedirectAttributes redirect) {
        if (!errores.hasErrors()) {
            try { socios.guardar(id, formulario); }
            catch (ValidacionSecretariaException e) { errores.reject("conflicto", e.getMessage()); }
            catch (DataIntegrityViolationException e) { errores.reject("integridad", "No se pudo guardar: revisá el DNI y los datos ingresados"); }
        }
        if (errores.hasErrors()) { return formularioSocio(id, model); }
        redirect.addFlashAttribute("exito", "Socio guardado correctamente");
        return "redirect:/secretaria/socios";
    }

    private String formularioSocio(Long id, Model model) {
        model.addAttribute("id", id);
        return "secretaria/socio-formulario";
    }

    @GetMapping("/actividades")
    public String actividades(@RequestParam(defaultValue = "") String disciplina,
            @RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("pagina", actividades.listar(disciplina, pagina));
        model.addAttribute("disciplina", disciplina);
        return "secretaria/actividades";
    }

    @GetMapping("/actividades/nueva")
    public String nuevaActividad(Model model) {
        model.addAttribute("formulario", new ActividadFormulario("", "", "", "", "", null, null, null, null));
        return formularioActividad(null, model);
    }

    @GetMapping("/actividades/{id}/editar")
    public String editarActividad(@PathVariable Long id, Model model) {
        model.addAttribute("formulario", actividades.obtener(id));
        return formularioActividad(id, model);
    }

    @PostMapping({"/actividades", "/actividades/{id}"})
    public String guardarActividad(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("formulario") ActividadFormulario formulario,
            BindingResult errores, Model model, RedirectAttributes redirect) {
        if (!errores.hasErrors()) {
            try { actividades.guardar(id, formulario); }
            catch (ValidacionSecretariaException e) { errores.reject("conflicto", e.getMessage()); }
            catch (DataIntegrityViolationException e) { errores.reject("integridad", "No se pudo guardar la actividad; revisá sus datos"); }
        }
        if (errores.hasErrors()) { return formularioActividad(id, model); }
        redirect.addFlashAttribute("exito", "Actividad guardada correctamente");
        return "redirect:/secretaria/actividades";
    }

    private String formularioActividad(Long id, Model model) {
        model.addAttribute("id", id);
        return "secretaria/actividad-formulario";
    }

    @PostMapping("/actividades/{id}/eliminar")
    public String eliminarActividad(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            actividades.eliminar(id);
            redirect.addFlashAttribute("exito", "Actividad eliminada");
        } catch (ValidacionSecretariaException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("error", "No se puede eliminar una actividad con historial relacionado");
        }
        return "redirect:/secretaria/actividades";
    }

    @GetMapping("/inscripciones")
    public String inscripciones(@RequestParam(required = false) Long actividadId,
            @RequestParam(required = false) EstadoInscripcion estado,
            @RequestParam(defaultValue = "0") int pagina, Model model) {
        model.addAttribute("pagina", consultas.listar(actividadId, estado, pagina));
        model.addAttribute("actividadId", actividadId);
        model.addAttribute("estado", estado);
        model.addAttribute("actividades", actividades.opciones());
        return "secretaria/inscripciones";
    }

    @GetMapping("/inscripciones/nueva")
    public String nuevaInscripcion(Model model) {
        model.addAttribute("formulario", new InscripcionFormulario(null, null));
        return formularioInscripcion(model);
    }

    @PostMapping("/inscripciones")
    public String inscribir(@Valid @ModelAttribute("formulario") InscripcionFormulario formulario,
            BindingResult errores, Model model, RedirectAttributes redirect) {
        if (!errores.hasErrors()) {
            try { inscripciones.inscribir(formulario.socioId(), formulario.actividadId()); }
            catch (ReglaNegocioException | RecursoNoEncontradoException e) { errores.reject("inscripcion", e.getMessage()); }
        }
        if (errores.hasErrors()) { return formularioInscripcion(model); }
        redirect.addFlashAttribute("exito", "Inscripción realizada correctamente");
        return "redirect:/secretaria/inscripciones";
    }

    private String formularioInscripcion(Model model) {
        model.addAttribute("socios", socios.opciones());
        model.addAttribute("actividades", actividades.opciones());
        return "secretaria/inscripcion-formulario";
    }

    @PostMapping("/inscripciones/{id}/baja")
    public String baja(@PathVariable Long id, RedirectAttributes redirect) {
        inscripciones.darDeBaja(id);
        redirect.addFlashAttribute("exito", "Inscripción dada de baja; el historial se conserva");
        return "redirect:/secretaria/inscripciones";
    }
}
