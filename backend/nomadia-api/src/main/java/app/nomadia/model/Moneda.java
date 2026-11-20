package app.nomadia.model;

/**
 * Monedas admitidas para el presupuesto de un viaje (restricción chk_viaje_moneda).
 * Se guardan en la base de datos con su nombre exacto: 'COP' o 'USD'.
 */
public enum Moneda {
    COP("Peso colombiano"),
    USD("Dólar estadounidense");

    private final String descripcion;

    Moneda(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
