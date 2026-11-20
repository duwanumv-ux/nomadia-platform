package app.nomadia.repository;

import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Viaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de la tabla viaje. */
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    List<Viaje> findAllByOrderByFechaInicioDesc();

    List<Viaje> findByEstadoOrderByFechaInicioDesc(EstadoViaje estado);
}
