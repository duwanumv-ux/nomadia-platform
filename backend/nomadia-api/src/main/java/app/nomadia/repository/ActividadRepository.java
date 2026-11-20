package app.nomadia.repository;

import app.nomadia.model.Actividad;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a datos de las actividades del itinerario. */
public interface ActividadRepository extends JpaRepository<Actividad, Long> {

    @Query("select a from Actividad a where a.itinerario.viaje.id = :viajeId order by a.itinerario.orden, a.id")
    List<Actividad> buscarPorViaje(@Param("viajeId") Long viajeId);

    @Query("select coalesce(sum(a.costo), 0) from Actividad a where a.itinerario.viaje.id = :viajeId")
    BigDecimal sumarCostosPorViaje(@Param("viajeId") Long viajeId);
}
