package app.nomadia.controller.api;

import app.nomadia.dto.GastoForm;
import app.nomadia.dto.GastoResponse;
import app.nomadia.model.Gasto;
import app.nomadia.service.GastoService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de gastos. Un gasto que rompe la Ley de Rigidez Financiera responde 422.
 */
@RestController
@RequestMapping("/api")
public class GastoApiController {

    private final GastoService gastoService;

    public GastoApiController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    /** GET /api/viajes/{id}/gastos */
    @GetMapping("/viajes/{viajeId}/gastos")
    public List<GastoResponse> listarGastos(@PathVariable("viajeId") Long viajeId) {
        return gastoService.listarPorViaje(viajeId).stream().map(GastoResponse::desde).toList();
    }

    /** POST /api/viajes/{id}/gastos → 201 Created */
    @PostMapping("/viajes/{viajeId}/gastos")
    public ResponseEntity<GastoResponse> registrarGasto(@PathVariable("viajeId") Long viajeId,
                                                        @Valid @RequestBody GastoForm form) {
        Gasto gasto = gastoService.registrar(viajeId, form);
        return ResponseEntity.created(URI.create("/api/gastos/" + gasto.getId())).body(GastoResponse.desde(gasto));
    }

    /** PUT /api/gastos/{id} */
    @PutMapping("/gastos/{gastoId}")
    public GastoResponse actualizarGasto(@PathVariable("gastoId") Long gastoId, @Valid @RequestBody GastoForm form) {
        return GastoResponse.desde(gastoService.actualizar(gastoId, form));
    }

    /** DELETE /api/gastos/{id} → 204 No Content */
    @DeleteMapping("/gastos/{gastoId}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable("gastoId") Long gastoId) {
        gastoService.eliminar(gastoId);
        return ResponseEntity.noContent().build();
    }
}
