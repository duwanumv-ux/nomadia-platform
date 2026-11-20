package app.nomadia.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Plan de viaje de un usuario hacia un destino (tabla nomadia_core.viaje).
 *
 * <p>Los itinerarios y gastos se consultan por medio de sus repositorios; la eliminación
 * en cascada la resuelve la base de datos (ON DELETE CASCADE).</p>
 */
@Entity
@Table(name = "viaje")
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_viaje")
    private Long id;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    /** Tope de gasto del viaje; base de la Ley de Rigidez Financiera. */
    @Column(name = "presupuesto", nullable = false, precision = 15, scale = 2)
    private BigDecimal presupuesto;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda", nullable = false, length = 3)
    private Moneda moneda;

    @Convert(converter = EstadoViajeConverter.class)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoViaje estado;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_destino", nullable = false)
    private Destino destino;

    /** Constructor requerido por JPA y usado al crear un viaje nuevo. */
    public Viaje() {
        this.estado = EstadoViaje.ACTIVO;
        this.moneda = Moneda.COP;
    }

    /** @return número de días del viaje, contando el día de llegada y el de regreso */
    public int getDuracionDias() {
        return (int) ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
    }

    /** @return true si el viaje admite nuevos gastos (solo los viajes activos) */
    public boolean isActivo() {
        return estado == EstadoViaje.ACTIVO;
    }

    /**
     * Indica si una fecha está dentro del rango del viaje (ambos extremos incluidos).
     *
     * @param fecha fecha a evaluar
     * @return true si la fecha pertenece al viaje
     */
    public boolean isFechaDentroDelViaje(LocalDate fecha) {
        return !fecha.isBefore(fechaInicio) && !fecha.isAfter(fechaFin);
    }

    public Long getId() {
        return id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public EstadoViaje getEstado() {
        return estado;
    }

    public void setEstado(EstadoViaje estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Destino getDestino() {
        return destino;
    }

    public void setDestino(Destino destino) {
        this.destino = destino;
    }
}
