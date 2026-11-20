package app.nomadia.util;

import app.nomadia.model.Moneda;
import java.math.BigDecimal;
import org.springframework.stereotype.Component;

/**
 * Componente que expone el formato de moneda a las plantillas Thymeleaf.
 * Uso en la vista: {@code ${@formato.moneda(viaje.presupuesto, viaje.moneda)}}
 */
@Component("formato")
public class FormatoVista {

    /**
     * @param valor monto a mostrar
     * @param moneda moneda del viaje
     * @return texto formateado
     */
    public String moneda(BigDecimal valor, Moneda moneda) {
        return FormatoMoneda.formatear(valor, moneda);
    }
}
