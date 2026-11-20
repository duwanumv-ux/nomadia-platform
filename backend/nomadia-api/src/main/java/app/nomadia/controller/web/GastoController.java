package app.nomadia.controller.web;

import app.nomadia.dto.GastoForm;
import app.nomadia.exception.PresupuestoExcedidoException;
import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.CategoriaGasto;
import app.nomadia.model.Gasto;
import app.nomadia.model.Viaje;
import app.nomadia.service.GastoService;
import app.nomadia.service.RigidezFinancieraService;
import app.nomadia.service.ViajeService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Control de gastos del viaje con la Ley de Rigidez Financiera.
 * Si un gasto supera el saldo, la vista muestra el modal interceptor.
 */
@Controller
public class GastoController {

    private static final String VISTA_GASTOS = "gastos/lista";
    private static final String VISTA_EDICION = "gastos/formulario";

    private final GastoService gastoService;
    private final ViajeService viajeService;
    private final RigidezFinancieraService rigidezFinancieraService;

    public GastoController(GastoService gastoService, ViajeService viajeService,
                           RigidezFinancieraService rigidezFinancieraService) {
        this.gastoService = gastoService;
        this.viajeService = viajeService;
        this.rigidezFinancieraService = rigidezFinancieraService;
    }

    @ModelAttribute("categorias")
    public CategoriaGasto[] cargarCategorias() {
        return CategoriaGasto.values();
    }

    /**
     * HU-06 y HU-08: muestra el estado financiero, el formulario y los gastos del viaje.
     *
     * @param viajeId viaje a consultar
     * @param model modelo de la vista
     * @return plantilla gastos/lista.html
     */
    @GetMapping("/viajes/{viajeId}/gastos")
    public String mostrarGastos(@PathVariable("viajeId") Long viajeId, Model model) {
        Viaje viaje = cargarPantallaGastos(viajeId, model);
        GastoForm form = new GastoForm();
        LocalDate hoy = LocalDate.now();
        form.setFecha(viaje.isFechaDentroDelViaje(hoy) ? hoy : viaje.getFechaInicio());
        model.addAttribute("gastoForm", form);
        return VISTA_GASTOS;
    }

    /**
     * HU-06: registra un gasto. Si rompe la Ley de Rigidez Financiera, se muestra el modal.
     *
     * @param viajeId viaje al que se carga el gasto
     * @param form datos del gasto
     * @param resultado errores de validación
     * @param model modelo de la vista
     * @return redirección a la lista de gastos, o la misma vista con errores
     */
    @PostMapping("/viajes/{viajeId}/gastos")
    public String registrarGasto(@PathVariable("viajeId") Long viajeId, @Valid @ModelAttribute("gastoForm") GastoForm form,
                                 BindingResult resultado, Model model) {
        if (!resultado.hasErrors()) {
            try {
                gastoService.registrar(viajeId, form);
                return "redirect:/viajes/" + viajeId + "/gastos?msg=registrado";
            } catch (PresupuestoExcedidoException ex) {
                model.addAttribute("rigidez", ex);
            } catch (ReglaNegocioException ex) {
                ErroresFormulario.registrar(resultado, ex);
            }
        }
        cargarPantallaGastos(viajeId, model);
        return VISTA_GASTOS;
    }

    /**
     * @param gastoId gasto a editar
     * @param model modelo de la vista
     * @return formulario de edición precargado
     */
    @GetMapping("/gastos/{gastoId}/editar")
    public String mostrarEdicion(@PathVariable("gastoId") Long gastoId, Model model) {
        Gasto gasto = gastoService.buscar(gastoId);
        model.addAttribute("gasto", gasto);
        model.addAttribute("gastoForm", GastoForm.desde(gasto));
        return VISTA_EDICION;
    }

    /**
     * HU-07: guarda los cambios de un gasto.
     *
     * @param gastoId gasto a modificar
     * @param form datos del gasto
     * @param resultado errores de validación
     * @param model modelo de la vista
     * @return redirección a la lista de gastos, o el formulario con errores
     */
    @PostMapping("/gastos/{gastoId}")
    public String actualizarGasto(@PathVariable("gastoId") Long gastoId, @Valid @ModelAttribute("gastoForm") GastoForm form,
                                  BindingResult resultado, Model model) {
        Gasto gasto = gastoService.buscar(gastoId);
        if (!resultado.hasErrors()) {
            try {
                gastoService.actualizar(gastoId, form);
                return "redirect:/viajes/" + gasto.getViaje().getId() + "/gastos?msg=actualizado";
            } catch (PresupuestoExcedidoException ex) {
                model.addAttribute("rigidez", ex);
            } catch (ReglaNegocioException ex) {
                ErroresFormulario.registrar(resultado, ex);
            }
        }
        model.addAttribute("gasto", gasto);
        return VISTA_EDICION;
    }

    /**
     * @param gastoId gasto a eliminar
     * @return redirección a la lista de gastos del viaje
     */
    @PostMapping("/gastos/{gastoId}/eliminar")
    public String eliminarGasto(@PathVariable("gastoId") Long gastoId) {
        Long viajeId = gastoService.eliminar(gastoId);
        return "redirect:/viajes/" + viajeId + "/gastos?msg=eliminado";
    }

    private Viaje cargarPantallaGastos(Long viajeId, Model model) {
        Viaje viaje = viajeService.buscar(viajeId);
        model.addAttribute("viaje", viaje);
        model.addAttribute("gastos", gastoService.listarPorViaje(viajeId));
        model.addAttribute("finanzas", rigidezFinancieraService.calcularEstado(viaje));
        return viaje;
    }
}
