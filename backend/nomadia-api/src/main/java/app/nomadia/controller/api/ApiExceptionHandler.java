package app.nomadia.controller.api;

import app.nomadia.exception.PresupuestoExcedidoException;
import app.nomadia.exception.RecursoNoEncontradoException;
import app.nomadia.exception.ReglaNegocioException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones de la API a respuestas RFC 9457 (Problem Details) en español.
 */
@RestControllerAdvice(basePackages = "app.nomadia.controller.api")
public class ApiExceptionHandler {

    /** 404: el viaje o el gasto no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(RecursoNoEncontradoException ex) {
        return crearProblema(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage());
    }

    /** 422: Ley de Rigidez Financiera, con el saldo disponible para que el cliente lo muestre. */
    @ExceptionHandler(PresupuestoExcedidoException.class)
    public ProblemDetail manejarPresupuestoExcedido(PresupuestoExcedidoException ex) {
        ProblemDetail problema = crearProblema(HttpStatus.UNPROCESSABLE_ENTITY, "Ley de Rigidez Financiera",
                ex.getMessage());
        problema.setProperty("saldoDisponible", ex.getSaldoDisponible());
        problema.setProperty("montoPropuesto", ex.getMontoPropuesto());
        return problema;
    }

    /** 422: cualquier otra regla de negocio (fechas, estado del viaje, presupuesto...). */
    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail manejarReglaNegocio(ReglaNegocioException ex) {
        ProblemDetail problema = crearProblema(HttpStatus.UNPROCESSABLE_ENTITY, "Regla de negocio", ex.getMessage());
        problema.setProperty("campo", ex.getCampo());
        return problema;
    }

    /** 400: datos que no cumplen las validaciones del formulario (Bean Validation). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        ProblemDetail problema = crearProblema(HttpStatus.BAD_REQUEST, "Datos inválidos",
                "Revise los campos indicados en 'errores'.");
        problema.setProperty("errores", errores);
        return problema;
    }

    /** 400: JSON mal formado o con valores de tipo incorrecto (por ejemplo, una fecha inválida). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail manejarJsonInvalido(HttpMessageNotReadableException ex) {
        return crearProblema(HttpStatus.BAD_REQUEST, "JSON inválido",
                "El cuerpo de la petición no es un JSON válido o tiene valores con formato incorrecto.");
    }

    private ProblemDetail crearProblema(HttpStatus estado, String titulo, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        return problema;
    }
}
