package dev.joruughen.ews.compatibilidad.jade;

import dev.joruughen.ews.Config;
import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import dev.joruughen.ews.atributos.Elemento;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.Modificador;
import dev.joruughen.ews.data.ModoArma;
import dev.joruughen.ews.data.ReglaDamageType;
import dev.joruughen.ews.network.ClientDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.registries.ForgeRegistries;
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

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {

        // Si la tecla no está presionada muestra los daños de la entidad
        if (!Tecla.isVerMasPresionado()) {

            for (Elemento elemento : Elemento.values()) {
                addTooltipAtributo(iTooltip, entityAccessor, "ews.tooltip.damage",
                        Ews.MODID + ".value." + elemento.id(), elemento.displayName(), elemento.icono(), true);
            }

            contador = 0;

            if (Config.mostrarReglas) agregarReglas(iTooltip, entityAccessor, false);

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

            if (Config.mostrarReglas) agregarReglas(iTooltip, entityAccessor, true);
        }
    }

    // Reglas por damage type de la entidad (datapack sincronizado al cliente): "Con daño magic:" + íconos con valores
    private void agregarReglas(ITooltip iTooltip, EntityAccessor entityAccessor, boolean resist) {
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entityAccessor.getEntity().getType());
        if (entityId == null) return;

        AtributosDataType datos = AtributosDataType.resolver(
                ClientDataHolder.getGroupsData(entityId), ClientDataHolder.getEntitiesData(entityId));
        if (datos == null) return;

        IElementHelper elements = iTooltip.getElementHelper();
        Style estiloEncabezado = Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00));

        // weapon_mode de la entidad (cómo combina su ataque con el arma de la mano)
        if (!resist && datos.weaponMode().isPresent() && datos.weaponMode().get() != ModoArma.ADD) {
            iTooltip.add(Component.literal(" ").append(textoModoArma(datos.weaponMode().get()).setStyle(estiloEncabezado)));
        }

        for (ReglaDamageType regla : datos.damageTypes()) {
            if (!regla.afecta(resist)) continue;

            String clave = Ews.MODID + (regla.mode() == ReglaDamageType.Modo.ADD ? ".tooltip.rule_add" : ".tooltip.rule");
            MutableComponent encabezado = Component.translatable(clave, regla.tiposTexto());
            if (!resist && regla.weaponMode().isPresent()) {
                encabezado.append(" ").append(textoModoArma(regla.weaponMode().get()));
            }
            iTooltip.add(Component.literal(" ").append(encabezado.setStyle(estiloEncabezado)));

            int columna = 0;
            for (Modificador modificador : regla.modifiers()) {
                Elemento elemento = resist ? Atributos.resistenciaDe(modificador.atributo()) : Atributos.ataqueDe(modificador.atributo());
                // En entidades solo cuenta "addition" (igual que en su perfil default)
                if (elemento == null || modificador.operacion() != AttributeModifier.Operation.ADDITION) continue;

                int valor = (int) modificador.valor();
                if (valor == 0) continue;

                IElement icon = elements.item(elemento.icono(), 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -3));
                if (columna % 3 == 0) {
                    iTooltip.add(icon);
                } else {
                    iTooltip.append(icon);
                }
                columna++;

                String translationKey = Ews.MODID + ".value." + elemento.id() + (resist ? "_resist" : "");
                iTooltip.append(Component.literal(" ").append(valorComponent(translationKey, valor, valor > 0 ? COLOR_POSITIVO : COLOR_NEGATIVO))
                        .append(Component.literal(" ")));
            }
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
        iTooltip.append(Component.literal(" ").append(valorComponent(translationKey, valor, colorRgb)).append(Component.literal(" ")));
    }

    private static MutableComponent textoModoArma(ModoArma modo) {
        return Component.translatable(Ews.MODID + ".tooltip.weapon_mode." + modo.id());
    }

    private Component valorComponent(String translationKey, int valor, int colorRgb) {
        return Config.detalles
                ? Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(colorRgb))))
                : Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(colorRgb)));
    }

    private String sinDetalles(String translationKey, int valor) {

        Component component = Component.translatable(translationKey, Component.literal(String.valueOf(valor)));
        String numeroString = component.getString().replaceAll("\\D", ""); // Eliminar todos los caracteres no numéricos de la cadena
        numeroString = numeroString.trim(); // Eliminar espacios en blanco alrededor del número

        return numeroString;// devuelve el numero solo como un String

    }

}
