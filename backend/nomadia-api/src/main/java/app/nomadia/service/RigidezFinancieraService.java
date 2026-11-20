package app.nomadia.service;

import app.nomadia.dto.EstadoFinanciero;
import app.nomadia.exception.PresupuestoExcedidoException;
import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.Viaje;
import app.nomadia.repository.GastoRepository;
import app.nomadia.util.FormatoMoneda;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aplica la Ley de Rigidez Financiera: ningún gasto puede superar el presupuesto del viaje.
 *
 * <p>Es la primera línea de defensa (capa de aplicación). La segunda es el disparador
 * trg_verifica_presupuesto de PostgreSQL, que protege la regla aunque alguien escriba
 * directamente en la base de datos.</p>
 */
@Service
@Transactional(readOnly = true)
public class RigidezFinancieraService {

    private final GastoRepository gastoRepository;

    public RigidezFinancieraService(GastoRepository gastoRepository) {
        this.gastoRepository = gastoRepository;
    }

    /**
     * @param viaje viaje a evaluar
     * @return presupuesto, ejecutado, saldo y semáforo del viaje
     */
    public EstadoFinanciero calcularEstado(Viaje viaje) {
        BigDecimal ejecutado = gastoRepository.sumarMontosPorViaje(viaje.getId());
        return new EstadoFinanciero(viaje.getPresupuesto(), ejecutado, viaje.getMoneda());
    }

    /**
     * Verifica que un gasto se pueda registrar o modificar sin romper el presupuesto.
     *
     * @param viaje viaje al que pertenece el gasto
     * @param montoPropuesto valor nuevo del gasto
     * @param gastoIdExcluido id del gasto que se edita (para no contarlo dos veces), o null si es nuevo
     * @throws ReglaNegocioException si el viaje no está activo
     * @throws PresupuestoExcedidoException si el gasto supera el saldo disponible
     */
    public void validarGasto(Viaje viaje, BigDecimal montoPropuesto, Long gastoIdExcluido) {
        if (!viaje.isActivo()) {
            throw new ReglaNegocioException(null, "Solo los viajes activos admiten gastos. Este viaje está "
                    + viaje.getEstado().getEtiqueta().toLowerCase() + ".");
        }
        BigDecimal ejecutado = gastoIdExcluido == null
                ? gastoRepository.sumarMontosPorViaje(viaje.getId())
                : gastoRepository.sumarMontosPorViajeExcluyendo(viaje.getId(), gastoIdExcluido);
        BigDecimal saldoDisponible = viaje.getPresupuesto().subtract(ejecutado);
        if (montoPropuesto.compareTo(saldoDisponible) > 0) {
            throw new PresupuestoExcedidoException(saldoDisponible.max(BigDecimal.ZERO), montoPropuesto);
        }
    }

    /**
     * Impide bajar el presupuesto por debajo de lo que ya se gastó.
     *
     * @param viaje viaje que se está editando
     * @param nuevoPresupuesto presupuesto propuesto en el formulario
     * @throws ReglaNegocioException si el nuevo presupuesto es menor que lo ejecutado
     */
    public void validarNuevoPresupuesto(Viaje viaje, BigDecimal nuevoPresupuesto) {
        BigDecimal ejecutado = gastoRepository.sumarMontosPorViaje(viaje.getId());
        if (nuevoPresupuesto.compareTo(ejecutado) < 0) {
            throw new ReglaNegocioException("presupuesto", "El presupuesto no puede ser menor que lo ya ejecutado ("
                    + FormatoMoneda.formatear(ejecutado, viaje.getMoneda()) + ").");
        }
    }
}
