package app.nomadia.controller.web;

import app.nomadia.dto.ActividadForm;
import app.nomadia.service.ItinerarioService;
import app.nomadia.service.ViajeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Pantalla 5 del prototipo: "Tu itinerario", con las actividades de cada día.
 */
@Controller
@RequestMapping("/viajes/{viajeId}")
public class ItinerarioController {

    private static final String VISTA_ITINERARIO = "viajes/itinerario";

    private final ViajeService viajeService;
    private final ItinerarioService itinerarioService;

    public ItinerarioController(ViajeService viajeService, ItinerarioService itinerarioService) {
        this.viajeService = viajeService;
        this.itinerarioService = itinerarioService;
    }

    /**
     * HU-05: muestra el itinerario día por día.
     *
     * @param viajeId viaje a consultar
     * @param model modelo de la vista
     * @return plantilla viajes/itinerario.html
     */
    @GetMapping("/itinerario")
    public String mostrarItinerario(@PathVariable("viajeId") Long viajeId, Model model) {
        cargarItinerario(viajeId, model);
        model.addAttribute("actividadForm", new ActividadForm());
        return VISTA_ITINERARIO;
    }

    /**
     * HU-05: agrega una actividad a un día del itinerario.
     *
     * @param viajeId viaje de la URL
     * @param diaId día de la URL
     * @param form datos de la actividad
     * @param resultado errores de validación
     * @param model modelo para volver a pintar la vista si hay errores
     * @return redirección al itinerario, o la misma vista con errores
     */
    @PostMapping("/itinerario/{diaId}/actividades")
    public String agregarActividad(@PathVariable("viajeId") Long viajeId, @PathVariable("diaId") Long diaId,
                                   @Valid @ModelAttribute("actividadForm") ActividadForm form,
                                   BindingResult resultado, Model model) {
        if (resultado.hasErrors()) {
            cargarItinerario(viajeId, model);
            model.addAttribute("diaConError", diaId);
            return VISTA_ITINERARIO;
        }
        itinerarioService.agregarActividad(viajeId, diaId, form);
        return "redirect:/viajes/" + viajeId + "/itinerario?msg=actividad";
    }

    /**
     * @param viajeId viaje de la URL
     * @param actividadId actividad a eliminar
     * @return redirección al itinerario
     */
    @PostMapping("/actividades/{actividadId}/eliminar")
    public String eliminarActividad(@PathVariable("viajeId") Long viajeId, @PathVariable("actividadId") Long actividadId) {
        Long viajeDeLaActividad = itinerarioService.eliminarActividad(actividadId);
        return "redirect:/viajes/" + viajeDeLaActividad + "/itinerario?msg=actividad-eliminada";
    }

    private void cargarItinerario(Long viajeId, Model model) {
        model.addAttribute("viaje", viajeService.buscar(viajeId));
        model.addAttribute("dias", itinerarioService.listarDias(viajeId));
        model.addAttribute("actividadesPorDia", itinerarioService.agruparActividadesPorDia(viajeId));
        model.addAttribute("costoPlaneado", itinerarioService.calcularCostoPlaneado(viajeId));
    }
}
