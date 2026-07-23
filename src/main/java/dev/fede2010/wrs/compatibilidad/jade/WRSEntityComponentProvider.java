package dev.fede2010.wrs.compatibilidad.jade;

import dev.fede2010.wrs.Config;
import dev.fede2010.wrs.Wrs;
import dev.fede2010.wrs.atributos.Atributos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

    int contador = 0;

    private final boolean DETALLES = Config.detalles;

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {

        // Si la tecla no está presionada muestra los daños de la entidad
        if(!Tecla.isVerMasPresionado()){

            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.slash", "Slash", new ItemStack(Items.DIAMOND_SWORD));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.strike", "Strike", new ItemStack(Items.DIAMOND_AXE));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.pierce", "Pierce", new ItemStack(Items.ARROW));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.fire", "Fire", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:healing");
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.ice", "Ice", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:long_swiftness");
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.lighting", "Lighting", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:long_fire_resistance");
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.aqua", "Aqua", new ItemStack(Items.WATER_BUCKET));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.holy", "Holy", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:invisibility");
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.ender", "Ender", new ItemStack(Items.ENDER_PEARL));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.blood", "Blood", new ItemStack(Items.REDSTONE));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.evocation", "Evocation", new ItemStack(Items.ENCHANTED_BOOK));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.nature", "Nature", new ItemStack(Items.OAK_SAPLING));
            addTooltipAtributoAtaque(iTooltip, entityAccessor, Wrs.MODID + ".value.eldritch", "Eldritch", new ItemStack(Items.SCULK));

            contador = 0;

        } else { // Si la tecla está presionada muestra las resistencias y debilidades

            // Resistencias
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.slash_resist", "Slash Resist", new ItemStack(Items.DIAMOND_SWORD));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.strike_resist", "Strike Resist", new ItemStack(Items.DIAMOND_AXE));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.pierce_resist", "Pierce Resist", new ItemStack(Items.ARROW));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.fire_resist", "Fire Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:healing");
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.ice_resist", "Ice Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:long_swiftness");
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.lighting_resist", "Lighting Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:long_fire_resistance");
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.aqua_resist", "Aqua Resist", new ItemStack(Items.WATER_BUCKET));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.holy_resist", "Holy Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:invisibility");
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.ender_resist", "Ender Resist", new ItemStack(Items.ENDER_PEARL));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.blood_resist", "Blood Resist", new ItemStack(Items.REDSTONE));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.evocation_resist", "Evocation Resist", new ItemStack(Items.ENCHANTED_BOOK));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.nature_resist", "Nature Resist", new ItemStack(Items.OAK_SAPLING));
            addTooltipAtributoResistencia(iTooltip, entityAccessor, Wrs.MODID + ".value.eldritch_resist", "Eldritch Resist", new ItemStack(Items.SCULK));

            contador = 0;

            // Debilidades
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.slash_resist", "Slash Resist", new ItemStack(Items.DIAMOND_SWORD));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.strike_resist", "Strike Resist", new ItemStack(Items.DIAMOND_AXE));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.pierce_resist", "Pierce Resist", new ItemStack(Items.ARROW));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.fire_resist", "Fire Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:healing");
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.ice_resist", "Ice Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:long_swiftness");
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.lighting_resist", "Lighting Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:long_fire_resistance");
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.aqua_resist", "Aqua Resist", new ItemStack(Items.WATER_BUCKET));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.holy_resist", "Holy Resist", new ItemStack(Items.LINGERING_POTION), "Potion", "minecraft:invisibility");
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.ender_resist", "Ender Resist", new ItemStack(Items.ENDER_PEARL));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.blood_resist", "Blood Resist", new ItemStack(Items.REDSTONE));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.evocation_resist", "Evocation Resist", new ItemStack(Items.ENCHANTED_BOOK));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.nature_resist", "Nature Resist", new ItemStack(Items.OAK_SAPLING));
            addTooltipAtributoDebilidad(iTooltip, entityAccessor, Wrs.MODID + ".value.eldritch_resist", "Eldritch Resist", new ItemStack(Items.SCULK));

            contador = 0;
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
        Object entityObj = entityAccessor.getEntity();
        if (entityObj instanceof LivingEntity entity) {

            AttributeInstance slash = entity.getAttribute(Atributos.SLASH.get());
            AttributeInstance strike = entity.getAttribute(Atributos.STRIKE.get());
            AttributeInstance pierce = entity.getAttribute(Atributos.PIERCE.get());
            AttributeInstance fire = entity.getAttribute(Atributos.FIRE.get());
            AttributeInstance ice = entity.getAttribute(Atributos.ICE.get());
            AttributeInstance lighting = entity.getAttribute(Atributos.LIGHTING.get());
            AttributeInstance aqua = entity.getAttribute(Atributos.AQUA.get());
            AttributeInstance holy = entity.getAttribute(Atributos.HOLY.get());
            AttributeInstance ender = entity.getAttribute(Atributos.ENDER.get());
            AttributeInstance blood = entity.getAttribute(Atributos.BLOOD.get());
            AttributeInstance evocation = entity.getAttribute(Atributos.EVOCATION.get());
            AttributeInstance nature = entity.getAttribute(Atributos.NATURE.get());
            AttributeInstance eldritch = entity.getAttribute(Atributos.ELDRITCH.get());

            AttributeInstance slashResist = entity.getAttribute(Atributos.SLASH_RESIST.get());
            AttributeInstance strikeResist = entity.getAttribute(Atributos.STRIKE_RESIST.get());
            AttributeInstance pierceResist = entity.getAttribute(Atributos.PIERCE_RESIST.get());
            AttributeInstance fireResist = entity.getAttribute(Atributos.FIRE_RESIST.get());
            AttributeInstance iceResist = entity.getAttribute(Atributos.ICE_RESIST.get());
            AttributeInstance lightingResist = entity.getAttribute(Atributos.LIGHTING_RESIST.get());
            AttributeInstance aquaResist = entity.getAttribute(Atributos.AQUA_RESIST.get());
            AttributeInstance holyResist = entity.getAttribute(Atributos.HOLY_RESIST.get());
            AttributeInstance enderResist = entity.getAttribute(Atributos.ENDER_RESIST.get());
            AttributeInstance bloodResist = entity.getAttribute(Atributos.BLOOD_RESIST.get());
            AttributeInstance evocationResist = entity.getAttribute(Atributos.EVOCATION_RESIST.get());
            AttributeInstance natureResist = entity.getAttribute(Atributos.NATURE_RESIST.get());
            AttributeInstance eldritchResist = entity.getAttribute(Atributos.ELDRITCH_RESIST.get());

            if(slash != null) compoundTag.putDouble("Slash", slash.getValue());
            if(strike != null) compoundTag.putDouble("Strike", strike.getValue());
            if(pierce != null) compoundTag.putDouble("Pierce", pierce.getValue());
            if(fire != null) compoundTag.putDouble("Fire", fire.getValue());
            if(ice != null) compoundTag.putDouble("Ice", ice.getValue());
            if(lighting != null) compoundTag.putDouble("Lighting", lighting.getValue());
            if(aqua != null) compoundTag.putDouble("Aqua", aqua.getValue());
            if(holy != null) compoundTag.putDouble("Holy", holy.getValue());
            if(ender != null) compoundTag.putDouble("Ender", ender.getValue());
            if(blood != null) compoundTag.putDouble("Blood", blood.getValue());
            if(evocation != null) compoundTag.putDouble("Evocation", evocation.getValue());
            if(nature != null) compoundTag.putDouble("Nature", nature.getValue());
            if(eldritch != null) compoundTag.putDouble("Eldritch", eldritch.getValue());

            if(slashResist != null) compoundTag.putDouble("Slash Resist", slashResist.getValue());
            if(strikeResist != null) compoundTag.putDouble("Strike Resist", strikeResist.getValue());
            if(pierceResist != null) compoundTag.putDouble("Pierce Resist", pierceResist.getValue());
            if(fireResist != null) compoundTag.putDouble("Fire Resist", fireResist.getValue());
            if(iceResist != null) compoundTag.putDouble("Ice Resist", iceResist.getValue());
            if(lightingResist != null) compoundTag.putDouble("Lighting Resist", lightingResist.getValue());
            if(aquaResist != null) compoundTag.putDouble("Aqua Resist", aquaResist.getValue());
            if(holyResist != null) compoundTag.putDouble("Holy Resist", holyResist.getValue());
            if(enderResist != null) compoundTag.putDouble("Ender Resist", enderResist.getValue());
            if(bloodResist != null) compoundTag.putDouble("Blood Resist", bloodResist.getValue());
            if(evocationResist != null) compoundTag.putDouble("Evocation Resist", evocationResist.getValue());
            if(natureResist != null) compoundTag.putDouble("Nature Resist", natureResist.getValue());
            if(eldritchResist != null) compoundTag.putDouble("Eldritch Resist", eldritchResist.getValue());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return new ResourceLocation(WRSPlugin.ATRIBUTOS);
    }

    private void addTooltipAtributoAtaque(ITooltip iTooltip, EntityAccessor entityAccessor, String translationKey, String serverData, ItemStack itemStack) {
        int valor = (int)entityAccessor.getServerData().getDouble(serverData);

        IElementHelper elements = iTooltip.getElementHelper();//.size aumenta el tamaño el primer valor es eje x y el segundo es eje y
        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -3));

        if (valor > 0) {
            if (contador == 0){
                iTooltip.add(Component.literal(" ").append(Component.translatable("wrs.tooltip.damage").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))));
            }
            if (contador % 3 == 0) {
                contador++;
                iTooltip.add(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                }
            }else {
                contador++;
                iTooltip.append(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                }
            }
        }
    }

    private void addTooltipAtributoAtaque(ITooltip iTooltip, EntityAccessor entityAccessor, String translationKey, String serverData, ItemStack itemStack, String tagArg1, String tagArg2) {
        int valor = (int)entityAccessor.getServerData().getDouble(serverData);

        IElementHelper elements = iTooltip.getElementHelper();

        CompoundTag potionTag2 = itemStack.getOrCreateTag();
        potionTag2.putString(tagArg1, tagArg2);

        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -5));

        if (valor > 0) {
            if (contador == 0){
                iTooltip.add(Component.literal(" ").append(Component.translatable("wrs.tooltip.damage").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))));
            }
            if (contador % 3 == 0) {
                contador++;
                iTooltip.add(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                }
            }else {
                contador++;
                iTooltip.append(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                }
            }
        }
    }

    private void addTooltipAtributoResistencia(ITooltip iTooltip, EntityAccessor entityAccessor, String translationKey, String serverData, ItemStack itemStack) {
        int valor = (int)entityAccessor.getServerData().getDouble(serverData);

        IElementHelper elements = iTooltip.getElementHelper();
        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -3));

            if (valor > 0) {
                if(contador == 0){
                    iTooltip.add(Component.translatable("wrs.tooltip.resist").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))));
                }
                if (contador % 3 == 0) {
                    contador++;
                    iTooltip.add(icon);
                    if (DETALLES){
                        iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                    }else {
                        iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                    }
                }else {
                    contador++;
                    iTooltip.append(icon);
                    if (DETALLES){
                        iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                    }else {
                        iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                    }
                }
            }
    }

    private void addTooltipAtributoResistencia(ITooltip iTooltip, EntityAccessor entityAccessor, String translationKey, String serverData, ItemStack itemStack, String tagArg1 , String tagArg2) {
        int valor = (int)entityAccessor.getServerData().getDouble(serverData);

        IElementHelper elements = iTooltip.getElementHelper();

        CompoundTag potionTag2 = itemStack.getOrCreateTag();
        potionTag2.putString(tagArg1, tagArg2);

        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -5));

        if (valor > 0) {
            if(contador == 0){
                iTooltip.add(Component.translatable("wrs.tooltip.resist").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))));
            }
            if (contador % 3 == 0) {
                contador++;
                iTooltip.add(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                }
            }else {
                contador++;
                iTooltip.append(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x0000FF)))).append(Component.literal(" ")));
                }
            }
        }
    }

    private void addTooltipAtributoDebilidad(ITooltip iTooltip, EntityAccessor entityAccessor, String translationKey, String serverData, ItemStack itemStack) {
        int valor = (int)entityAccessor.getServerData().getDouble(serverData);

        IElementHelper elements = iTooltip.getElementHelper();
        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -3));

        if (valor < 0) {
            if(contador == 0){
                iTooltip.add(Component.translatable("wrs.tooltip.weak").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))));
            }
            if (contador % 3 == 0) {
                contador++;
                iTooltip.add(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)))).append(Component.literal(" ")));
                }
            }else {
                contador++;
                iTooltip.append(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)))).append(Component.literal(" ")));
                }
            }
        }
    }

    private void addTooltipAtributoDebilidad(ITooltip iTooltip, EntityAccessor entityAccessor, String translationKey, String serverData, ItemStack itemStack, String tagArg1, String tagArg2) {
        int valor = (int)entityAccessor.getServerData().getDouble(serverData);

        IElementHelper elements = iTooltip.getElementHelper();

        CompoundTag potionTag2 = itemStack.getOrCreateTag();
        potionTag2.putString(tagArg1, tagArg2);

        IElement icon = elements.item(itemStack, 0.75f).size(new Vec2(10, 14)).translate(new Vec2(-3, -5));

        if (valor < 0) {
            if(contador == 0){
                iTooltip.add(Component.translatable("wrs.tooltip.weak").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))));
            }
            if (contador % 3 == 0) {
                contador++;
                iTooltip.add(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)))).append(Component.literal(" ")));
                }
            }else {
                contador++;
                iTooltip.append(icon);
                if (DETALLES){
                    iTooltip.append(Component.literal(" ").append(Component.translatable(translationKey, Component.literal(String.valueOf(valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))))).append(Component.literal(" ")));
                }else {
                    iTooltip.append(Component.literal(" ").append(Component.literal(sinDetalles(translationKey, valor)).setStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000)))).append(Component.literal(" ")));
                }
            }
        }
    }

    private String sinDetalles(String translationKey, int valor){

        Component component = Component.translatable(translationKey, Component.literal(String.valueOf(valor)));
        String numeroString = component.getString().replaceAll("\\D", ""); // Eliminar todos los caracteres no numéricos de la cadena
        numeroString = numeroString.trim(); // Eliminar espacios en blanco alrededor del número

        return numeroString;// devuelve el numero solo como un String

    }

}
