package dev.joruughen.ews.cliente;

import dev.joruughen.ews.Config;
import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import dev.joruughen.ews.atributos.Elemento;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.Modificador;
import dev.joruughen.ews.data.ReglaDamageType;
import dev.joruughen.ews.network.ClientDataHolder;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;


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

        AtributosDataType resultado = AtributosDataType.resolver(
                ClientDataHolder.getGroupsData(itemStackId), ClientDataHolder.getItemsData(itemStackId));
        if (resultado == null) return;

        for (Modificador modificador : resultado.modifiers()) {
            agregarAtributo(event, modificador);
        }

        if (Config.mostrarReglas) {
            for (ReglaDamageType regla : resultado.damageTypes()) {
                agregarRegla(event, regla);
            }
        }
    }

    // Función genérica para agregar atributos al tooltip
    private static void agregarAtributo(ItemTooltipEvent event, Modificador modificador) {
        MutableComponent linea = linea(modificador);
        if (linea == null) return;

        // Se compara con el nombre completo (el que generan vanilla/Apothic); la línea propia usa el nombre corto
        String attributeName = I18n.get(ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo()).getDescriptionId());
        event.getToolTip().removeIf(component -> component.getString().contains(attributeName));

        event.getToolTip().add(linea);
    }

    // Una regla por damage type: encabezado "Con daño magic:" y debajo sus modificadores
    private static void agregarRegla(ItemTooltipEvent event, ReglaDamageType regla) {
        String clave = Ews.MODID + (regla.mode() == ReglaDamageType.Modo.ADD ? ".tooltip.rule_add" : ".tooltip.rule");
        event.getToolTip().add(Component.translatable(clave, regla.tiposTexto()).withStyle(ChatFormatting.GRAY));

        for (Modificador modificador : regla.modifiers()) {
            MutableComponent linea = linea(modificador);
            if (linea != null) {
                event.getToolTip().add(Component.literal("  ").append(linea));
            }
        }
    }

    private static MutableComponent linea(Modificador modificador) {
        if (modificador.valor() == 0) return null;

        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo());
        if (attribute == null) return null;

        Elemento elemento = Atributos.elementoDe(modificador.atributo());
        ChatFormatting color = elemento != null ? elemento.color() : ChatFormatting.GRAY;

        String claveCorta = attribute.getDescriptionId() + ".short";
        String nombreMostrado = I18n.exists(claveCorta) ? I18n.get(claveCorta) : I18n.get(attribute.getDescriptionId());
        ChatFormatting colorValor = (modificador.valor() > 0) ? ChatFormatting.BLUE : ChatFormatting.RED;
        String textoValor = modificador.operacion() == AttributeModifier.Operation.ADDITION
                ? String.valueOf(modificador.valor())
                : (modificador.valor() > 0 ? "+" : "") + modificador.valor() + "%";

        return Component.literal(nombreMostrado + ": ").withStyle(color).append(Component.literal(textoValor).withStyle(colorValor));
    }

    /**
     * Última pasada sobre el tooltip (prioridad baja, después de vanilla, Apothic y otros mods):
     * toda línea que todavía nombre un atributo de EWS con su nombre completo ("E. Daño de Corte",
     * el que se ve en el menú de Apothic) se reescribe con el nombre corto y el color del elemento.
     * Cubre modificadores que no vienen del datapack (NBT, otros mods, valores combinados de Apothic).
     */
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onTooltipLimpiarPrefijo(ItemTooltipEvent event) {
        List<Component> lineas = event.getToolTip();
        for (int i = 0; i < lineas.size(); i++) {
            Component reescrita = reescribir(lineas.get(i));
            if (reescrita != null) lineas.set(i, reescrita);
        }
    }

    private static Component reescribir(Component linea) {
        String texto = linea.getString();
        if (texto.isEmpty()) return null;

        for (Elemento elemento : Elemento.values()) {
            for (boolean resist : new boolean[]{false, true}) {
                Attribute attribute = Atributos.de(elemento, resist);
                String nombreCompleto = I18n.get(attribute.getDescriptionId());
                int pos = texto.indexOf(nombreCompleto);
                if (pos < 0) continue;

                String claveCorta = attribute.getDescriptionId() + ".short";
                String nombreCorto = I18n.exists(claveCorta) ? I18n.get(claveCorta) : nombreCompleto;
                Style estilo = linea.getStyle();

                return Component.literal(texto.substring(0, pos)).withStyle(estilo)
                        .append(Component.literal(nombreCorto).withStyle(elemento.color()))
                        .append(Component.literal(texto.substring(pos + nombreCompleto.length())).withStyle(estilo));
            }
        }
        return null;
    }

}
