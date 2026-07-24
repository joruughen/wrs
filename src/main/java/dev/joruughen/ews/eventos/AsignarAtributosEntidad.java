package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.LivingEntity;

@Mod.EventBusSubscriber(
        modid = Ews.MODID,
        bus = Mod.EventBusSubscriber.Bus.MOD)
public class AsignarAtributosEntidad {

    @SubscribeEvent
    public static void onAttributeModification(EntityAttributeModificationEvent event) {
        //ciclo for que recorre a todas las entidades vivas (LivingEntity) y usa la funcion agregar en todas ellas
        for (EntityType<? extends LivingEntity> entity : event.getTypes()) {
            agregar(event, entity);
        }
    }

    //funcion que agrega los atributos nuevos a las entidades vivas (que extienden de LivingEntity)
    public static void agregar(EntityAttributeModificationEvent event, EntityType<? extends LivingEntity> entidad) {
        // Slash
        event.add(entidad, Atributos.SLASH.get());
        event.add(entidad, Atributos.SLASH_RESIST.get());

        // Strike
        event.add(entidad, Atributos.STRIKE.get());
        event.add(entidad, Atributos.STRIKE_RESIST.get());

        // Pierce
        event.add(entidad, Atributos.PIERCE.get());
        event.add(entidad, Atributos.PIERCE_RESIST.get());

        // Fire
        event.add(entidad, Atributos.FIRE.get());
        event.add(entidad, Atributos.FIRE_RESIST.get());

        // Ice
        event.add(entidad, Atributos.ICE.get());
        event.add(entidad, Atributos.ICE_RESIST.get());

        // Lighting
        event.add(entidad, Atributos.LIGHTING.get());
        event.add(entidad, Atributos.LIGHTING_RESIST.get());

        // Aqua
        event.add(entidad, Atributos.AQUA.get());
        event.add(entidad, Atributos.AQUA_RESIST.get());

        // Holy
        event.add(entidad, Atributos.HOLY.get());
        event.add(entidad, Atributos.HOLY_RESIST.get());

        // Ender
        event.add(entidad, Atributos.ENDER.get());
        event.add(entidad, Atributos.ENDER_RESIST.get());

        // Blood
        event.add(entidad, Atributos.BLOOD.get());
        event.add(entidad, Atributos.BLOOD_RESIST.get());

        // Evocation
        event.add(entidad, Atributos.EVOCATION.get());
        event.add(entidad, Atributos.EVOCATION_RESIST.get());

        // Nature
        event.add(entidad, Atributos.NATURE.get());
        event.add(entidad, Atributos.NATURE_RESIST.get());

        // Eldritch
        event.add(entidad, Atributos.ELDRITCH.get());
        event.add(entidad, Atributos.ELDRITCH_RESIST.get());
    }
}
