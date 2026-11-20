package app.nomadia.util;

import app.nomadia.model.Moneda;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Da formato a los valores monetarios según la moneda del viaje (convención colombiana).
 * Ejemplos: "$ 2.500.000 COP" y "US$ 1.500,00".
 */
public final class FormatoMoneda {

    private static final Locale LOCALE_COLOMBIA = Locale.forLanguageTag("es-CO");

    private FormatoMoneda() {
        // Clase utilitaria: no se instancia
    }

    /**
     * @param valor monto a mostrar (si es null se muestra 0)
     * @param moneda moneda del viaje
     * @return texto con símbolo, separadores de miles y decimales según la moneda
     */
    public static String formatear(BigDecimal valor, Moneda moneda) {
        BigDecimal monto = valor == null ? BigDecimal.ZERO : valor;
        NumberFormat formato = NumberFormat.getNumberInstance(LOCALE_COLOMBIA);
        if (moneda == Moneda.USD) {
            formato.setMinimumFractionDigits(2);
            formato.setMaximumFractionDigits(2);
            return "US$ " + formato.format(monto);
        }
        formato.setMaximumFractionDigits(0);
        return "$ " + formato.format(monto) + " COP";
    }
}
