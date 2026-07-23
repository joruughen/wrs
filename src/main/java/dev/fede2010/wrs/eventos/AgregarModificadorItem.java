package dev.fede2010.wrs.eventos;

import dev.fede2010.wrs.Wrs;
import dev.fede2010.wrs.atributos.Atributos;
import dev.fede2010.wrs.data.AtributosDataType;
import dev.fede2010.wrs.data.DataLoaderEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = Wrs.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class AgregarModificadorItem {

    private static final Map<Attribute, UUID> mainHand = new HashMap<>();
    private static final Map<Attribute, UUID> offHand = new HashMap<>();
    private static final Map<Attribute, UUID> head = new HashMap<>();
    private static final Map<Attribute, UUID> chest = new HashMap<>();
    private static final Map<Attribute, UUID> legs = new HashMap<>();
    private static final Map<Attribute, UUID> feet = new HashMap<>();

    // Evento para agregar modificadores de atributo a los items
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

        if (dataGrupo == null){
            if (dataItem == null){
                return;
            }else resultado = dataItem;
        }else resultado = dataGrupo;

        if (dataGrupo != null && dataItem != null) {
            resultado = AtributosDataType.reemplazarValores(dataGrupo, dataItem);
        }

        mainHand.clear();
        offHand.clear();
        head.clear();
        chest.clear();
        legs.clear();
        feet.clear();

        mapeo(mainHand, event);
        mapeo(offHand, event);
        mapeo(head, event);
        mapeo(chest, event);
        mapeo(legs, event);
        mapeo(feet, event);

        // Verifica si el ítem es una armadura
        if (itemStack.getItem() instanceof ArmorItem) {
            // Obtiene el tipo de ranura de equipo de la armadura
            EquipmentSlot armorSlot = ((ArmorItem) itemStack.getItem()).getEquipmentSlot();
            // Verifica si el slot actual coincide con el tipo de ranura de equipo de la armadura
            if (event.getSlotType() == armorSlot) {

                if (EquipmentSlot.HEAD == armorSlot) {
                    crearModificador(event, head, resultado);
                }

                if (EquipmentSlot.CHEST == armorSlot) {
                    crearModificador(event, chest, resultado);
                }

                if (EquipmentSlot.LEGS == armorSlot) {
                    crearModificador(event, legs, resultado);
                }

                if (EquipmentSlot.FEET == armorSlot) {
                    crearModificador(event, feet, resultado);
                }
            }
        } else if (itemStack.getItem() instanceof ShieldItem && event.getSlotType() == EquipmentSlot.OFFHAND) {
            crearModificador(event, offHand, resultado);
        }
        else if (!(itemStack.getItem() instanceof ShieldItem) && event.getSlotType() == EquipmentSlot.MAINHAND) {
            crearModificador(event, mainHand, resultado);
        }
    }

    public static void crearModificador(ItemAttributeModifierEvent event, Map<Attribute, UUID> map, AtributosDataType data){
        // Daño
        event.addModifier(Atributos.SLASH.get(), new AttributeModifier(map.get(Atributos.SLASH.get()), Atributos.SLASH.get() + ":" + event.getSlotType(), data.damage().getSlash(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.STRIKE.get(), new AttributeModifier(map.get(Atributos.STRIKE.get()), Atributos.STRIKE.get() + ":" + event.getSlotType(), data.damage().getStrike(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.PIERCE.get(), new AttributeModifier(map.get(Atributos.PIERCE.get()), Atributos.PIERCE.get() + ":" + event.getSlotType(), data.damage().getPierce(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.FIRE.get(), new AttributeModifier(map.get(Atributos.FIRE.get()), Atributos.FIRE.get() + ":" + event.getSlotType(), data.damage().getFire(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.ICE.get(), new AttributeModifier(map.get(Atributos.ICE.get()), Atributos.ICE.get() + ":" + event.getSlotType(), data.damage().getIce(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.LIGHTING.get(), new AttributeModifier(map.get(Atributos.LIGHTING.get()), Atributos.LIGHTING.get() + ":" + event.getSlotType(), data.damage().getLighting(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.AQUA.get(), new AttributeModifier(map.get(Atributos.AQUA.get()), Atributos.AQUA.get() + ":" + event.getSlotType(), data.damage().getAqua(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.HOLY.get(), new AttributeModifier(map.get(Atributos.HOLY.get()), Atributos.HOLY.get() + ":" + event.getSlotType(), data.damage().getHoly(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.ENDER.get(), new AttributeModifier(map.get(Atributos.ENDER.get()), Atributos.ENDER.get() + ":" + event.getSlotType(), data.damage().getEnder(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.BLOOD.get(), new AttributeModifier(map.get(Atributos.BLOOD.get()), Atributos.BLOOD.get() + ":" + event.getSlotType(), data.damage().getBlood(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.EVOCATION.get(), new AttributeModifier(map.get(Atributos.EVOCATION.get()), Atributos.EVOCATION.get() + ":" + event.getSlotType(), data.damage().getEvocation(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.NATURE.get(), new AttributeModifier(map.get(Atributos.NATURE.get()), Atributos.NATURE.get() + ":" + event.getSlotType(), data.damage().getNature(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.ELDRITCH.get(), new AttributeModifier(map.get(Atributos.ELDRITCH.get()), Atributos.ELDRITCH.get() + ":" + event.getSlotType(), data.damage().getEldritch(), AttributeModifier.Operation.ADDITION));

        // Resistencias
        event.addModifier(Atributos.SLASH_RESIST.get(), new AttributeModifier(map.get(Atributos.SLASH_RESIST.get()), Atributos.SLASH_RESIST.get() + ":" + event.getSlotType(), data.resistance().getSlash(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.STRIKE_RESIST.get(), new AttributeModifier(map.get(Atributos.STRIKE_RESIST.get()), Atributos.STRIKE_RESIST.get() + ":" + event.getSlotType(), data.resistance().getStrike(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.PIERCE_RESIST.get(), new AttributeModifier(map.get(Atributos.PIERCE_RESIST.get()), Atributos.PIERCE_RESIST.get() + ":" + event.getSlotType(), data.resistance().getPierce(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.FIRE_RESIST.get(), new AttributeModifier(map.get(Atributos.FIRE_RESIST.get()), Atributos.FIRE_RESIST.get() + ":" + event.getSlotType(), data.resistance().getFire(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.ICE_RESIST.get(), new AttributeModifier(map.get(Atributos.ICE_RESIST.get()), Atributos.ICE_RESIST.get() + ":" + event.getSlotType(), data.resistance().getIce(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.LIGHTING_RESIST.get(), new AttributeModifier(map.get(Atributos.LIGHTING_RESIST.get()), Atributos.LIGHTING_RESIST.get() + ":" + event.getSlotType(), data.resistance().getLighting(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.AQUA_RESIST.get(), new AttributeModifier(map.get(Atributos.AQUA_RESIST.get()), Atributos.AQUA_RESIST.get() + ":" + event.getSlotType(), data.resistance().getAqua(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.HOLY_RESIST.get(), new AttributeModifier(map.get(Atributos.HOLY_RESIST.get()), Atributos.HOLY_RESIST.get() + ":" + event.getSlotType(), data.resistance().getHoly(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.ENDER_RESIST.get(), new AttributeModifier(map.get(Atributos.ENDER_RESIST.get()), Atributos.ENDER_RESIST.get() + ":" + event.getSlotType(), data.resistance().getEnder(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.BLOOD_RESIST.get(), new AttributeModifier(map.get(Atributos.BLOOD_RESIST.get()), Atributos.BLOOD_RESIST.get() + ":" + event.getSlotType(), data.resistance().getBlood(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.EVOCATION_RESIST.get(), new AttributeModifier(map.get(Atributos.EVOCATION_RESIST.get()), Atributos.EVOCATION_RESIST.get() + ":" + event.getSlotType(), data.resistance().getEvocation(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.NATURE_RESIST.get(), new AttributeModifier(map.get(Atributos.NATURE_RESIST.get()), Atributos.NATURE_RESIST.get() + ":" + event.getSlotType(), data.resistance().getNature(), AttributeModifier.Operation.ADDITION));
        event.addModifier(Atributos.ELDRITCH_RESIST.get(), new AttributeModifier(map.get(Atributos.ELDRITCH_RESIST.get()), Atributos.ELDRITCH_RESIST.get() + ":" + event.getSlotType(), data.resistance().getEldritch(), AttributeModifier.Operation.ADDITION));
    }

    public static void mapeo(Map<Attribute, UUID> map, ItemAttributeModifierEvent event) {
        if (map.isEmpty()) {
            String evento = event.getItemStack().getItem().getDescriptionId() + ":";

            // Daño
            map.put(Atributos.SLASH.get(), UUID.nameUUIDFromBytes((evento + "SLASH").getBytes()));
            map.put(Atributos.STRIKE.get(), UUID.nameUUIDFromBytes((evento + "STRIKE").getBytes()));
            map.put(Atributos.PIERCE.get(), UUID.nameUUIDFromBytes((evento + "PIERCE").getBytes()));
            map.put(Atributos.FIRE.get(), UUID.nameUUIDFromBytes((evento + "FIRE").getBytes()));
            map.put(Atributos.ICE.get(), UUID.nameUUIDFromBytes((evento + "ICE").getBytes()));
            map.put(Atributos.LIGHTING.get(), UUID.nameUUIDFromBytes((evento + "LIGHTING").getBytes()));
            map.put(Atributos.AQUA.get(), UUID.nameUUIDFromBytes((evento + "AQUA").getBytes()));
            map.put(Atributos.HOLY.get(), UUID.nameUUIDFromBytes((evento + "HOLY").getBytes()));
            map.put(Atributos.ENDER.get(), UUID.nameUUIDFromBytes((evento + "ENDER").getBytes()));
            map.put(Atributos.BLOOD.get(), UUID.nameUUIDFromBytes((evento + "BLOOD").getBytes()));
            map.put(Atributos.EVOCATION.get(), UUID.nameUUIDFromBytes((evento + "EVOCATION").getBytes()));
            map.put(Atributos.NATURE.get(), UUID.nameUUIDFromBytes((evento + "NATURE").getBytes()));
            map.put(Atributos.ELDRITCH.get(), UUID.nameUUIDFromBytes((evento + "ELDRITCH").getBytes()));

            // Resistencias
            map.put(Atributos.SLASH_RESIST.get(), UUID.nameUUIDFromBytes((evento + "SLASH_RESIST").getBytes()));
            map.put(Atributos.STRIKE_RESIST.get(), UUID.nameUUIDFromBytes((evento + "STRIKE_RESIST").getBytes()));
            map.put(Atributos.PIERCE_RESIST.get(), UUID.nameUUIDFromBytes((evento + "PIERCE_RESIST").getBytes()));
            map.put(Atributos.FIRE_RESIST.get(), UUID.nameUUIDFromBytes((evento + "FIRE_RESIST").getBytes()));
            map.put(Atributos.ICE_RESIST.get(), UUID.nameUUIDFromBytes((evento + "ICE_RESIST").getBytes()));
            map.put(Atributos.LIGHTING_RESIST.get(), UUID.nameUUIDFromBytes((evento + "LIGHTING_RESIST").getBytes()));
            map.put(Atributos.AQUA_RESIST.get(), UUID.nameUUIDFromBytes((evento + "AQUA_RESIST").getBytes()));
            map.put(Atributos.HOLY_RESIST.get(), UUID.nameUUIDFromBytes((evento + "HOLY_RESIST").getBytes()));
            map.put(Atributos.ENDER_RESIST.get(), UUID.nameUUIDFromBytes((evento + "ENDER_RESIST").getBytes()));
            map.put(Atributos.BLOOD_RESIST.get(), UUID.nameUUIDFromBytes((evento + "BLOOD_RESIST").getBytes()));
            map.put(Atributos.EVOCATION_RESIST.get(), UUID.nameUUIDFromBytes((evento + "EVOCATION_RESIST").getBytes()));
            map.put(Atributos.NATURE_RESIST.get(), UUID.nameUUIDFromBytes((evento + "NATURE_RESIST").getBytes()));
            map.put(Atributos.ELDRITCH_RESIST.get(), UUID.nameUUIDFromBytes((evento + "ELDRITCH_RESIST").getBytes()));
        }
    }
}
