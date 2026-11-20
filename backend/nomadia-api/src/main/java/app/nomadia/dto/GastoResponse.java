package app.nomadia.dto;

import app.nomadia.model.Gasto;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representación JSON de un gasto para la API REST.
 */
public record GastoResponse(
        Long id,
        Long viajeId,
        String concepto,
        BigDecimal monto,
        LocalDate fecha,
        String categoria) {

    /**
     * @param gasto entidad a convertir
     * @return DTO listo para serializar
     */
    public static GastoResponse desde(Gasto gasto) {
        return new GastoResponse(
                gasto.getId(),
                gasto.getViaje().getId(),
                gasto.getConcepto(),
                gasto.getMonto(),
                gasto.getFecha(),
                gasto.getCategoria().getCodigo());
    }
}
