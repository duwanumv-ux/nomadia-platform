package app.nomadia.web;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import app.nomadia.service.ViajeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de humo de todo el módulo con la base H2 y los datos de demostración:
 * cada pantalla debe renderizarse y las reglas de negocio deben responder igual en la web y en la API.
 * Viajes de demostración: 1 = Cartagena, 2 = Salento (87 % ejecutado), 3 = San Andrés (USD).
 */
@SpringBootTest
@AutoConfigureMockMvc
class PantallasIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ViajeService viajeService;

    @Test
    void muestraLasPantallasDelPrototipo() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Tu próximo destino")));
        mockMvc.perform(get("/viajes/nuevo")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Planifica tu viaje")));
        mockMvc.perform(get("/viajes/1/procesando")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Creando tu plan de viaje")));
        mockMvc.perform(get("/viajes/1").param("nuevo", "true")).andExpect(status().isOk())
                .andExpect(content().string(containsString("¡Tu viaje está listo!")));
        mockMvc.perform(get("/viajes/1/itinerario")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Islas del Rosario en lancha")));
    }

    @Test
    void muestraPlanesGastosYEdicion() throws Exception {
        mockMvc.perform(get("/viajes")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Mis planes")));
        mockMvc.perform(get("/viajes").param("estado", "ACTIVO")).andExpect(status().isOk());
        mockMvc.perform(get("/viajes/2/gastos")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Has usado más del 85 %")));
        mockMvc.perform(get("/viajes/1/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar viaje")));
        mockMvc.perform(get("/gastos/1/editar")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Editar gasto")));
    }

    @Test
    void responde404ConPaginaPropia() throws Exception {
        mockMvc.perform(get("/viajes/999")).andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Viaje con id 999 no existe")));
    }

    @Test
    void muestraElModalDeRigidezFinanciera() throws Exception {
        String fecha = viajeService.buscar(2L).getFechaInicio().toString();
        mockMvc.perform(post("/viajes/2/gastos")
                        .param("concepto", "Cena de despedida")
                        .param("monto", "500000")
                        .param("fecha", fecha)
                        .param("categoria", "COMIDA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Gasto bloqueado")));
    }

    @Test
    void validaCaracteresEspecialesYMontos() throws Exception {
        String fecha = viajeService.buscar(1L).getFechaInicio().toString();
        mockMvc.perform(post("/viajes/1/gastos")
                        .param("concepto", "<script>")
                        .param("monto", "-5")
                        .param("fecha", fecha)
                        .param("categoria", "COMIDA"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("caracteres no permitidos")))
                .andExpect(content().string(containsString("El monto debe ser mayor que cero")));
    }

    @Test
    void apiAplicaLaMismaRegla() throws Exception {
        String fecha = viajeService.buscar(2L).getFechaInicio().toString();
        String json = "{\"concepto\":\"Cena\",\"monto\":500000,\"fecha\":\"" + fecha + "\",\"categoria\":\"COMIDA\"}";
        mockMvc.perform(get("/api/viajes")).andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
        mockMvc.perform(post("/api/viajes/2/gastos").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value("Ley de Rigidez Financiera"));
    }
}
