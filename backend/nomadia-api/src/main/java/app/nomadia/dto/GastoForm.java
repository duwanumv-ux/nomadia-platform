package app.nomadia.dto;

import app.nomadia.model.CategoriaGasto;
import app.nomadia.model.Gasto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Datos del formulario de registro y edición de un gasto.
 */
public class GastoForm {

    /** Letras (con tildes y ñ), números, espacios y signos básicos. Bloquea &lt; &gt; { } $ % etc. */
    public static final String PATRON_TEXTO_SEGURO = "^[\\p{L}\\p{N} .,()#/'-]+$";

    @NotBlank(message = "El concepto es obligatorio.")
    @Size(min = 3, max = 150, message = "El concepto debe tener entre 3 y 150 caracteres.")
    @Pattern(regexp = PATRON_TEXTO_SEGURO, message = "El concepto contiene caracteres no permitidos.")
    private String concepto;

    @NotNull(message = "El monto es obligatorio.")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero.")
    @Digits(integer = 13, fraction = 2, message = "Máximo 13 dígitos enteros y 2 decimales.")
    private BigDecimal monto;

    @NotNull(message = "La fecha del gasto es obligatoria.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @NotNull(message = "Seleccione una categoría.")
    private CategoriaGasto categoria = CategoriaGasto.GENERAL;

    /**
     * @param gasto gasto existente
     * @return formulario precargado para la pantalla de edición
     */
    public static GastoForm desde(Gasto gasto) {
        GastoForm form = new GastoForm();
        form.concepto = gasto.getConcepto();
        form.monto = gasto.getMonto();
        form.fecha = gasto.getFecha();
        form.categoria = gasto.getCategoria();
        return form;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public CategoriaGasto getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaGasto categoria) {
        this.categoria = categoria;
    }
}
