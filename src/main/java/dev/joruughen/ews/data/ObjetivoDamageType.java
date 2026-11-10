package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

/**
 * Un elemento de la lista "types" de una {@link ReglaDamageType}: o un id exacto de damage type
 * ("minecraft:magic", o "magic" que asume el namespace minecraft) o un tag ("#minecraft:is_fire").
 */
public record ObjetivoDamageType(ResourceLocation id, boolean esTag) {

    public static final Codec<ObjetivoDamageType> CODEC = Codec.STRING.comapFlatMap(
            ObjetivoDamageType::parsear,
            ObjetivoDamageType::toString
    );

    private static DataResult<ObjetivoDamageType> parsear(String texto) {
        boolean esTag = texto.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(esTag ? texto.substring(1) : texto);
        if (id == null) {
            return DataResult.error(() -> "Damage type o tag inválido: '" + texto + "'");
        }
        return DataResult.success(new ObjetivoDamageType(id, esTag));
    }

    public boolean coincide(Holder<DamageType> tipo) {
        return esTag ? tipo.is(TagKey.create(Registries.DAMAGE_TYPE, id)) : tipo.is(id);
    }

    /** Nombre corto para mostrar: sin el namespace "minecraft". */
    public String nombreCorto() {
        String nombre = "minecraft".equals(id.getNamespace()) ? id.getPath() : id.toString();
        return esTag ? "#" + nombre : nombre;
    }

    @Override
    public String toString() {
        return esTag ? "#" + id : id.toString();
    }
}
