package app.nomadia.service;

import app.nomadia.dto.ActividadForm;
import app.nomadia.exception.RecursoNoEncontradoException;
import app.nomadia.model.Actividad;
import app.nomadia.model.Itinerario;
import app.nomadia.model.Viaje;
import app.nomadia.repository.ActividadRepository;
import app.nomadia.repository.ItinerarioRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Genera y administra el itinerario de un viaje: un día por cada fecha y sus actividades.
 */
@Service
@Transactional
public class ItinerarioService {

    private final ItinerarioRepository itinerarioRepository;
    private final ActividadRepository actividadRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository, ActividadRepository actividadRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.actividadRepository = actividadRepository;
    }

    /**
     * Crea un día de itinerario por cada fecha del viaje ("Creando tu plan de viaje...").
     *
     * @param viaje viaje ya guardado
     * @return días creados, en orden
     */
    public List<Itinerario> generarDias(Viaje viaje) {
        List<Itinerario> dias = new ArrayList<>();
        for (int indice = 0; indice < viaje.getDuracionDias(); indice++) {
            dias.add(new Itinerario(viaje, viaje.getFechaInicio().plusDays(indice), indice + 1));
        }
        return itinerarioRepository.saveAll(dias);
    }

    /**
     * Borra el itinerario actual (con sus actividades) y lo crea de nuevo.
     * Se usa cuando el usuario cambia las fechas del viaje.
     *
     * @param viaje viaje con las fechas nuevas
     */
    public void regenerarDias(Viaje viaje) {
        itinerarioRepository.eliminarPorViaje(viaje.getId());
        generarDias(viaje);
    }

    @Transactional(readOnly = true)
    public List<Itinerario> listarDias(Long viajeId) {
        return itinerarioRepository.findByViajeIdOrderByOrdenAsc(viajeId);
    }

    /**
     * @param viajeId viaje a consultar
     * @return actividades agrupadas por el id del día, en el orden del itinerario
     */
    @Transactional(readOnly = true)
    public Map<Long, List<Actividad>> agruparActividadesPorDia(Long viajeId) {
        Map<Long, List<Actividad>> actividadesPorDia = new LinkedHashMap<>();
        for (Actividad actividad : actividadRepository.buscarPorViaje(viajeId)) {
            actividadesPorDia.computeIfAbsent(actividad.getItinerario().getId(), id -> new ArrayList<>()).add(actividad);
        }
        return actividadesPorDia;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCostoPlaneado(Long viajeId) {
        return actividadRepository.sumarCostosPorViaje(viajeId);
    }

    /**
     * Agrega una actividad a un día, verificando que el día pertenezca al viaje indicado.
     *
     * @param viajeId viaje de la URL
     * @param diaId día del itinerario
     * @param form datos validados de la actividad
     * @return la actividad creada
     */
    public Actividad agregarActividad(Long viajeId, Long diaId, ActividadForm form) {
        Itinerario dia = itinerarioRepository.findById(diaId)
                .filter(itinerario -> itinerario.getViaje().getId().equals(viajeId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Día de itinerario", diaId));
        BigDecimal costo = form.getCosto() == null ? BigDecimal.ZERO : form.getCosto();
        Actividad actividad = new Actividad(dia, form.getNombre().trim(), costo);
        actividad.setDireccion(textoONulo(form.getDireccion()));
        return actividadRepository.save(actividad);
    }

    /**
     * @param actividadId actividad a eliminar
     * @return id del viaje al que pertenecía (para volver a su itinerario)
     */
    public Long eliminarActividad(Long actividadId) {
        Actividad actividad = actividadRepository.findById(actividadId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Actividad", actividadId));
        Long viajeId = actividad.getItinerario().getViaje().getId();
        actividadRepository.delete(actividad);
        return viajeId;
    }

    private static String textoONulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
