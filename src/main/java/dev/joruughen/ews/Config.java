package dev.joruughen.ews;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = Ews.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue DETALLES = BUILDER
            .comment("Agrega los nombres de atributos al tooltip en jade")
            .comment("default: false")
            .define("detalles", false);

    private static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("Muestra el daño final realizado")
            .comment("default: false")
            .define("debug", false);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean detalles;
    public static boolean debug;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        detalles = DETALLES.get();
        debug = DEBUG.get();
    }
}
