package app.nomadia.config;

import app.nomadia.dto.ActividadForm;
import app.nomadia.dto.GastoForm;
import app.nomadia.dto.ViajeForm;
import app.nomadia.model.CategoriaGasto;
import app.nomadia.model.Itinerario;
import app.nomadia.model.Moneda;
import app.nomadia.model.Viaje;
import app.nomadia.repository.DestinoRepository;
import app.nomadia.repository.UsuarioRepository;
import app.nomadia.service.GastoService;
import app.nomadia.service.ItinerarioService;
import app.nomadia.service.ViajeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Carga viajes de demostración en el perfil "h2" usando los mismos servicios de la aplicación,
 * de modo que los datos de ejemplo también pasan por todas las reglas de negocio.
 * Las fechas se calculan desde hoy para que siempre sean válidas.
 */
@Component
@Profile("h2")
public class DatosDemoLoader implements CommandLineRunner {

    private final ViajeService viajeService;
    private final GastoService gastoService;
    private final ItinerarioService itinerarioService;
    private final UsuarioRepository usuarioRepository;
    private final DestinoRepository destinoRepository;

    public DatosDemoLoader(ViajeService viajeService, GastoService gastoService, ItinerarioService itinerarioService,
                           UsuarioRepository usuarioRepository, DestinoRepository destinoRepository) {
        this.viajeService = viajeService;
        this.gastoService = gastoService;
        this.itinerarioService = itinerarioService;
        this.usuarioRepository = usuarioRepository;
        this.destinoRepository = destinoRepository;
    }

    @Override
    public void run(String... args) {
        LocalDate hoy = LocalDate.now();
        Viaje cartagena = crearViaje("ana.rios@mail.com", "Cartagena", hoy.plusDays(10), Moneda.COP);
        registrarGasto(cartagena, "Hotel en Getsemaní (4 noches)", "1200000", CategoriaGasto.ALOJAMIENTO);
        registrarGasto(cartagena, "Tiquete aéreo Bogotá - Cartagena", "380000", CategoriaGasto.TRANSPORTE);
        registrarGasto(cartagena, "Almuerzo típico en el centro", "65000", CategoriaGasto.COMIDA);
        agregarActividades(cartagena);

        Viaje salento = crearViaje("carlos.gomez@mail.com", "Salento", hoy.plusDays(20), Moneda.COP);
        registrarGasto(salento, "Glamping Valle de Cocora", "1050000", CategoriaGasto.ALOJAMIENTO);

        Viaje sanAndres = crearViaje("julian.herrera@mail.com", "San Andrés", hoy.plusDays(30), Moneda.USD);
        registrarGasto(sanAndres, "Tour de snorkel en Johnny Cay", "320.50", CategoriaGasto.OCIO);
    }

    private Viaje crearViaje(String correo, String ciudad, LocalDate inicio, Moneda moneda) {
        ViajeForm form = new ViajeForm();
        form.setUsuarioId(usuarioRepository.findByCorreo(correo).orElseThrow().getId());
        form.setDestinoId(destinoRepository.findFirstByCiudad(ciudad).orElseThrow().getId());
        form.setFechaInicio(inicio);
        form.setFechaFin(inicio.plusDays(moneda == Moneda.USD ? 5 : 3));
        form.setMoneda(moneda);
        form.setPresupuesto(moneda == Moneda.USD ? new BigDecimal("1500") : presupuestoCop(ciudad));
        return viajeService.crear(form);
    }

    private static BigDecimal presupuestoCop(String ciudad) {
        return "Cartagena".equals(ciudad) ? new BigDecimal("2500000") : new BigDecimal("1200000");
    }

    private void registrarGasto(Viaje viaje, String concepto, String monto, CategoriaGasto categoria) {
        GastoForm form = new GastoForm();
        form.setConcepto(concepto);
        form.setMonto(new BigDecimal(monto));
        form.setFecha(viaje.getFechaInicio());
        form.setCategoria(categoria);
        gastoService.registrar(viaje.getId(), form);
    }

    private void agregarActividades(Viaje viaje) {
        List<Itinerario> dias = itinerarioService.listarDias(viaje.getId());
        agregarActividad(viaje, dias.get(0), "Recorrido por la ciudad amurallada", "0");
        agregarActividad(viaje, dias.get(0), "Atardecer en el Café del Mar", "45000");
        agregarActividad(viaje, dias.get(1), "Islas del Rosario en lancha", "250000");
        agregarActividad(viaje, dias.get(2), "Castillo de San Felipe", "33000");
    }

    private void agregarActividad(Viaje viaje, Itinerario dia, String nombre, String costo) {
        ActividadForm form = new ActividadForm();
        form.setNombre(nombre);
        form.setCosto(new BigDecimal(costo));
        itinerarioService.agregarActividad(viaje.getId(), dia.getId(), form);
    }
}
