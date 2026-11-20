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
import java.time.LocalDate;

/**
 * Día del itinerario de un viaje (tabla nomadia_core.itinerario).
 * La combinación (viaje, orden) es única: restricción uq_itinerario_orden.
 */
@Entity
@Table(name = "itinerario")
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_itinerario")
    private Long id;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    /** Posición del día dentro del viaje: 1, 2, 3... (restricción chk_orden > 0). */
    @Column(name = "orden", nullable = false)
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_viaje", nullable = false)
    private Viaje viaje;

    protected Itinerario() {
        // Constructor requerido por JPA
    }

    /**
     * Crea un día de itinerario.
     *
     * @param viaje viaje al que pertenece el día
     * @param fecha fecha del día
     * @param orden posición del día dentro del viaje (empieza en 1)
     */
    public Itinerario(Viaje viaje, LocalDate fecha, Integer orden) {
        this.viaje = viaje;
        this.fecha = fecha;
        this.orden = orden;
        this.descripcion = "Día " + orden + " en " + viaje.getDestino().getCiudad();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public Viaje getViaje() {
        return viaje;
    }
}
