package app.nomadia.controller.web;

import app.nomadia.exception.ReglaNegocioException;
import org.springframework.validation.BindingResult;

/**
 * Traslada una {@link ReglaNegocioException} al formulario, junto al campo correspondiente.
 */
final class ErroresFormulario {

    private static final String CODIGO_REGLA = "regla.negocio";

    private ErroresFormulario() {
        // Clase utilitaria: no se instancia
    }

    /**
     * @param resultado errores del formulario en curso
     * @param ex regla de negocio incumplida
     */
    static void registrar(BindingResult resultado, ReglaNegocioException ex) {
        if (ex.hasCampo()) {
            resultado.rejectValue(ex.getCampo(), CODIGO_REGLA, ex.getMessage());
        } else {
            resultado.reject(CODIGO_REGLA, ex.getMessage());
        }
    }
}
