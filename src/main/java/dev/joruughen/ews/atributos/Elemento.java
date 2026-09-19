package dev.joruughen.ews.atributos;

import dev.joruughen.ews.data.AtributosData;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Fuente única de verdad para los 13 tipos elementales del mod: id de atributo,
 * color de tooltip, acceso a su valor dentro de {@link AtributosData} e ícono para Jade.
 */
public enum Elemento {
    SLASH("slash", ChatFormatting.WHITE, AtributosData::getSlash, () -> new ItemStack(Items.DIAMOND_SWORD)),
    STRIKE("strike", ChatFormatting.WHITE, AtributosData::getStrike, () -> new ItemStack(Items.DIAMOND_AXE)),
    PIERCE("pierce", ChatFormatting.WHITE, AtributosData::getPierce, () -> new ItemStack(Items.ARROW)),
    FIRE("fire", ChatFormatting.GOLD, AtributosData::getFire, () -> potion(Items.LINGERING_POTION, "minecraft:healing")),
    ICE("ice", ChatFormatting.DARK_AQUA, AtributosData::getIce, () -> potion(Items.LINGERING_POTION, "minecraft:long_swiftness")),
    LIGHTNING("lightning", ChatFormatting.AQUA, AtributosData::getLightning, () -> potion(Items.LINGERING_POTION, "minecraft:long_fire_resistance")),
    AQUA("aqua", ChatFormatting.BLUE, AtributosData::getAqua, () -> new ItemStack(Items.WATER_BUCKET)),
    HOLY("holy", ChatFormatting.YELLOW, AtributosData::getHoly, () -> potion(Items.LINGERING_POTION, "minecraft:invisibility")),
    ENDER("ender", ChatFormatting.DARK_PURPLE, AtributosData::getEnder, () -> new ItemStack(Items.ENDER_PEARL)),
    BLOOD("blood", ChatFormatting.DARK_RED, AtributosData::getBlood, () -> new ItemStack(Items.REDSTONE)),
    EVOCATION("evocation", ChatFormatting.GRAY, AtributosData::getEvocation, () -> new ItemStack(Items.ENCHANTED_BOOK)),
    NATURE("nature", ChatFormatting.GREEN, AtributosData::getNature, () -> new ItemStack(Items.OAK_SAPLING)),
    ELDRITCH("eldritch", ChatFormatting.DARK_GREEN, AtributosData::getEldritch, () -> new ItemStack(Items.SCULK));

    private final String id;
    private final ChatFormatting color;
    private final Function<AtributosData, Double> valorEn;
    private final Supplier<ItemStack> icono;

    Elemento(String id, ChatFormatting color, Function<AtributosData, Double> valorEn, Supplier<ItemStack> icono) {
        this.id = id;
        this.color = color;
        this.valorEn = valorEn;
        this.icono = icono;
    }

    private static ItemStack potion(Item item, String potionId) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTag().putString("Potion", potionId);
        return stack;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    public ChatFormatting color() {
        return color;
    }

    public double valorEn(AtributosData data) {
        return valorEn.apply(data);
    }

    public ItemStack icono() {
        return icono.get();
    }
}
