package dev.joruughen.ews.atributos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.DamageTypeData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class Atributos {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Ews.MODID);

    public static final RegistryObject<Attribute> SLASH = ATTRIBUTES.register("slash", () -> (new RangedAttribute(Ews.MODID + ".slash", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> STRIKE = ATTRIBUTES.register("strike", () -> (new RangedAttribute(Ews.MODID + ".strike", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> PIERCE = ATTRIBUTES.register("pierce", () -> (new RangedAttribute(Ews.MODID + ".pierce", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> FIRE = ATTRIBUTES.register("fire", () -> (new RangedAttribute(Ews.MODID + ".fire", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> ICE = ATTRIBUTES.register("ice", () -> (new RangedAttribute(Ews.MODID + ".ice", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTING = ATTRIBUTES.register("lighting", () -> (new RangedAttribute(Ews.MODID + ".lighting", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> AQUA = ATTRIBUTES.register("aqua", () -> (new RangedAttribute(Ews.MODID + ".aqua", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> HOLY = ATTRIBUTES.register("holy", () -> (new RangedAttribute(Ews.MODID + ".holy", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> ENDER = ATTRIBUTES.register("ender", () -> (new RangedAttribute(Ews.MODID + ".ender", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> BLOOD = ATTRIBUTES.register("blood", () -> (new RangedAttribute(Ews.MODID + ".blood", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> EVOCATION = ATTRIBUTES.register("evocation", () -> (new RangedAttribute(Ews.MODID + ".evocation", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> NATURE = ATTRIBUTES.register("nature", () -> (new RangedAttribute(Ews.MODID + ".nature", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> ELDRITCH = ATTRIBUTES.register("eldritch", () -> (new RangedAttribute(Ews.MODID + ".eldritch", 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true));

    public static final RegistryObject<Attribute> SLASH_RESIST = ATTRIBUTES.register("slash_resist", () -> (new RangedAttribute(Ews.MODID + ".slash_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> STRIKE_RESIST = ATTRIBUTES.register("strike_resist", () -> (new RangedAttribute(Ews.MODID + ".strike_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> PIERCE_RESIST = ATTRIBUTES.register("pierce_resist", () -> (new RangedAttribute(Ews.MODID + ".pierce_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> FIRE_RESIST = ATTRIBUTES.register("fire_resist", () -> (new RangedAttribute(Ews.MODID + ".fire_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> ICE_RESIST = ATTRIBUTES.register("ice_resist", () -> (new RangedAttribute(Ews.MODID + ".ice_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> LIGHTING_RESIST = ATTRIBUTES.register("lighting_resist", () -> (new RangedAttribute(Ews.MODID + ".lighting_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> AQUA_RESIST = ATTRIBUTES.register("aqua_resist", () -> (new RangedAttribute(Ews.MODID + ".aqua_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> HOLY_RESIST = ATTRIBUTES.register("holy_resist", () -> (new RangedAttribute(Ews.MODID + ".holy_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> ENDER_RESIST = ATTRIBUTES.register("ender_resist", () -> (new RangedAttribute(Ews.MODID + ".ender_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> BLOOD_RESIST = ATTRIBUTES.register("blood_resist", () -> (new RangedAttribute(Ews.MODID + ".blood_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> EVOCATION_RESIST = ATTRIBUTES.register("evocation_resist", () -> (new RangedAttribute(Ews.MODID + ".evocation_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> NATURE_RESIST = ATTRIBUTES.register("nature_resist", () -> (new RangedAttribute(Ews.MODID + ".nature_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));
    public static final RegistryObject<Attribute> ELDRITCH_RESIST = ATTRIBUTES.register("eldritch_resist", () -> (new RangedAttribute(Ews.MODID + ".eldritch_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true));

    public Atributos() {
    }

        public static void atributosMap(Map<Attribute, Double> atributosMap, AtributosDataType data){
        atributosMap.put(Atributos.SLASH.get(), data.damage().getSlash());
        atributosMap.put(Atributos.STRIKE.get(), data.damage().getStrike());
        atributosMap.put(Atributos.PIERCE.get(), data.damage().getPierce());
        atributosMap.put(Atributos.FIRE.get(), data.damage().getFire());
        atributosMap.put(Atributos.ICE.get(), data.damage().getIce());
        atributosMap.put(Atributos.LIGHTING.get(), data.damage().getLighting());
        atributosMap.put(Atributos.AQUA.get(), data.damage().getAqua());
        atributosMap.put(Atributos.HOLY.get(), data.damage().getHoly());
        atributosMap.put(Atributos.ENDER.get(), data.damage().getEnder());
        atributosMap.put(Atributos.BLOOD.get(), data.damage().getBlood());
        atributosMap.put(Atributos.EVOCATION.get(), data.damage().getEvocation());
        atributosMap.put(Atributos.NATURE.get(), data.damage().getNature());
        atributosMap.put(Atributos.ELDRITCH.get(), data.damage().getEldritch());

        atributosMap.put(Atributos.SLASH_RESIST.get(), data.resistance().getSlash());
        atributosMap.put(Atributos.STRIKE_RESIST.get(), data.resistance().getStrike());
        atributosMap.put(Atributos.PIERCE_RESIST.get(), data.resistance().getPierce());
        atributosMap.put(Atributos.FIRE_RESIST.get(), data.resistance().getFire());
        atributosMap.put(Atributos.ICE_RESIST.get(), data.resistance().getIce());
        atributosMap.put(Atributos.LIGHTING_RESIST.get(), data.resistance().getLighting());
        atributosMap.put(Atributos.AQUA_RESIST.get(), data.resistance().getAqua());
        atributosMap.put(Atributos.HOLY_RESIST.get(), data.resistance().getHoly());
        atributosMap.put(Atributos.ENDER_RESIST.get(), data.resistance().getEnder());
        atributosMap.put(Atributos.BLOOD_RESIST.get(), data.resistance().getBlood());
        atributosMap.put(Atributos.EVOCATION_RESIST.get(), data.resistance().getEvocation());
        atributosMap.put(Atributos.NATURE_RESIST.get(), data.resistance().getNature());
        atributosMap.put(Atributos.ELDRITCH_RESIST.get(), data.resistance().getEldritch());
    }

    public static double atributosTotales(LivingEntity entity) {
        Attribute[] damageAttributes = new Attribute[]{
                Atributos.SLASH.get(), Atributos.STRIKE.get(), Atributos.PIERCE.get(),
                Atributos.FIRE.get(), Atributos.ICE.get(), Atributos.LIGHTING.get(),
                Atributos.AQUA.get(), Atributos.HOLY.get(), Atributos.ENDER.get(),
                Atributos.BLOOD.get(), Atributos.EVOCATION.get(), Atributos.NATURE.get(),
                Atributos.ELDRITCH.get()
        };

        double atributosTotales = 0.0;
        for (Attribute attr : damageAttributes) {
            atributosTotales += entity.getAttributeValue(attr);
        }

        return atributosTotales;
    }

    public static double atributosTotales(DamageTypeData data) {
        double[] dataType = {
                data.damage().getSlash(), data.damage().getStrike(), data.damage().getPierce(),
                data.damage().getFire(), data.damage().getIce(), data.damage().getLighting(),
                data.damage().getAqua(), data.damage().getHoly(), data.damage().getEnder(),
                data.damage().getBlood(), data.damage().getEvocation(), data.damage().getNature(),
                data.damage().getEldritch()
        };

        double atributosTotales = 0.0;
        for (double attr : dataType) {
            atributosTotales += attr;
        }

        return atributosTotales;
    }


    public static void register(IEventBus eventBus){
        ATTRIBUTES.register(eventBus);
    }
}
