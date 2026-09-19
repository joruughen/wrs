package dev.joruughen.ews.cliente;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import dev.joruughen.ews.atributos.Elemento;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.Modificador;
import dev.joruughen.ews.network.ClientDataHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(
        modid = Ews.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class AgregarTooltip {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack itemStack = event.getItemStack();

        if (itemStack.isEmpty()) return;

        ResourceLocation itemStackId = ForgeRegistries.ITEMS.getKey(itemStack.getItem());

        if (itemStackId == null) {
            // Manejo de error: el item no tiene un ResourceLocation válido.
            return;
        }

        AtributosDataType dataGrupo = ClientDataHolder.getGroupsData(itemStackId);
        AtributosDataType dataItem = ClientDataHolder.getItemsData(itemStackId);

        AtributosDataType resultado;

        if (dataGrupo == null) {
            if (dataItem == null) {
                return;
            } else resultado = dataItem;
        } else resultado = dataGrupo;

        if (dataGrupo != null && dataItem != null) {
            resultado = AtributosDataType.reemplazarValores(dataGrupo, dataItem);
        }

        for (Modificador modificador : resultado.modifiers()) {
            agregarAtributo(event, modificador);
        }

    }

    // Función genérica para agregar atributos al tooltip
    private static void agregarAtributo(ItemTooltipEvent event, Modificador modificador) {
        if (modificador.valor() == 0) return;

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo());
        if (attribute == null) return;

        Elemento elemento = Atributos.elementoDe(modificador.atributo());
        ChatFormatting color = elemento != null ? elemento.color() : ChatFormatting.GRAY;

        String attributeName = I18n.get(attribute.getDescriptionId());
        ChatFormatting colorValor = (modificador.valor() > 0) ? ChatFormatting.BLUE : ChatFormatting.RED;
        String textoValor = modificador.operacion() == AttributeModifier.Operation.ADDITION
                ? String.valueOf(modificador.valor())
                : (modificador.valor() > 0 ? "+" : "") + modificador.valor() + "%";

        // Remover cualquier línea que contenga el atributo antes de agregarlo
        event.getToolTip().removeIf(component -> component.getString().contains(attributeName));

        event.getToolTip().add(Component.literal(attributeName + ": ").withStyle(color).append(Component.literal(textoValor).withStyle(colorValor)));
    }

}
