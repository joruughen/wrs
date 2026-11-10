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

    private static final ForgeConfigSpec.BooleanValue DEBUG_DAMAGE = BUILDER
            .comment("Muestra en el chat el cálculo de cada golpe: daño inicial, daño de cada elemento (solo los distintos de 0) y daño final")
            .comment("default: false")
            .define("debug_damage", false);

    private static final ForgeConfigSpec.BooleanValue DEBUG_INFO = BUILDER
            .comment("Muestra en el chat los datos de cada golpe: damage type, atacante/objetivo y qué perfil o regla del datapack se usó")
            .comment("default: false")
            .define("debug_info", false);

    private static final ForgeConfigSpec.BooleanValue MOSTRAR_REGLAS = BUILDER
            .comment("Muestra las reglas por damage type (\"damage_types\" del datapack) en Jade y en el tooltip de los ítems")
            .comment("default: true")
            .define("mostrar_reglas_damage_type", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static boolean detalles;
    public static boolean debugDamage;
    public static boolean debugInfo;
    public static boolean mostrarReglas = true;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        detalles = DETALLES.get();
        debugDamage = DEBUG_DAMAGE.get();
        debugInfo = DEBUG_INFO.get();
        mostrarReglas = MOSTRAR_REGLAS.get();
    }
}
