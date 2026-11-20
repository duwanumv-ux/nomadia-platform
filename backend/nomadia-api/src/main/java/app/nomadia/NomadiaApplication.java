package app.nomadia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del módulo "Planificador de viajes y control de gastos" de NomadIA.
 *
 * <p>Arquitectura por capas: controller (web y API) → service (reglas de negocio)
 * → repository (Spring Data JPA) → model (entidades del esquema nomadia_core).</p>
 *
 * @author Diego Felipe Bolaños Peña, Santiago Blanco Agudelo, Duván Mejía
 * @version 0.2.0
 */
@SpringBootApplication
public class NomadiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(NomadiaApplication.class, args);
    }
}
