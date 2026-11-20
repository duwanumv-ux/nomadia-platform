package app.nomadia.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Datos del formulario para agregar una actividad a un día del itinerario.
 */
public class ActividadForm {

    @NotBlank(message = "El nombre de la actividad es obligatorio.")
    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres.")
    @Pattern(regexp = GastoForm.PATRON_TEXTO_SEGURO, message = "El nombre contiene caracteres no permitidos.")
    private String nombre;

    @Size(max = 150, message = "La dirección admite máximo 150 caracteres.")
    private String direccion;

    @DecimalMin(value = "0", message = "El costo no puede ser negativo.")
    @Digits(integer = 13, fraction = 2, message = "Máximo 13 dígitos enteros y 2 decimales.")
    private BigDecimal costo;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}
