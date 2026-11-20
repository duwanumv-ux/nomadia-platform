package app.nomadia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * Actividad planeada dentro de un día del itinerario (tabla nomadia_core.actividad).
 * La columna geográfica {@code coordenada} la gestiona el módulo de rutas.
 */
@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Long id;

    @Column(name = "nombre", nullable = false, length = 120)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "direccion", length = 150)
    private String direccion;

    /** Costo estimado; no descuenta presupuesto (eso solo lo hacen los gastos reales). */
    @Column(name = "costo", nullable = false, precision = 15, scale = 2)
    private BigDecimal costo;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_itinerario", nullable = false)
    private Itinerario itinerario;

    protected Actividad() {
        // Constructor requerido por JPA
    }

    /**
     * Crea una actividad asociada a un día del itinerario.
     *
     * @param itinerario día al que pertenece
     * @param nombre nombre corto de la actividad
     * @param costo costo estimado (cero si no tiene costo)
     */
    public Actividad(Itinerario itinerario, String nombre, BigDecimal costo) {
        this.itinerario = itinerario;
        this.nombre = nombre;
        this.costo = costo;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }
}
