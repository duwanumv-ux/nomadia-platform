# NomadIA · Módulo Planificador de viajes y control de gastos

Evidencia **GA7-220501096-AA3-EV01 — Codificación de módulos del software stand-alone, web y móvil** · SENA, Tecnología en Análisis y Desarrollo de Software.

| | |
|---|---|
| **Equipo** | Diego Felipe Bolaños Peña · Santiago Blanco Agudelo · Duván Mejía |
| **Instructor** | Edinson Daniel Banguera Preciado |
| **Tipo de proyecto** | Web (con API REST reutilizable por la app móvil y el front-end React) |
| **Framework** | Spring Boot 3.5 · Spring MVC · Thymeleaf · Spring Data JPA · Bean Validation |
| **Lenguaje** | Java 17 o superior |
| **Base de datos** | PostgreSQL 16 + PostGIS (esquema `nomadia_core`) · H2 en memoria para demostración |
| **Versión** | 0.2.0 |

## 1. Qué hace el módulo

Codifica el flujo del prototipo de Figma *NomadIA – Planificador de viajes* y la regla de negocio central del proyecto, la **Ley de Rigidez Financiera**: ningún gasto puede superar el presupuesto del viaje.

| Pantalla del prototipo | Ruta | Historia de usuario |
|---|---|---|
| 1. Inicio | `GET /` | HU-01 |
| 2. Planifica tu viaje | `GET /viajes/nuevo` · `POST /viajes` | HU-01 Planificar viaje |
| 3. Creando tu plan de viaje... | `GET /viajes/{id}/procesando` | HU-01 |
| 4. ¡Tu viaje está listo! | `GET /viajes/{id}` | HU-02 Consultar viaje y su estado financiero |
| 5. Tu itinerario | `GET /viajes/{id}/itinerario` | HU-05 Itinerario y actividades |
| Mis planes | `GET /viajes?estado=ACTIVO` | HU-02 Consultar mis planes |
| Editar / eliminar viaje | `GET /viajes/{id}/editar` · `POST /viajes/{id}` · `POST /viajes/{id}/eliminar` | HU-03, HU-04 |
| Gastos del viaje | `GET /viajes/{id}/gastos` · `POST /viajes/{id}/gastos` | HU-06 Registrar gasto |
| Editar / eliminar gasto | `GET /gastos/{id}/editar` · `POST /gastos/{id}` · `POST /gastos/{id}/eliminar` | HU-07, HU-08 |

### Historias de usuario implementadas

| Código | Como… | Quiero… | Para… |
|---|---|---|---|
| HU-01 | viajero | registrar destino, fechas y presupuesto | que NomadIA genere mi itinerario día por día |
| HU-02 | viajero | consultar mis planes y su estado financiero | saber cuánto he gastado y cuánto me queda |
| HU-03 | viajero | editar un viaje | corregir fechas, presupuesto o estado |
| HU-04 | viajero | eliminar un viaje | descartar planes que ya no haré |
| HU-05 | viajero | agregar y quitar actividades por día | organizar lo que haré en el destino |
| HU-06 | viajero | registrar un gasto | que la app me bloquee si supero el presupuesto |
| HU-07 | viajero | corregir un gasto | ajustar montos sin romper el presupuesto |
| HU-08 | viajero | eliminar un gasto | quitar registros equivocados |

## 2. Arquitectura

Arquitectura por capas (MVC + servicios):

```text
Navegador / App móvil
   │  HTML (Thymeleaf)        JSON (API REST)
   ▼                          ▼
controller.web            controller.api        ← reciben la petición y validan el formato (@Valid)
   └──────────┬───────────────┘
              ▼
           service                              ← reglas de negocio (Ley de Rigidez Financiera, fechas, itinerario)
              ▼
         repository                             ← Spring Data JPA (consultas derivadas y JPQL)
              ▼
            model                               ← entidades JPA de nomadia_core
              ▼
   PostgreSQL 16 + PostGIS  (disparador trg_verifica_presupuesto como segunda defensa)
```

```text
nomadia-api/
├── pom.xml
└── src/
    ├── main/java/app/nomadia/
    │   ├── NomadiaApplication.java        # Punto de entrada
    │   ├── config/DatosDemoLoader.java    # Datos de demostración (perfil h2)
    │   ├── controller/web/                # Controladores de las pantallas
    │   ├── controller/api/                # API REST + manejo de errores (Problem Details)
    │   ├── dto/                           # Formularios, respuestas y EstadoFinanciero
    │   ├── exception/                     # Excepciones de negocio
    │   ├── model/                         # Entidades, enums y convertidores
    │   ├── repository/                    # Interfaces Spring Data JPA
    │   ├── service/                       # Reglas de negocio
    │   └── util/                          # Formato de moneda COP/USD
    ├── main/resources/
    │   ├── templates/                     # Vistas Thymeleaf (fragmentos reutilizables)
    │   ├── static/                        # CSS, JS y logo
    │   ├── db/h2/                         # Esquema y catálogos para el perfil h2
    │   └── application*.properties        # Configuración por perfil
    └── test/java/app/nomadia/             # Pruebas unitarias (Mockito) y de integración (MockMvc)
```

## 3. Estándares de codificación aplicados

Según el *Manual de estándares de codificación de NomadIA*:

