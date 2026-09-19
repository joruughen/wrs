package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import dev.joruughen.ews.atributos.Elemento;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
        for (Elemento elemento : Elemento.values()) {
            event.add(entidad, Atributos.damage(elemento));
            event.add(entidad, Atributos.resist(elemento));
        }
    }
}
