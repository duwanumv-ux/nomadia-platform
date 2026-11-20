package app.nomadia.controller.web;

import app.nomadia.dto.EstadoFinanciero;
import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Viaje;
import app.nomadia.service.ItinerarioService;
import app.nomadia.service.RigidezFinancieraService;
import app.nomadia.service.ViajeService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Consulta y eliminación de viajes: "Mis planes", "Procesando" y "¡Tu viaje está listo!".
 */
@Controller
@RequestMapping("/viajes")
public class ViajeController {

    private final ViajeService viajeService;
    private final ItinerarioService itinerarioService;
    private final RigidezFinancieraService rigidezFinancieraService;

    public ViajeController(ViajeService viajeService, ItinerarioService itinerarioService,
                           RigidezFinancieraService rigidezFinancieraService) {
        this.viajeService = viajeService;
        this.itinerarioService = itinerarioService;
        this.rigidezFinancieraService = rigidezFinancieraService;
    }

    /**
     * HU-02: lista los viajes, con filtro opcional por estado.
     *
     * @param estado filtro (ACTIVO, FINALIZADO, CANCELADO) o vacío para todos
     * @param model modelo de la vista
     * @return plantilla viajes/lista.html
     */
    @GetMapping
    public String listarViajes(@RequestParam(name = "estado", required = false) EstadoViaje estado, Model model) {
        List<Viaje> viajes = viajeService.listar(estado);
        Map<Long, EstadoFinanciero> finanzas = new HashMap<>();
        viajes.forEach(viaje -> finanzas.put(viaje.getId(), rigidezFinancieraService.calcularEstado(viaje)));
        model.addAttribute("viajes", viajes);
        model.addAttribute("finanzas", finanzas);
        model.addAttribute("estados", EstadoViaje.values());
        model.addAttribute("estadoFiltro", estado);
        return "viajes/lista";
    }

    /**
     * Pantalla 3 del prototipo: animación mientras se arma el plan; redirige sola al resumen.
     *
     * @param viajeId viaje recién creado
     * @param model modelo de la vista
     * @return plantilla viajes/procesando.html
     */
    @GetMapping("/{viajeId}/procesando")
    public String mostrarProcesando(@PathVariable("viajeId") Long viajeId, Model model) {
        model.addAttribute("viaje", viajeService.buscar(viajeId));
        return "viajes/procesando";
    }

    /**
     * Pantalla 4 del prototipo: resumen del viaje con su estado financiero.
     *
     * @param viajeId viaje a mostrar
     * @param model modelo de la vista
     * @return plantilla viajes/detalle.html
     */
    @GetMapping("/{viajeId}")
    public String mostrarDetalle(@PathVariable("viajeId") Long viajeId, Model model) {
        Viaje viaje = viajeService.buscar(viajeId);
        model.addAttribute("viaje", viaje);
        model.addAttribute("finanzas", rigidezFinancieraService.calcularEstado(viaje));
        model.addAttribute("costoPlaneado", itinerarioService.calcularCostoPlaneado(viajeId));
        return "viajes/detalle";
    }

    /**
     * HU-04: elimina el viaje con su itinerario y sus gastos.
     *
     * @param viajeId viaje a eliminar
     * @return redirección a "Mis planes"
     */
    @PostMapping("/{viajeId}/eliminar")
    public String eliminarViaje(@PathVariable("viajeId") Long viajeId) {
        viajeService.eliminar(viajeId);
        return "redirect:/viajes?msg=eliminado";
    }
}
