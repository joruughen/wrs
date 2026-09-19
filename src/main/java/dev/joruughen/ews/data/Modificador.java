package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.joruughen.ews.Ews;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.List;

/**
 * Un modificador de atributo tal como se define en el datapack: a qué atributo apunta (propio de EWS
 * o de cualquier otro mod, referenciado por su ResourceLocation), con qué operación de Minecraft se
 * aplica, su valor, y opcionalmente en qué slots de equipo aplica (para ítems; para entidades se ignora).
 * Para "addition" el valor es una cantidad plana; para "multiply_base"/"multiply_total" el valor es un
 * porcentaje (10 = +10%), convertido internamente a la fracción que espera Minecraft.
 */
public record Modificador(ResourceLocation atributo, AttributeModifier.Operation operacion, double valor, List<EquipmentSlot> slots) {

    private static final Codec<ResourceLocation> ATRIBUTO_CODEC = Codec.STRING.xmap(
            texto -> texto.indexOf(':') >= 0 ? new ResourceLocation(texto) : new ResourceLocation(Ews.MODID, texto),
            ResourceLocation::toString
    );

    private static final Codec<AttributeModifier.Operation> OPERACION_CODEC = Codec.STRING.flatXmap(
            texto -> switch (texto) {
                case "addition" -> DataResult.success(AttributeModifier.Operation.ADDITION);
                case "multiply_base" -> DataResult.success(AttributeModifier.Operation.MULTIPLY_BASE);
                case "multiply_total" -> DataResult.success(AttributeModifier.Operation.MULTIPLY_TOTAL);
                default -> DataResult.error(() -> "Operación de atributo desconocida: '" + texto + "' (usar addition, multiply_base o multiply_total)");
            },
            operacion -> switch (operacion) {
                case ADDITION -> DataResult.success("addition");
                case MULTIPLY_BASE -> DataResult.success("multiply_base");
                case MULTIPLY_TOTAL -> DataResult.success("multiply_total");
            }
    );

    private static final Codec<EquipmentSlot> SLOT_CODEC = Codec.STRING.flatXmap(
            texto -> switch (texto) {
                case "head" -> DataResult.success(EquipmentSlot.HEAD);
                case "chest" -> DataResult.success(EquipmentSlot.CHEST);
                case "legs" -> DataResult.success(EquipmentSlot.LEGS);
                case "feet" -> DataResult.success(EquipmentSlot.FEET);
                case "main" -> DataResult.success(EquipmentSlot.MAINHAND);
                case "off" -> DataResult.success(EquipmentSlot.OFFHAND);
                default -> DataResult.error(() -> "Slot desconocido: '" + texto + "' (usar head, chest, legs, feet, main u off)");
            },
            slot -> switch (slot) {
                case HEAD -> DataResult.success("head");
                case CHEST -> DataResult.success("chest");
                case LEGS -> DataResult.success("legs");
                case FEET -> DataResult.success("feet");
                case MAINHAND -> DataResult.success("main");
                case OFFHAND -> DataResult.success("off");
            }
    );

    public static final Codec<Modificador> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ATRIBUTO_CODEC.fieldOf("attribute").forGetter(Modificador::atributo),
            OPERACION_CODEC.optionalFieldOf("operation", AttributeModifier.Operation.ADDITION).forGetter(Modificador::operacion),
            Codec.DOUBLE.fieldOf("value").forGetter(Modificador::valor),
            SLOT_CODEC.listOf().optionalFieldOf("slots", List.of()).forGetter(Modificador::slots)
    ).apply(instance, Modificador::new));

    /** Valor listo para un {@link AttributeModifier}: en operaciones porcentuales, 10 (=10%) pasa a 0.10. */
    public double valorParaModificador() {
        return operacion == AttributeModifier.Operation.ADDITION ? valor : valor / 100.0;
    }

    /** Sin "slots" definidos, el modificador aplica en cualquier slot (comportamiento por defecto). */
    public boolean aplicaEnSlot(EquipmentSlot slot) {
        return slots.isEmpty() || slots.contains(slot);
    }
}
