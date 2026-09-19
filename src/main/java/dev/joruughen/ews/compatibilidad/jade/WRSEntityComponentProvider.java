package dev.joruughen.ews.compatibilidad.jade;

import dev.joruughen.ews.Config;
import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import dev.joruughen.ews.atributos.Elemento;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;


public enum WRSEntityComponentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {

    INSTANCE;

    private static final int COLOR_POSITIVO = 0x0000FF;
    private static final int COLOR_NEGATIVO = 0xFF0000;

    int contador = 0;

    private final boolean DETALLES = Config.detalles;

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {

        // Si la tecla no está presionada muestra los daños de la entidad
        if (!Tecla.isVerMasPresionado()) {

            for (Elemento elemento : Elemento.values()) {
                addTooltipAtributo(iTooltip, entityAccessor, "ews.tooltip.damage",
                        Ews.MODID + ".value." + elemento.id(), elemento.displayName(), elemento.icono(), true);
            }

            contador = 0;

        } else { // Si la tecla está presionada muestra las resistencias y debilidades

            // Resistencias
            for (Elemento elemento : Elemento.values()) {
                addTooltipAtributo(iTooltip, entityAccessor, "ews.tooltip.resist",
                        Ews.MODID + ".value." + elemento.id() + "_resist", elemento.displayName() + " Resist", elemento.icono(), true);
            }

            contador = 0;

            // Debilidades
            for (Elemento elemento : Elemento.values()) {
                addTooltipAtributo(iTooltip, entityAccessor, "ews.tooltip.weak",
                        Ews.MODID + ".value." + elemento.id() + "_resist", elemento.displayName() + " Resist", elemento.icono(), false);
            }

            contador = 0;
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
        Object entityObj = entityAccessor.getEntity();
        if (entityObj instanceof LivingEntity entity) {

            for (Elemento elemento : Elemento.values()) {
                AttributeInstance damage = entity.getAttribute(Atributos.damage(elemento));
                if (damage != null) compoundTag.putDouble(elemento.displayName(), damage.getValue());

                AttributeInstance resist = entity.getAttribute(Atributos.resist(elemento));
                if (resist != null) compoundTag.putDouble(elemento.displayName() + " Resist", resist.getValue());
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(WRSPlugin.ATRIBUTOS);
    }

    // positivo = true: muestra daño/resistencia (valor > 0, color azul). positivo = false: muestra debilidades (valor < 0, color rojo).
    private void addTooltipAtributo(ITooltip iTooltip, EntityAccessor entityAccessor, String headerKey, String translationKey, String serverData, ItemStack itemStack, boolean positivo) {
        int valor = (int) entityAccessor.getServerData().getDouble(serverData);

        if (positivo ? valor <= 0 : valor >= 0) return;

        IElementHelper elements = iTooltip.getElementHelper();//.size aumenta el tamaño el primer valor es eje x y el segundo es eje y
        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -3));

        if (contador == 0) {
            iTooltip.add(Component.literal(" ").append(Component.translatable(headerKey).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))));
        }
        if (contador % 3 == 0) {
            contador++;
            iTooltip.add(icon);
        } else {
            contador++;
            iTooltip.append(icon);
        }

        int colorRgb = positivo ? COLOR_POSITIVO : COLOR_NEGATIVO;
        Component valorComponent = DETALLES
                ? Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(colorRgb))))
                : Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(colorRgb)));

        iTooltip.append(Component.literal(" ").append(valorComponent).append(Component.literal(" ")));
    }

    private String sinDetalles(String translationKey, int valor) {

        Component component = Component.translatable(translationKey, Component.literal(String.valueOf(valor)));
        String numeroString = component.getString().replaceAll("\\D", ""); // Eliminar todos los caracteres no numéricos de la cadena
        numeroString = numeroString.trim(); // Eliminar espacios en blanco alrededor del número

        return numeroString;// devuelve el numero solo como un String

    }

}
