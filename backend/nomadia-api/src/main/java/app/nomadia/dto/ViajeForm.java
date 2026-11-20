package app.nomadia.dto;

import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Moneda;
import app.nomadia.model.Viaje;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Datos del formulario "Planifica tu viaje" (crear y editar) y cuerpo JSON de la API.
 *
 * <p>Las validaciones de formato van aquí con anotaciones; las que dependen de otros
 * datos (rango de fechas, presupuesto frente a lo ejecutado) las aplica {@code ViajeService}.</p>
 */
public class ViajeForm {

    @NotNull(message = "Seleccione el viajero.")
    private Long usuarioId;

    @NotNull(message = "Seleccione un destino.")
    private Long destinoId;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de regreso es obligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    @NotNull(message = "El presupuesto es obligatorio.")
    @DecimalMin(value = "1", message = "El presupuesto debe ser mayor que cero.")
    @Digits(integer = 13, fraction = 2, message = "Máximo 13 dígitos enteros y 2 decimales.")
    private BigDecimal presupuesto;

    @NotNull(message = "Seleccione la moneda.")
    private Moneda moneda = Moneda.COP;

    /** Solo se usa al editar; un viaje nuevo siempre nace ACTIVO. */
    private EstadoViaje estado = EstadoViaje.ACTIVO;

    /**
     * Crea un formulario con los datos actuales de un viaje (para la pantalla de edición).
     *
     * @param viaje viaje existente
     * @return formulario precargado
     */
    public static ViajeForm desde(Viaje viaje) {
        ViajeForm form = new ViajeForm();
        form.usuarioId = viaje.getUsuario().getId();
        form.destinoId = viaje.getDestino().getId();
        form.fechaInicio = viaje.getFechaInicio();
        form.fechaFin = viaje.getFechaFin();
        form.presupuesto = viaje.getPresupuesto();
        form.moneda = viaje.getMoneda();
        form.estado = viaje.getEstado();
        return form;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getDestinoId() {
        return destinoId;
    }

    public void setDestinoId(Long destinoId) {
        this.destinoId = destinoId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public EstadoViaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoViaje estado) {
        this.estado = estado;
    }
}
