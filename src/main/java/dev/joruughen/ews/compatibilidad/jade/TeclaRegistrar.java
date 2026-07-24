package dev.joruughen.ews.compatibilidad.jade;

import dev.joruughen.ews.Ews;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Registra la tecla en  el lado del cliente
@Mod.EventBusSubscriber(modid = Ews.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TeclaRegistrar {
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(Tecla.KEY_VER_MAS);
    }
}
