package app.nomadia.exception;

import java.math.BigDecimal;

/**
 * Ley de Rigidez Financiera: el gasto propuesto supera el saldo disponible del viaje.
 */
public class PresupuestoExcedidoException extends ReglaNegocioException {

    private static final long serialVersionUID = 1L;

    private final BigDecimal saldoDisponible;
    private final BigDecimal montoPropuesto;

    /**
     * @param saldoDisponible lo que aún se puede gastar en el viaje
     * @param montoPropuesto valor del gasto que se intentó registrar
     */
    public PresupuestoExcedidoException(BigDecimal saldoDisponible, BigDecimal montoPropuesto) {
        super("monto", "Ley de Rigidez Financiera: el gasto supera el saldo disponible del viaje.");
        this.saldoDisponible = saldoDisponible;
        this.montoPropuesto = montoPropuesto;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public BigDecimal getMontoPropuesto() {
        return montoPropuesto;
    }
}
