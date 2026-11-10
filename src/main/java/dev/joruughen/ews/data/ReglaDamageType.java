package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.joruughen.ews.atributos.Atributos;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Perfil elemental alternativo de una entidad o ítem para ciertos damage types. Se define dentro de
 * "damage_types" en los archivos de entities/groups/items:
 *
 * <pre>
 * { "types": ["minecraft:magic", "#minecraft:is_fire"], "mode": "replace", "keep_bonuses": true,
 *   "modifiers": [ { "attribute": "fire", "value": 100 } ] }
 * </pre>
 *
 * Los modificadores de ataque se usan cuando la entidad/ítem hace daño de ese tipo; los de resistencia
 * cuando lo recibe.
 */
public record ReglaDamageType(
        List<ObjetivoDamageType> types,
        Modo mode,
        boolean keepBonuses,
        Optional<ModoArma> weaponMode,
        List<Modificador> modifiers
) {

    public enum Modo {
        /** Ataque: reemplaza todo el perfil de ataque. Resistencias: reemplaza solo las que nombra la regla. */
        REPLACE,
        /** Suma los valores de la regla a los que ya tiene la entidad/ítem. */
        ADD
    }

    private static final Codec<Modo> MODO_CODEC = Codec.STRING.flatXmap(
            texto -> switch (texto) {
                case "replace" -> DataResult.success(Modo.REPLACE);
                case "add" -> DataResult.success(Modo.ADD);
                default -> DataResult.error(() -> "Modo de regla desconocido: '" + texto + "' (usar replace o add)");
            },
            modo -> DataResult.success(modo == Modo.REPLACE ? "replace" : "add")
    );

    public static final Codec<ReglaDamageType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ObjetivoDamageType.CODEC.listOf().fieldOf("types").forGetter(ReglaDamageType::types),
            MODO_CODEC.optionalFieldOf("mode", Modo.REPLACE).forGetter(ReglaDamageType::mode),
            Codec.BOOL.optionalFieldOf("keep_bonuses", true).forGetter(ReglaDamageType::keepBonuses),
            ModoArma.CODEC.optionalFieldOf("weapon_mode").forGetter(ReglaDamageType::weaponMode),
            Modificador.CODEC.listOf().optionalFieldOf("modifiers", List.of()).forGetter(ReglaDamageType::modifiers)
    ).apply(instance, ReglaDamageType::new));

    /** true si la regla define algún atributo de ataque (resist = false) o de resistencia (resist = true) de EWS. */
    public boolean tieneModificadores(boolean resist) {
        for (Modificador modificador : modifiers) {
            if ((resist ? Atributos.resistenciaDe(modificador.atributo()) : Atributos.ataqueDe(modificador.atributo())) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * true si la regla tiene algo que aplicar en esa categoría: modificadores, o (solo en ataque) un
     * weapon_mode. Una regla con solo weapon_mode no toca el perfil base, solo cómo se combina con el arma.
     */
    public boolean afecta(boolean resist) {
        return tieneModificadores(resist) || (!resist && weaponMode.isPresent());
    }

    public String tiposTexto() {
        return types.stream().map(ObjetivoDamageType::nombreCorto).collect(Collectors.joining(", "));
    }

    /**
     * Busca la regla que aplica a este damage type entre las que afectan a la categoría pedida
     * (ataque o resistencia). Un id exacto gana sobre un tag; a igual precisión gana la primera de la
     * lista (las del archivo específico van antes que las de los grupos).
     */
    public static ReglaDamageType buscar(List<ReglaDamageType> reglas, Holder<DamageType> tipo, boolean resist) {
        if (reglas.isEmpty() || tipo == null) return null;

        ReglaDamageType porTag = null;
        for (ReglaDamageType regla : reglas) {
            if (!regla.afecta(resist)) continue;
            for (ObjetivoDamageType objetivo : regla.types()) {
                if (!objetivo.coincide(tipo)) continue;
                if (!objetivo.esTag()) return regla;
                if (porTag == null) porTag = regla;
            }
        }
        return porTag;
    }
}
