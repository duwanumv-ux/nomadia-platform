package app.nomadia.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte {@link EstadoViaje} al código en minúscula que exige la restricción chk_estado.
 */
@Converter
public class EstadoViajeConverter implements AttributeConverter<EstadoViaje, String> {

    @Override
    public String convertToDatabaseColumn(EstadoViaje estado) {
        return estado == null ? null : estado.getCodigo();
    }

    @Override
    public EstadoViaje convertToEntityAttribute(String codigo) {
        return codigo == null ? null : EstadoViaje.fromCodigo(codigo);
    }
}
