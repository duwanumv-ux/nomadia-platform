package app.nomadia.model;

import java.util.Arrays;

/**
 * Dominio cerrado de categorías de gasto (restricción chk_gasto_categoria).
 */
public enum CategoriaGasto {
    GENERAL("general", "General"),
    ALOJAMIENTO("alojamiento", "Alojamiento"),
    TRANSPORTE("transporte", "Transporte"),
    COMIDA("comida", "Comida"),
    OCIO("ocio", "Ocio"),
    COMPRAS("compras", "Compras");

    private final String codigo;
    private final String etiqueta;

    CategoriaGasto(String codigo, String etiqueta) {
        this.codigo = codigo;
        this.etiqueta = etiqueta;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    /**
     * Obtiene la categoría a partir del código guardado en la base de datos.
     *
     * @param codigo valor de la columna categoria (por ejemplo, "comida")
     * @return la categoría correspondiente
     */
    public static CategoriaGasto fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(categoria -> categoria.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Categoría de gasto desconocida: " + codigo));
    }
}
