package app.nomadia.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte {@link CategoriaGasto} al código en minúscula que exige la restricción chk_gasto_categoria.
 */
@Converter
public class CategoriaGastoConverter implements AttributeConverter<CategoriaGasto, String> {

    @Override
    public String convertToDatabaseColumn(CategoriaGasto categoria) {
        return categoria == null ? null : categoria.getCodigo();
    }

    @Override
    public CategoriaGasto convertToEntityAttribute(String codigo) {
        return codigo == null ? null : CategoriaGasto.fromCodigo(codigo);
    }
}
