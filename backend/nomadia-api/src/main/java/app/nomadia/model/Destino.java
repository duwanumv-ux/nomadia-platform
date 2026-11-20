package app.nomadia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Catálogo maestro de destinos (tabla nomadia_core.destino).
 *
 * <p>La columna geográfica {@code ubicacion} (PostGIS) no se mapea en este módulo:
 * la usa el módulo de rutas para la Ley del Nodo Central.</p>
 */
@Entity
@Table(name = "destino")
public class Destino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_destino")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "ciudad", nullable = false, length = 120)
    private String ciudad;

    @Column(name = "pais", nullable = false, length = 80)
    private String pais;

    protected Destino() {
        // Constructor requerido por JPA
    }

    /** @return texto corto para listas desplegables, por ejemplo "Cartagena · Centro Histórico" */
    public String getEtiqueta() {
        return ciudad + " · " + nombre;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getPais() {
        return pais;
    }
}
