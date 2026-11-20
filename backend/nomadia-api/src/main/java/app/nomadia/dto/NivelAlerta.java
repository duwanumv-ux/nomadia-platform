package app.nomadia.dto;

/**
 * Semáforo de la Ley de Rigidez Financiera según el porcentaje ejecutado del presupuesto.
 */
public enum NivelAlerta {
    /** Menos del 85 % ejecutado. */
    NORMAL,
    /** Entre el 85 % y el 99 % ejecutado: el viajero debe moderar el gasto. */
    ALERTA,
    /** 100 % ejecutado: no se admiten más gastos. */
    AGOTADO
}
