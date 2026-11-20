package app.nomadia.dto;

import app.nomadia.model.Moneda;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Resumen financiero de un viaje: presupuesto, ejecutado, saldo y semáforo.
 * Equivale a la vista vw_estado_financiero de la base de datos.
 */
public class EstadoFinanciero {

    /** Umbral de alerta: 85 % del presupuesto (manual de estándares, MARGIN_THRESHOLD_WARNING). */
    public static final BigDecimal UMBRAL_ALERTA = new BigDecimal("0.85");

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private final BigDecimal presupuesto;
    private final BigDecimal ejecutado;
    private final Moneda moneda;

    /**
     * @param presupuesto tope del viaje
     * @param ejecutado suma de los gastos registrados
     * @param moneda moneda del viaje
     */
    public EstadoFinanciero(BigDecimal presupuesto, BigDecimal ejecutado, Moneda moneda) {
        this.presupuesto = presupuesto;
        this.ejecutado = ejecutado;
        this.moneda = moneda;
    }

    /** @return lo que aún se puede gastar (nunca negativo) */
    public BigDecimal getSaldo() {
        return presupuesto.subtract(ejecutado).max(BigDecimal.ZERO);
    }

    /** @return porcentaje ejecutado, de 0 a 100, redondeado a entero */
    public int getPorcentajeEjecucion() {
        if (presupuesto.signum() == 0) {
            return 0;
        }
        BigDecimal porcentaje = ejecutado.multiply(CIEN).divide(presupuesto, 0, RoundingMode.HALF_UP);
        return Math.min(porcentaje.intValue(), 100);
    }

    /** @return nivel del semáforo según el porcentaje ejecutado */
    public NivelAlerta getNivel() {
        if (ejecutado.compareTo(presupuesto) >= 0) {
            return NivelAlerta.AGOTADO;
        }
        BigDecimal limiteAlerta = presupuesto.multiply(UMBRAL_ALERTA);
        return ejecutado.compareTo(limiteAlerta) >= 0 ? NivelAlerta.ALERTA : NivelAlerta.NORMAL;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public BigDecimal getEjecutado() {
        return ejecutado;
    }

    public Moneda getMoneda() {
        return moneda;
    }
}
