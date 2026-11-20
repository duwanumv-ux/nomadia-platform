package app.nomadia.model;

import java.util.Arrays;

/**
 * Estados posibles de un viaje (restricción chk_estado de la tabla viaje).
 * El código en minúscula es el valor que se almacena en la base de datos.
 */
public enum EstadoViaje {
    ACTIVO("activo", "Activo"),
    FINALIZADO("finalizado", "Finalizado"),
    CANCELADO("cancelado", "Cancelado");

    private final String codigo;
    private final String etiqueta;

    EstadoViaje(String codigo, String etiqueta) {
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
     * Obtiene el estado a partir del código guardado en la base de datos.
     *
     * @param codigo valor de la columna estado (por ejemplo, "activo")
     * @return el estado correspondiente
     * @throws IllegalArgumentException si el código no existe en el dominio
     */
    public static EstadoViaje fromCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(estado -> estado.codigo.equals(codigo))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado de viaje desconocido: " + codigo));
    }
}
