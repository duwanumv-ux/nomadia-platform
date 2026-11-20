package app.nomadia.repository;

import app.nomadia.model.Gasto;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a datos de los gastos de un viaje. */
public interface GastoRepository extends JpaRepository<Gasto, Long> {

    List<Gasto> findByViajeIdOrderByFechaDescIdDesc(Long viajeId);

    /** @return total ejecutado del viaje (0 si no tiene gastos) */
    @Query("select coalesce(sum(g.monto), 0) from Gasto g where g.viaje.id = :viajeId")
    BigDecimal sumarMontosPorViaje(@Param("viajeId") Long viajeId);

    /** @return total ejecutado sin contar un gasto concreto (se usa al editar ese gasto) */
    @Query("select coalesce(sum(g.monto), 0) from Gasto g where g.viaje.id = :viajeId and g.id <> :gastoId")
    BigDecimal sumarMontosPorViajeExcluyendo(@Param("viajeId") Long viajeId, @Param("gastoId") Long gastoId);
}