| Elemento | Convención | Ejemplo en el código |
|---|---|---|
| Paquetes | minúsculas, dominio invertido | `app.nomadia.service` |
| Clases e interfaces | PascalCase, sin prefijo `I` | `RigidezFinancieraService`, `GastoRepository` |
| Métodos | camelCase, verbo en infinitivo | `validarGasto()`, `calcularEstado()`, `generarDias()` |
| Variables | camelCase, nombres completos | `saldoDisponible`, `montoPropuesto` |
| Booleanos | prefijo `is` / `has` | `isActivo()`, `hasFechasNuevas`, `hasCampo()` |
| Constantes | UPPER_SNAKE_CASE | `MAX_DIAS_VIAJE`, `UMBRAL_ALERTA`, `PATRON_TEXTO_SEGURO` |
| Tablas y columnas | snake_case (mapeadas con `@Column`) | `fecha_inicio`, `id_viaje` |
| Comentarios | Javadoc en clases y métodos públicos | `@param`, `@return`, `@throws` |

Otras prácticas: inyección por constructor, una responsabilidad por clase, métodos cortos, montos con `BigDecimal` (nunca `double`), DTO para no exponer entidades en la API y fragmentos Thymeleaf para no duplicar HTML. Los métodos de negocio reciben máximo 3 datos; los manejadores de Spring MVC además reciben objetos de infraestructura que inyecta el framework (`BindingResult`, `Model`).

## 4. Validaciones

| Dato | Regla | Dónde |
|---|---|---|
| Viajero, destino, fechas, presupuesto, moneda | Obligatorios | `ViajeForm` (`@NotNull`) |
| Fecha de inicio | No puede estar en el pasado (al crear) | `ViajeService` |
| Fecha de regreso | Igual o posterior al inicio; viaje de máximo 30 días | `ViajeService` |
| Presupuesto | Mayor que 0; máximo 13 enteros y 2 decimales; no menor que lo ya gastado | `ViajeForm` + `RigidezFinancieraService` |
| Moneda | Solo COP o USD; no se cambia si el viaje ya tiene gastos | enum `Moneda` + `ViajeService` |
| Concepto del gasto, nombre de actividad | 3 a 150/120 caracteres; solo letras, números y `. , ( ) # / ' -` | `GastoForm`, `ActividadForm` (`@Size`, `@Pattern`) |
| Monto del gasto | Mayor que 0; máximo 13 enteros y 2 decimales | `GastoForm` |
| Fecha del gasto | Dentro de las fechas del viaje | `GastoService` |
| Ley de Rigidez Financiera | Gasto ≤ saldo disponible; solo viajes activos admiten gastos | `RigidezFinancieraService` + disparador PostgreSQL |
| Semáforo | Normal < 85 % ≤ Alerta < 100 % = Agotado | `EstadoFinanciero` |

## 5. Cómo ejecutar

**Requisitos:** JDK 17 o superior (recomendado 21) y un IDE con Maven: IntelliJ IDEA Community, VS Code con *Extension Pack for Java*, o Maven instalado.

### Opción A: demostración sin instalar base de datos (perfil `h2`, por defecto)

1. Abrir la carpeta `nomadia-api` en IntelliJ (*Open* → seleccionar `pom.xml` → *Open as Project*) y esperar a que Maven descargue las dependencias.
2. Ejecutar `NomadiaApplication` (botón ▶).
3. Abrir <http://localhost:8080>.

Desde la terminal: `mvn spring-boot:run`

Se cargan 3 viajes de ejemplo con fechas relativas a hoy: Cartagena (66 % ejecutado), Salento (87 %, en alerta) y San Andrés (en USD). La consola H2 queda en <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:nomadia`, usuario `sa`, sin contraseña).

### Opción B: base de datos real del proyecto (perfil `postgres`)

1. Ejecutar en PostgreSQL los scripts `database/01` a `03` del repositorio `nomadia-platform`.
2. Definir las credenciales como variables de entorno (`DB_USER`, `DB_PASSWORD`; opcionales `DB_HOST`, `DB_PORT`, `DB_NAME`).
3. Ejecutar con el perfil postgres:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

En IntelliJ: *Run → Edit Configurations → Active profiles:* `postgres`.

### Pruebas

```bash
mvn test
```

Incluye pruebas unitarias de la Ley de Rigidez Financiera (Mockito) y una prueba de integración que abre todas las pantallas, valida caracteres especiales y verifica que la API y la web bloqueen el mismo gasto.

## 6. API REST

| Método | Ruta | Respuesta |
|---|---|---|
| GET | `/api/viajes?estado=ACTIVO` | Lista de viajes |
| GET | `/api/viajes/{id}` | Un viaje · 404 si no existe |
| POST | `/api/viajes` | 201 Created |
| PUT | `/api/viajes/{id}` | Viaje actualizado |
| DELETE | `/api/viajes/{id}` | 204 No Content |
| GET | `/api/viajes/{id}/estado-financiero` | Presupuesto, ejecutado, saldo, % y semáforo |
| GET | `/api/viajes/{id}/gastos` | Gastos del viaje |
| POST | `/api/viajes/{id}/gastos` | 201 · **422** si rompe la Ley de Rigidez Financiera |
| PUT | `/api/gastos/{id}` | Gasto actualizado |
| DELETE | `/api/gastos/{id}` | 204 No Content |

Ejemplo de gasto bloqueado (422, formato RFC 9457):

```json
{
  "type": "about:blank",
  "title": "Ley de Rigidez Financiera",
  "status": 422,
  "detail": "Ley de Rigidez Financiera: el gasto supera el saldo disponible del viaje.",
  "saldoDisponible": 150000.00,
  "montoPropuesto": 500000
}
```

## 7. Control de versiones

El módulo se integra al repositorio `nomadia-platform` en la carpeta `backend/nomadia-api`, siguiendo Git Flow:

```bash
git switch develop
git pull
git switch -c feature/NIA-04-planificador-viajes
# copiar la carpeta nomadia-api dentro de backend/
git add backend/nomadia-api
git commit -m "feat(backend): agregar módulo planificador de viajes con Spring Boot"
git push -u origin feature/NIA-04-planificador-viajes
# Pull Request en GitHub: feature/NIA-04-planificador-viajes → develop
```
