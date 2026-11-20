package app.nomadia.repository;

import app.nomadia.model.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de la tabla usuario. */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByOrderByNombresAsc();

    Optional<Usuario> findByCorreo(String correo);
}
