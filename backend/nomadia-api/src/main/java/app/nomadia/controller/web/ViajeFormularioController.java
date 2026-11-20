package app.nomadia.controller.web;

import app.nomadia.dto.ViajeForm;
import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.Destino;
import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Moneda;
import app.nomadia.model.Usuario;
import app.nomadia.model.Viaje;
import app.nomadia.service.CatalogoService;
import app.nomadia.service.ViajeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Pantalla 2 del prototipo: "Planifica tu viaje" (crear y editar).
 * Los catálogos de las listas desplegables se cargan con métodos @ModelAttribute.
 */
@Controller
@RequestMapping("/viajes")
public class ViajeFormularioController {

    private static final String VISTA_FORMULARIO = "viajes/formulario";

    private final ViajeService viajeService;
    private final CatalogoService catalogoService;

    public ViajeFormularioController(ViajeService viajeService, CatalogoService catalogoService) {
        this.viajeService = viajeService;
        this.catalogoService = catalogoService;
    }

    @ModelAttribute("usuarios")
    public List<Usuario> cargarUsuarios() {
        return catalogoService.listarUsuarios();
    }

    @ModelAttribute("destinos")
    public List<Destino> cargarDestinos() {
        return catalogoService.listarDestinos();
    }

    @ModelAttribute("monedas")
    public Moneda[] cargarMonedas() {
        return Moneda.values();
    }

    @ModelAttribute("estados")
    public EstadoViaje[] cargarEstados() {
        return EstadoViaje.values();
    }

    /** Id del viaje en edición (null cuando se crea uno nuevo); la vista lo usa para cambiar títulos y acción. */
    @ModelAttribute("viajeId")
    public Long cargarViajeId(@PathVariable(name = "viajeId", required = false) Long viajeId) {
        return viajeId;
    }

    /**
     * Formulario vacío o precargado desde la pantalla de inicio (parámetros GET).
     *
     * @param form datos recibidos por la URL, si los hay
     * @param resultado errores de conversión (por ejemplo, un presupuesto con letras)
     * @return plantilla del formulario
     */
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(@ModelAttribute("viajeForm") ViajeForm form, BindingResult resultado) {
        return VISTA_FORMULARIO;
    }

    /**
     * HU-01: guarda el viaje y pasa a la pantalla "Creando tu plan de viaje...".
     *
     * @param form datos del formulario
     * @param resultado errores de validación
     * @return redirección a procesando, o el formulario con errores
     */
    @PostMapping
    public String crearViaje(@Valid @ModelAttribute("viajeForm") ViajeForm form, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            Viaje viaje = viajeService.crear(form);
            return "redirect:/viajes/" + viaje.getId() + "/procesando";
        } catch (ReglaNegocioException ex) {
            ErroresFormulario.registrar(resultado, ex);
            return VISTA_FORMULARIO;
        }
    }

    /**
     * @param viajeId viaje a editar
     * @param model modelo de la vista
     * @return formulario precargado con los datos actuales
     */
    @GetMapping("/{viajeId}/editar")
    public String mostrarFormularioEdicion(@PathVariable("viajeId") Long viajeId, Model model) {
        model.addAttribute("viajeForm", ViajeForm.desde(viajeService.buscar(viajeId)));
        return VISTA_FORMULARIO;
    }

    /**
     * HU-03: guarda los cambios del viaje.
     *
     * @param viajeId viaje a modificar
     * @param form datos del formulario
     * @param resultado errores de validación
     * @return redirección al resumen, o el formulario con errores
     */
    @PostMapping("/{viajeId}")
    public String actualizarViaje(@PathVariable("viajeId") Long viajeId,
                                  @Valid @ModelAttribute("viajeForm") ViajeForm form, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return VISTA_FORMULARIO;
        }
        try {
            viajeService.actualizar(viajeId, form);
            return "redirect:/viajes/" + viajeId + "?msg=actualizado";
        } catch (ReglaNegocioException ex) {
            ErroresFormulario.registrar(resultado, ex);
            return VISTA_FORMULARIO;
        }
    }
}
