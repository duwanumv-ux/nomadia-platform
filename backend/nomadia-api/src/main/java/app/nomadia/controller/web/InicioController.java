package app.nomadia.controller.web;

import app.nomadia.dto.ViajeForm;
import app.nomadia.model.Viaje;
import app.nomadia.service.CatalogoService;
import app.nomadia.service.ViajeService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Pantalla 1 del prototipo: "Tu próximo destino está más cerca de lo que imaginas".
 */
@Controller
public class InicioController {

    private static final int MAX_VIAJES_RECIENTES = 3;

    private final CatalogoService catalogoService;
    private final ViajeService viajeService;

    public InicioController(CatalogoService catalogoService, ViajeService viajeService) {
        this.catalogoService = catalogoService;
        this.viajeService = viajeService;
    }

    /**
     * Muestra el formulario rápido de planificación y los últimos planes.
     *
     * @param model modelo de la vista
     * @return plantilla index.html
     */
    @GetMapping("/")
    public String mostrarInicio(Model model) {
        List<Viaje> viajes = viajeService.listar(null);
        model.addAttribute("destinos", catalogoService.listarDestinos());
        model.addAttribute("viajeForm", new ViajeForm());
        model.addAttribute("viajesRecientes", viajes.subList(0, Math.min(MAX_VIAJES_RECIENTES, viajes.size())));
        return "index";
    }
}
