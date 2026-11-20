package app.nomadia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Viajero registrado en NomadIA (tabla nomadia_core.usuario).
 *
 * <p>En este módulo el usuario se usa como catálogo de solo lectura: el registro,
 * la autenticación y el cifrado de contraseñas pertenecen al módulo de cuentas.
 * Por eso solo se mapean las columnas que el planificador necesita.</p>
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombres", nullable = false, length = 80)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 80)
    private String apellidos;

    @Column(name = "correo", nullable = false, length = 150)
    private String correo;

    protected Usuario() {
        // Constructor requerido por JPA
    }

    /** @return nombres y apellidos unidos, para mostrar en pantalla */
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public Long getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreo() {
        return correo;
    }
}
