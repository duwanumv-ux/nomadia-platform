package app.nomadia.service;

import app.nomadia.dto.GastoForm;
import app.nomadia.exception.RecursoNoEncontradoException;
import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.Gasto;
import app.nomadia.model.Viaje;
import app.nomadia.repository.GastoRepository;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Casos de uso de los gastos de un viaje, sometidos a la Ley de Rigidez Financiera.
 */
@Service
@Transactional
public class GastoService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final GastoRepository gastoRepository;
    private final ViajeService viajeService;
    private final RigidezFinancieraService rigidezFinancieraService;

    public GastoService(GastoRepository gastoRepository, ViajeService viajeService,
                        RigidezFinancieraService rigidezFinancieraService) {
        this.gastoRepository = gastoRepository;
        this.viajeService = viajeService;
        this.rigidezFinancieraService = rigidezFinancieraService;
    }

    @Transactional(readOnly = true)
    public List<Gasto> listarPorViaje(Long viajeId) {
        return gastoRepository.findByViajeIdOrderByFechaDescIdDesc(viajeId);
    }

    /**
     * @param gastoId identificador del gasto
     * @return el gasto
     * @throws RecursoNoEncontradoException si no existe
     */
    @Transactional(readOnly = true)
    public Gasto buscar(Long gastoId) {
        return gastoRepository.findById(gastoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Gasto", gastoId));
    }

    /**
     * HU-06: registra un gasto si cabe dentro del presupuesto del viaje.
     *
     * @param viajeId viaje al que se carga el gasto
     * @param form datos validados del gasto
     * @return el gasto guardado
     */
    public Gasto registrar(Long viajeId, GastoForm form) {
        Viaje viaje = viajeService.buscar(viajeId);
        validarFecha(viaje, form);
        rigidezFinancieraService.validarGasto(viaje, form.getMonto(), null);
        Gasto gasto = new Gasto();
        gasto.setViaje(viaje);
        aplicarDatos(gasto, form);
        return guardarConControl(gasto);
    }

    /**
     * HU-07: modifica un gasto; el monto nuevo también se somete a la Ley de Rigidez Financiera.
     *
     * @param gastoId gasto a modificar
     * @param form datos validados
     * @return el gasto actualizado
     */
    public Gasto actualizar(Long gastoId, GastoForm form) {
        Gasto gasto = buscar(gastoId);
        validarFecha(gasto.getViaje(), form);
        rigidezFinancieraService.validarGasto(gasto.getViaje(), form.getMonto(), gastoId);
        aplicarDatos(gasto, form);
        return guardarConControl(gasto);
    }

    /**
     * @param gastoId gasto a eliminar
     * @return id del viaje al que pertenecía
     */
    public Long eliminar(Long gastoId) {
        Gasto gasto = buscar(gastoId);
        Long viajeId = gasto.getViaje().getId();
        gastoRepository.delete(gasto);
        return viajeId;
    }

    private void aplicarDatos(Gasto gasto, GastoForm form) {
        gasto.setConcepto(form.getConcepto().trim());
        gasto.setMonto(form.getMonto());
        gasto.setFecha(form.getFecha());
        gasto.setCategoria(form.getCategoria());
    }

    private void validarFecha(Viaje viaje, GastoForm form) {
        if (!viaje.isFechaDentroDelViaje(form.getFecha())) {
            throw new ReglaNegocioException("fecha", "La fecha del gasto debe estar entre el "
                    + viaje.getFechaInicio().format(FORMATO_FECHA) + " y el "
                    + viaje.getFechaFin().format(FORMATO_FECHA) + ".");
        }
    }

    /**
     * Guarda el gasto y traduce el rechazo del disparador trg_verifica_presupuesto
     * (caso de dos gastos registrados al mismo tiempo) a un mensaje de negocio.
     */
    private Gasto guardarConControl(Gasto gasto) {
        try {
            return gastoRepository.saveAndFlush(gasto);
        } catch (DataIntegrityViolationException ex) {
            throw new ReglaNegocioException("monto", "Ley de Rigidez Financiera: la base de datos rechazó el gasto "
                    + "porque supera el presupuesto. Actualice la página y revise el saldo.");
        }
    }
}
