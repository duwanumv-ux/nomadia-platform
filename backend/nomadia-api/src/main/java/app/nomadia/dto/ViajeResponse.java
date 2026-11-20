package app.nomadia.dto;

import app.nomadia.model.Viaje;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Representación JSON de un viaje para la API REST.
 * Se usa un DTO para no exponer las entidades JPA directamente.
 */
public record ViajeResponse(
        Long id,
        String viajero,
        String destino,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        int duracionDias,
        BigDecimal presupuesto,
        String moneda,
        String estado) {

    /**
     * @param viaje entidad a convertir
     * @return DTO listo para serializar
     */
    public static ViajeResponse desde(Viaje viaje) {
        return new ViajeResponse(
                viaje.getId(),
                viaje.getUsuario().getNombreCompleto(),
                viaje.getDestino().getEtiqueta(),
                viaje.getFechaInicio(),
                viaje.getFechaFin(),
                viaje.getDuracionDias(),
                viaje.getPresupuesto(),
                viaje.getMoneda().name(),
                viaje.getEstado().getCodigo());
    }
}
