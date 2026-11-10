package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.DataLoaderEvent;
import dev.joruughen.ews.data.Modificador;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = Ews.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class AgregarModificadorItem {

    /** Un modificador del datapack ya resuelto a un {@link Attribute} y a un {@link AttributeModifier} con UUID estable. */
    public record ModificadorAplicado(ResourceLocation idAtributo, Attribute atributo, AttributeModifier modificador) {
    }

    // Evento para agregar modificadores de atributo a los items. Forge dispara este evento para
    // cualquier ítem en cualquier slot de equipo (armadura, mano principal, mano secundaria), así que
    // ya no hace falta restringir por tipo de ítem: cada ítem aporta sus modificadores en el slot
    // donde efectivamente esté puesto.
    @SubscribeEvent
    public static void agregarModificadores(ItemAttributeModifierEvent event) {
        for (ModificadorAplicado aplicado : modificadoresBase(event.getItemStack(), event.getSlotType(), true)) {
            event.addModifier(aplicado.atributo(), aplicado.modificador());
        }
    }

    /**
     * Modificadores que el datapack le da a este ítem puesto en este slot (sin reglas por damage type).
     * Los UUID son deterministas, así {@code PerfilElemental} puede reconocer qué modificadores de la
     * entidad vienen de este ítem.
     */
    public static List<ModificadorAplicado> modificadoresBase(ItemStack itemStack, EquipmentSlot slot, boolean avisar) {
        if (itemStack.isEmpty()) return List.of();

        AtributosDataType resultado = DataLoaderEvent.datosItem(ForgeRegistries.ITEMS.getKey(itemStack.getItem()));
        if (resultado == null) return List.of();

        String origen = itemStack.getItem().getDescriptionId() + ":" + slot;
        List<Modificador> modificadores = resultado.modifiers();
        List<ModificadorAplicado> aplicados = new ArrayList<>();

        for (int i = 0; i < modificadores.size(); i++) {
            Modificador modificador = modificadores.get(i);

            if (!modificador.aplicaEnSlot(slot)) continue;

            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo());
            if (attribute == null) {
                if (avisar) Ews.LOGGER.warn("Atributo desconocido en datapack: {}", modificador.atributo());
                continue;
            }

            String nombre = modificador.atributo() + ":" + modificador.operacion() + ":" + slot;
            UUID uuid = UUID.nameUUIDFromBytes((origen + ":" + i + ":" + nombre).getBytes());

            aplicados.add(new ModificadorAplicado(modificador.atributo(), attribute,
                    new AttributeModifier(uuid, nombre, modificador.valorParaModificador(), modificador.operacion())));
        }
        return aplicados;
    }

}
