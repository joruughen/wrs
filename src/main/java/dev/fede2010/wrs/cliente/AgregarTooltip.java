package dev.fede2010.wrs.cliente;

import dev.fede2010.wrs.Wrs;
import dev.fede2010.wrs.atributos.Atributos;
import dev.fede2010.wrs.data.AtributosDataType;
import dev.fede2010.wrs.network.ClientDataHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;


@Mod.EventBusSubscriber(
        modid = Wrs.MODID,
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

        if (dataGrupo == null){
            if (dataItem == null){
                return;
            }else resultado = dataItem;
        }else resultado = dataGrupo;

        if (dataGrupo != null && dataItem != null) {
            resultado = AtributosDataType.reemplazarValores(dataGrupo, dataItem);
        }

        // Daños
        agregarAtributo(event, Atributos.SLASH.get().getDescriptionId(), ChatFormatting.GRAY, resultado.damage().getSlash());
        agregarAtributo(event, Atributos.STRIKE.get().getDescriptionId(), ChatFormatting.GRAY, resultado.damage().getStrike());
        agregarAtributo(event, Atributos.PIERCE.get().getDescriptionId(), ChatFormatting.GRAY, resultado.damage().getPierce());
        agregarAtributo(event, Atributos.FIRE.get().getDescriptionId(), ChatFormatting.RED, resultado.damage().getFire());
        agregarAtributo(event, Atributos.ICE.get().getDescriptionId(), ChatFormatting.AQUA, resultado.damage().getIce());
        agregarAtributo(event, Atributos.LIGHTING.get().getDescriptionId(), ChatFormatting.YELLOW, resultado.damage().getLighting());
        agregarAtributo(event, Atributos.AQUA.get().getDescriptionId(), ChatFormatting.BLUE, resultado.damage().getAqua());
        agregarAtributo(event, Atributos.HOLY.get().getDescriptionId(), ChatFormatting.WHITE, resultado.damage().getHoly());
        agregarAtributo(event, Atributos.ENDER.get().getDescriptionId(), ChatFormatting.DARK_PURPLE, resultado.damage().getEnder());
        agregarAtributo(event, Atributos.BLOOD.get().getDescriptionId(), ChatFormatting.DARK_RED, resultado.damage().getBlood());
        agregarAtributo(event, Atributos.EVOCATION.get().getDescriptionId(), ChatFormatting.LIGHT_PURPLE, resultado.damage().getEvocation());
        agregarAtributo(event, Atributos.NATURE.get().getDescriptionId(), ChatFormatting.GREEN, resultado.damage().getNature());
        agregarAtributo(event, Atributos.ELDRITCH.get().getDescriptionId(), ChatFormatting.DARK_GREEN, resultado.damage().getEldritch());

        // Resistencias
        agregarAtributo(event, Atributos.SLASH_RESIST.get().getDescriptionId(), ChatFormatting.GRAY, resultado.resistance().getSlash());
        agregarAtributo(event, Atributos.STRIKE_RESIST.get().getDescriptionId(), ChatFormatting.GRAY, resultado.resistance().getStrike());
        agregarAtributo(event, Atributos.PIERCE_RESIST.get().getDescriptionId(), ChatFormatting.GRAY, resultado.resistance().getPierce());
        agregarAtributo(event, Atributos.FIRE_RESIST.get().getDescriptionId(), ChatFormatting.RED, resultado.resistance().getFire());
        agregarAtributo(event, Atributos.ICE_RESIST.get().getDescriptionId(), ChatFormatting.AQUA, resultado.resistance().getIce());
        agregarAtributo(event, Atributos.LIGHTING_RESIST.get().getDescriptionId(), ChatFormatting.YELLOW, resultado.resistance().getLighting());
        agregarAtributo(event, Atributos.AQUA_RESIST.get().getDescriptionId(), ChatFormatting.BLUE, resultado.resistance().getAqua());
        agregarAtributo(event, Atributos.HOLY_RESIST.get().getDescriptionId(), ChatFormatting.WHITE, resultado.resistance().getHoly());
        agregarAtributo(event, Atributos.ENDER_RESIST.get().getDescriptionId(), ChatFormatting.DARK_PURPLE, resultado.resistance().getEnder());
        agregarAtributo(event, Atributos.BLOOD_RESIST.get().getDescriptionId(), ChatFormatting.DARK_RED, resultado.resistance().getBlood());
        agregarAtributo(event, Atributos.EVOCATION_RESIST.get().getDescriptionId(), ChatFormatting.LIGHT_PURPLE, resultado.resistance().getEvocation());
        agregarAtributo(event, Atributos.NATURE_RESIST.get().getDescriptionId(), ChatFormatting.GREEN, resultado.resistance().getNature());
        agregarAtributo(event, Atributos.ELDRITCH_RESIST.get().getDescriptionId(), ChatFormatting.DARK_GREEN, resultado.resistance().getEldritch());

    }

    // Función genérica para agregar atributos al tooltip
    private static void agregarAtributo(ItemTooltipEvent event, String atributoId, ChatFormatting color, double valorAtributo) {
        if (valorAtributo != 0) {
            String attributeName = I18n.get(atributoId);
            ChatFormatting colorValor = (valorAtributo > 0) ? ChatFormatting.BLUE : ChatFormatting.RED;

            // Remover cualquier línea que contenga el atributo antes de agregarlo
            event.getToolTip().removeIf(component -> component.getString().contains(attributeName));

            event.getToolTip().add(Component.literal(attributeName + ": ").withStyle(color).append(Component.literal("" + valorAtributo).withStyle(colorValor)));
        }
    }

}
