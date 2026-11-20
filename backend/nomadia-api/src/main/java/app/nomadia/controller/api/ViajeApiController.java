package app.nomadia.controller.api;

import app.nomadia.dto.EstadoFinanciero;
import app.nomadia.dto.ViajeForm;
import app.nomadia.dto.ViajeResponse;
import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Viaje;
import app.nomadia.service.RigidezFinancieraService;
import app.nomadia.service.ViajeService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de viajes (JSON). Reutiliza los mismos servicios que las pantallas web,
 * de modo que las reglas de negocio son idénticas para la web, el front-end React y la app móvil.
 */
@RestController
@RequestMapping("/api/viajes")
public class ViajeApiController {

    private final ViajeService viajeService;
    private final RigidezFinancieraService rigidezFinancieraService;

    public ViajeApiController(ViajeService viajeService, RigidezFinancieraService rigidezFinancieraService) {
        this.viajeService = viajeService;
        this.rigidezFinancieraService = rigidezFinancieraService;
    }

    /** GET /api/viajes?estado=ACTIVO */
    @GetMapping
    public List<ViajeResponse> listarViajes(@RequestParam(name = "estado", required = false) EstadoViaje estado) {
        return viajeService.listar(estado).stream().map(ViajeResponse::desde).toList();
    }

    /** GET /api/viajes/{id} */
    @GetMapping("/{viajeId}")
    public ViajeResponse buscarViaje(@PathVariable("viajeId") Long viajeId) {
        return ViajeResponse.desde(viajeService.buscar(viajeId));
    }

    /** POST /api/viajes → 201 Created con la cabecera Location. */
    @PostMapping
    public ResponseEntity<ViajeResponse> crearViaje(@Valid @RequestBody ViajeForm form) {
        Viaje viaje = viajeService.crear(form);
        return ResponseEntity.created(URI.create("/api/viajes/" + viaje.getId())).body(ViajeResponse.desde(viaje));
    }

    /** PUT /api/viajes/{id} */
    @PutMapping("/{viajeId}")
    public ViajeResponse actualizarViaje(@PathVariable("viajeId") Long viajeId, @Valid @RequestBody ViajeForm form) {
        return ViajeResponse.desde(viajeService.actualizar(viajeId, form));
    }

    /** DELETE /api/viajes/{id} → 204 No Content */
    @DeleteMapping("/{viajeId}")
    public ResponseEntity<Void> eliminarViaje(@PathVariable("viajeId") Long viajeId) {
        viajeService.eliminar(viajeId);
        return ResponseEntity.noContent().build();
    }

    /** GET /api/viajes/{id}/estado-financiero */
    @GetMapping("/{viajeId}/estado-financiero")
    public EstadoFinanciero consultarEstadoFinanciero(@PathVariable("viajeId") Long viajeId) {
        return rigidezFinancieraService.calcularEstado(viajeService.buscar(viajeId));
    }
}
