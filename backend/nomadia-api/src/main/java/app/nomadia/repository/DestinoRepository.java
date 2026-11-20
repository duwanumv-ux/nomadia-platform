package app.nomadia.repository;

import app.nomadia.model.Destino;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos del catálogo de destinos. */
public interface DestinoRepository extends JpaRepository<Destino, Long> {

    List<Destino> findAllByOrderByCiudadAsc();

    Optional<Destino> findFirstByCiudad(String ciudad);
}
