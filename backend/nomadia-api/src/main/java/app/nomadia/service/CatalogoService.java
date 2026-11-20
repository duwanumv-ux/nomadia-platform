package app.nomadia.service;

import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.Destino;
import app.nomadia.model.Usuario;
import app.nomadia.repository.DestinoRepository;
import app.nomadia.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consulta de catálogos que alimentan los formularios: viajeros y destinos.
 */
@Service
@Transactional(readOnly = true)
public class CatalogoService {

    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;

    public CatalogoService(UsuarioRepository usuarioRepository, DestinoRepository destinoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByOrderByNombresAsc();
    }

    public List<Destino> listarDestinos() {
        return destinoRepository.findAllByOrderByCiudadAsc();
    }

    /**
     * @param usuarioId identificador elegido en el formulario
     * @return el usuario
     * @throws ReglaNegocioException si el viajero no existe
     */
    public Usuario buscarUsuario(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("usuarioId", "El viajero seleccionado no existe."));
    }

    /**
     * @param destinoId identificador elegido en el formulario
     * @return el destino
     * @throws ReglaNegocioException si el destino no existe
     */
    public Destino buscarDestino(Long destinoId) {
        return destinoRepository.findById(destinoId)
                .orElseThrow(() -> new ReglaNegocioException("destinoId", "El destino seleccionado no existe."));
    }
}
