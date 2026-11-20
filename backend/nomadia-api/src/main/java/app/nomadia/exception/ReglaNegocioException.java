package app.nomadia.exception;

/**
 * Se lanza cuando una operación incumple una regla de negocio de NomadIA.
 *
 * <p>Lleva el nombre del campo afectado para que la vista muestre el mensaje
 * junto al control correcto del formulario. Si el campo es null, el error es general.</p>
 */
public class ReglaNegocioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String campo;

    /**
     * @param campo nombre del campo del formulario afectado, o null si es un error general
     * @param mensaje explicación para el usuario
     */
    public ReglaNegocioException(String campo, String mensaje) {
        super(mensaje);
        this.campo = campo;
    }

    public String getCampo() {
        return campo;
    }

    /** @return true si el error se puede asociar a un campo del formulario */
    public boolean hasCampo() {
        return campo != null;
    }
}
