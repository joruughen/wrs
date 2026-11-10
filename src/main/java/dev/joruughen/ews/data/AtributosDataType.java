package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record AtributosDataType(
        List<ResourceLocation> ids,
        List<Modificador> modifiers,
        List<ReglaDamageType> damageTypes,
        Optional<ModoArma> weaponMode
) {
    public static final Codec<AtributosDataType> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.listOf()
                            .optionalFieldOf("ids", List.of()) // Hace que "ids" sea opcional con una lista vacía por defecto
                            .forGetter(AtributosDataType::ids),
                    Modificador.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(AtributosDataType::modifiers),
                    ReglaDamageType.CODEC.listOf().optionalFieldOf("damage_types", List.of()).forGetter(AtributosDataType::damageTypes),
                    ModoArma.CODEC.optionalFieldOf("weapon_mode").forGetter(AtributosDataType::weaponMode)
            ).apply(instance, AtributosDataType::new)
    );

    private record ClaveModificador(ResourceLocation atributo, AttributeModifier.Operation operacion) {
    }

    /**
     * Combina los modificadores de un grupo con los específicos de una entidad/ítem. Si ambos definen
     * un modificador para el mismo atributo+operación, el específico reemplaza al del grupo; el resto
     * de los modificadores de ambos se conserva. Las reglas por damage type de ambos se conservan,
     * con las del específico primero para que tengan prioridad (ver {@link ReglaDamageType#buscar}).
     */
    public static AtributosDataType reemplazarValores(AtributosDataType grupo, AtributosDataType especifico) {
        Map<ClaveModificador, Modificador> combinados = new LinkedHashMap<>();

        for (Modificador modificador : grupo.modifiers()) {
            combinados.put(new ClaveModificador(modificador.atributo(), modificador.operacion()), modificador);
        }
        for (Modificador modificador : especifico.modifiers()) {
            combinados.put(new ClaveModificador(modificador.atributo(), modificador.operacion()), modificador);
        }

        List<ReglaDamageType> reglas = new ArrayList<>(especifico.damageTypes());
        reglas.addAll(grupo.damageTypes());

        return new AtributosDataType(List.of(), List.copyOf(combinados.values()), List.copyOf(reglas),
                especifico.weaponMode().or(grupo::weaponMode));
    }

    /** Resuelve grupo + específico (cualquiera puede ser null). Devuelve null si no hay ninguno. */
    public static AtributosDataType resolver(AtributosDataType grupo, AtributosDataType especifico) {
        if (grupo == null) return especifico;
        if (especifico == null) return grupo;
        return reemplazarValores(grupo, especifico);
    }

}
