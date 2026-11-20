package app.nomadia.controller.web;

import app.nomadia.exception.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;

/**
 * Manejo centralizado de errores para las pantallas HTML (no aplica a la API REST).
 */
@ControllerAdvice(basePackages = "app.nomadia.controller.web")
public class WebExceptionHandler {

    /**
     * @param ex registro inexistente
     * @return página de error con código 404
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ModelAndView manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return crearPaginaError(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * @param ex parámetro de URL con formato inválido (por ejemplo, /viajes/abc)
     * @return página de error con código 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView manejarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return crearPaginaError(HttpStatus.BAD_REQUEST, "El valor '" + ex.getValue() + "' no es válido para "
                + ex.getName() + ".");
    }

    private ModelAndView crearPaginaError(HttpStatus estado, String mensaje) {
        ModelAndView vista = new ModelAndView("error");
        vista.setStatus(estado);
        vista.addObject("status", estado.value());
        vista.addObject("mensaje", mensaje);
        return vista;
    }
}
