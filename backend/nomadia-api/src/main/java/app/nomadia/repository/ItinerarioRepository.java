package app.nomadia.repository;

import app.nomadia.model.Itinerario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a datos de los días del itinerario. */
public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    List<Itinerario> findByViajeIdOrderByOrdenAsc(Long viajeId);

    /**
     * Elimina todos los días de un viaje (y sus actividades por ON DELETE CASCADE).
     * Se ejecuta antes de regenerar el itinerario cuando cambian las fechas.
     *
     * @param viajeId identificador del viaje
     * @return número de días eliminados
     */
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from Itinerario i where i.viaje.id = :viajeId")
    int eliminarPorViaje(@Param("viajeId") Long viajeId);
}
