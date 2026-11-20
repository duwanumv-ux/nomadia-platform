package app.nomadia.service;

import app.nomadia.dto.ViajeForm;
import app.nomadia.exception.RecursoNoEncontradoException;
import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Viaje;
import app.nomadia.repository.ViajeRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso del viaje: planificar, consultar, editar y eliminar.
 */
@Service
@Transactional
public class ViajeService {

    /** Duración máxima de un viaje en días (incluye llegada y regreso). */
    public static final int MAX_DIAS_VIAJE = 30;

    private static final ZoneId ZONA_COLOMBIA = ZoneId.of("America/Bogota");

    private final ViajeRepository viajeRepository;
    private final CatalogoService catalogoService;
    private final ItinerarioService itinerarioService;
    private final RigidezFinancieraService rigidezFinancieraService;

    public ViajeService(ViajeRepository viajeRepository, CatalogoService catalogoService,
                        ItinerarioService itinerarioService, RigidezFinancieraService rigidezFinancieraService) {
        this.viajeRepository = viajeRepository;
        this.catalogoService = catalogoService;
        this.itinerarioService = itinerarioService;
        this.rigidezFinancieraService = rigidezFinancieraService;
    }

    /**
     * @param estado filtro opcional; si es null se listan todos
     * @return viajes del más reciente al más antiguo
     */
    @Transactional(readOnly = true)
    public List<Viaje> listar(EstadoViaje estado) {
        return estado == null
                ? viajeRepository.findAllByOrderByFechaInicioDesc()
                : viajeRepository.findByEstadoOrderByFechaInicioDesc(estado);
    }

    /**
     * @param viajeId identificador del viaje
     * @return el viaje
     * @throws RecursoNoEncontradoException si no existe
     */
    @Transactional(readOnly = true)
    public Viaje buscar(Long viajeId) {
        return viajeRepository.findById(viajeId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Viaje", viajeId));
    }

    /**
     * HU-01: planifica un viaje nuevo y genera su itinerario día por día.
     *
     * @param form datos validados del formulario
     * @return el viaje creado
     */
    public Viaje crear(ViajeForm form) {
        validarFechas(form, true);
        Viaje viaje = new Viaje();
        aplicarDatos(viaje, form);
        viaje.setEstado(EstadoViaje.ACTIVO);
        Viaje guardado = viajeRepository.save(viaje);
        itinerarioService.generarDias(guardado);
        return guardado;
    }

    /**
     * HU-03: actualiza un viaje. Si cambian las fechas, el itinerario se genera de nuevo.
     *
     * @param viajeId viaje a modificar
     * @param form datos validados del formulario
     * @return el viaje actualizado
     */
    public Viaje actualizar(Long viajeId, ViajeForm form) {
        Viaje viaje = buscar(viajeId);
        validarFechas(form, false);
        validarCambiosFinancieros(viaje, form);
        boolean hasFechasNuevas = !viaje.getFechaInicio().equals(form.getFechaInicio())
                || !viaje.getFechaFin().equals(form.getFechaFin());
        aplicarDatos(viaje, form);
        viaje.setEstado(form.getEstado() == null ? viaje.getEstado() : form.getEstado());
        Viaje guardado = viajeRepository.saveAndFlush(viaje);
        if (hasFechasNuevas) {
            itinerarioService.regenerarDias(guardado);
        }
        return guardado;
    }

    /**
     * HU-04: elimina el viaje; la base de datos borra en cascada su itinerario y sus gastos.
     *
     * @param viajeId viaje a eliminar
     */
    public void eliminar(Long viajeId) {
        viajeRepository.delete(buscar(viajeId));
    }

    private void aplicarDatos(Viaje viaje, ViajeForm form) {
        viaje.setUsuario(catalogoService.buscarUsuario(form.getUsuarioId()));
        viaje.setDestino(catalogoService.buscarDestino(form.getDestinoId()));
        viaje.setFechaInicio(form.getFechaInicio());
        viaje.setFechaFin(form.getFechaFin());
        viaje.setPresupuesto(form.getPresupuesto());
        viaje.setMoneda(form.getMoneda());
    }

    private void validarFechas(ViajeForm form, boolean isNuevo) {
        LocalDate hoy = LocalDate.now(ZONA_COLOMBIA);
        if (isNuevo && form.getFechaInicio().isBefore(hoy)) {
            throw new ReglaNegocioException("fechaInicio", "La fecha de inicio no puede estar en el pasado.");
        }
        if (form.getFechaFin().isBefore(form.getFechaInicio())) {
            throw new ReglaNegocioException("fechaFin", "La fecha de regreso no puede ser anterior a la de inicio.");
        }
        long dias = ChronoUnit.DAYS.between(form.getFechaInicio(), form.getFechaFin()) + 1;
        if (dias > MAX_DIAS_VIAJE) {
            throw new ReglaNegocioException("fechaFin", "Un viaje puede durar máximo " + MAX_DIAS_VIAJE + " días.");
        }
    }

    private void validarCambiosFinancieros(Viaje viaje, ViajeForm form) {
        rigidezFinancieraService.validarNuevoPresupuesto(viaje, form.getPresupuesto());
        boolean hasGastos = rigidezFinancieraService.calcularEstado(viaje).getEjecutado().signum() > 0;
        if (hasGastos && viaje.getMoneda() != form.getMoneda()) {
            throw new ReglaNegocioException("moneda", "No se puede cambiar la moneda de un viaje que ya tiene gastos.");
        }
    }
}
