package app.nomadia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import app.nomadia.dto.NivelAlerta;
import app.nomadia.exception.PresupuestoExcedidoException;
import app.nomadia.exception.ReglaNegocioException;
import app.nomadia.model.EstadoViaje;
import app.nomadia.model.Moneda;
import app.nomadia.model.Viaje;
import app.nomadia.repository.GastoRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias de la Ley de Rigidez Financiera (sin base de datos: el repositorio es un mock).
 */
@ExtendWith(MockitoExtension.class)
class RigidezFinancieraServiceTest {

    @Mock
    private GastoRepository gastoRepository;

    @InjectMocks
    private RigidezFinancieraService rigidezFinancieraService;

    private Viaje viaje;

    @BeforeEach
    void prepararViaje() {
        viaje = new Viaje();
        viaje.setFechaInicio(LocalDate.of(2026, 12, 1));
        viaje.setFechaFin(LocalDate.of(2026, 12, 5));
        viaje.setPresupuesto(new BigDecimal("1000000"));
        viaje.setMoneda(Moneda.COP);
    }

    @Test
    void permiteGastoQueCabeEnElSaldo() {
        when(gastoRepository.sumarMontosPorViaje(any())).thenReturn(new BigDecimal("600000"));

        rigidezFinancieraService.validarGasto(viaje, new BigDecimal("400000"), null);
    }

    @Test
    void bloqueaGastoQueSuperaElSaldo() {
        when(gastoRepository.sumarMontosPorViaje(any())).thenReturn(new BigDecimal("600000"));

        assertThatThrownBy(() -> rigidezFinancieraService.validarGasto(viaje, new BigDecimal("400000.01"), null))
                .isInstanceOf(PresupuestoExcedidoException.class)
                .satisfies(ex -> assertThat(((PresupuestoExcedidoException) ex).getSaldoDisponible())
                        .isEqualByComparingTo("400000"));
    }

    @Test
    void bloqueaGastosEnViajeCancelado() {
        viaje.setEstado(EstadoViaje.CANCELADO);

        assertThatThrownBy(() -> rigidezFinancieraService.validarGasto(viaje, BigDecimal.ONE, null))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Solo los viajes activos");
    }

    @Test
    void marcaAlertaDesdeEl85PorCiento() {
        when(gastoRepository.sumarMontosPorViaje(any())).thenReturn(new BigDecimal("850000"));

        assertThat(rigidezFinancieraService.calcularEstado(viaje).getNivel()).isEqualTo(NivelAlerta.ALERTA);
    }

    @Test
    void impideBajarElPresupuestoPorDebajoDeLoEjecutado() {
        when(gastoRepository.sumarMontosPorViaje(any())).thenReturn(new BigDecimal("700000"));

        assertThatThrownBy(() -> rigidezFinancieraService.validarNuevoPresupuesto(viaje, new BigDecimal("699999")))
                .isInstanceOf(ReglaNegocioException.class)
                .extracting("campo").isEqualTo("presupuesto");
    }
}
