package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.DataLoaderEvent;
import dev.joruughen.ews.data.Modificador;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = Ews.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class AgregarModificadorItem {

    // Evento para agregar modificadores de atributo a los items. Forge dispara este evento para
    // cualquier ítem en cualquier slot de equipo (armadura, mano principal, mano secundaria), así que
    // ya no hace falta restringir por tipo de ítem: cada ítem aporta sus modificadores en el slot
    // donde efectivamente esté puesto.
    @SubscribeEvent
    public static void agregarModificadores(ItemAttributeModifierEvent event) {
        if (event.getItemStack().isEmpty()) return;

        ItemStack itemStack = event.getItemStack();

        ResourceLocation itemStackId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());

        if (itemStackId == null) {
            return;
        }

        AtributosDataType dataGrupo = DataLoaderEvent.GROUPS.getData().get(itemStackId);
        AtributosDataType dataItem = DataLoaderEvent.ITEMS.getData().get(itemStackId);

        AtributosDataType resultado;

        if (dataGrupo == null) {
            if (dataItem == null) {
                return;
            } else resultado = dataItem;
        } else resultado = dataGrupo;

        if (dataGrupo != null && dataItem != null) {
            resultado = AtributosDataType.reemplazarValores(dataGrupo, dataItem);
        }

        String origen = itemStack.getItem().getDescriptionId() + ":" + event.getSlotType();
        List<Modificador> modificadores = resultado.modifiers();

        for (int i = 0; i < modificadores.size(); i++) {
            Modificador modificador = modificadores.get(i);

            if (!modificador.aplicaEnSlot(event.getSlotType())) continue;

            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo());
            if (attribute == null) {
                Ews.LOGGER.warn("Atributo desconocido en datapack: {}", modificador.atributo());
                continue;
            }

            String nombre = modificador.atributo() + ":" + modificador.operacion() + ":" + event.getSlotType();
            UUID uuid = UUID.nameUUIDFromBytes((origen + ":" + i + ":" + nombre).getBytes());

            event.addModifier(attribute, new AttributeModifier(uuid, nombre, modificador.valorParaModificador(), modificador.operacion()));
        }
    }

}
