package dev.joruughen.ews.atributos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.DamageTypeData;
import dev.joruughen.ews.data.Modificador;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Atributos {

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Ews.MODID);

    private static final Map<Elemento, RegistryObject<Attribute>> DAMAGE = new EnumMap<>(Elemento.class);
    private static final Map<Elemento, RegistryObject<Attribute>> RESIST = new EnumMap<>(Elemento.class);
    private static final Map<ResourceLocation, Elemento> POR_ID = new HashMap<>();

    static {
        for (Elemento elemento : Elemento.values()) {
            DAMAGE.put(elemento, ATTRIBUTES.register(elemento.id(),
                    () -> (new RangedAttribute(Ews.MODID + "." + elemento.id(), 0.0, 0.0, Double.MAX_VALUE)).setSyncable(true)));
            RESIST.put(elemento, ATTRIBUTES.register(elemento.id() + "_resist",
                    () -> (new RangedAttribute(Ews.MODID + "." + elemento.id() + "_resist", 0.0, -Double.MAX_VALUE, Double.MAX_VALUE)).setSyncable(true)));
            POR_ID.put(new ResourceLocation(Ews.MODID, elemento.id()), elemento);
            POR_ID.put(new ResourceLocation(Ews.MODID, elemento.id() + "_resist"), elemento);
        }
    }

    public Atributos() {
    }

    public static Attribute damage(Elemento elemento) {
        return DAMAGE.get(elemento).get();
    }

    public static Attribute resist(Elemento elemento) {
        return RESIST.get(elemento).get();
    }

    /** Devuelve el {@link Elemento} propio de EWS al que pertenece este id de atributo, o null si es de otro mod. */
    public static Elemento elementoDe(ResourceLocation idAtributo) {
        return POR_ID.get(idAtributo);
    }

    /**
     * Resuelve los modificadores de tipo "addition" de {@code data} a un valor base por atributo,
     * para asignar directamente vía {@code AttributeInstance.setBaseValue(...)} (usado al spawnear
     * entidades). Los modificadores porcentuales ("multiply_base"/"multiply_total") no aplican a un
     * valor base y se ignoran aquí — están pensados para ítems, ver {@code AgregarModificadorItem}.
     */
    public static Map<Attribute, Double> valoresBase(AtributosDataType data) {
        Map<Attribute, Double> valores = new HashMap<>();
        for (Modificador modificador : data.modifiers()) {
            if (modificador.operacion() != AttributeModifier.Operation.ADDITION) continue;

            Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo());
            if (attribute == null) {
                Ews.LOGGER.warn("Atributo desconocido en datapack: {}", modificador.atributo());
                continue;
            }

            valores.merge(attribute, modificador.valor(), Double::sum);
        }
        return valores;
    }

    public static double atributosTotales(LivingEntity entity) {
        double atributosTotales = 0.0;
        for (Elemento elemento : Elemento.values()) {
            atributosTotales += entity.getAttributeValue(damage(elemento));
        }
        return atributosTotales;
    }

    public static double atributosTotales(DamageTypeData data) {
        double atributosTotales = 0.0;
        for (Elemento elemento : Elemento.values()) {
            atributosTotales += elemento.valorEn(data.damage());
        }
        return atributosTotales;
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
