package app.nomadia.exception;

/**
 * Se lanza cuando se consulta un registro que no existe (HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param recurso nombre del recurso, por ejemplo "Viaje"
     * @param id identificador buscado
     */
    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " con id " + id + " no existe.");
    }
}
