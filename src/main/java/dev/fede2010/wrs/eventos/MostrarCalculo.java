package dev.fede2010.wrs.eventos;

import dev.fede2010.wrs.Config;
import dev.fede2010.wrs.Wrs;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Locale;

@Mod.EventBusSubscriber(
        modid = Wrs.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class MostrarCalculo {

    public static float damageOriginal;
    public static double slashDamage;
    public static double strikeDamage;
    public static double pierceDamage;
    public static double fireDamage;
    public static double iceDamage;
    public static double lightingDamage;
    public static double aquaDamage;
    public static double holyDamage;
    public static double enderDamage;
    public static double bloodDamage;
    public static double evocationDamage;
    public static double natureDamage;
    public static double eldritchDamage;
    public static double damageModificado;

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent event) {

        if (Config.debug){
            if (event.getSource().getEntity() instanceof ServerPlayer atacante) {

                mensaje(atacante, Wrs.MODID + ".initial_damage", damageOriginal, ChatFormatting.GREEN);
                mensaje(atacante, Wrs.MODID + ".value.slash", slashDamage, ChatFormatting.GRAY);
                mensaje(atacante, Wrs.MODID + ".value.strike", strikeDamage, ChatFormatting.GRAY);
                mensaje(atacante, Wrs.MODID + ".value.pierce", pierceDamage, ChatFormatting.GRAY);
                mensaje(atacante, Wrs.MODID + ".value.fire", fireDamage, ChatFormatting.RED);
                mensaje(atacante, Wrs.MODID + ".value.ice", iceDamage, ChatFormatting.AQUA);
                mensaje(atacante, Wrs.MODID + ".value.lighting", lightingDamage, ChatFormatting.YELLOW);
                mensaje(atacante, Wrs.MODID + ".value.aqua", aquaDamage, ChatFormatting.BLUE);
                mensaje(atacante, Wrs.MODID + ".value.holy", holyDamage, ChatFormatting.WHITE);
                mensaje(atacante, Wrs.MODID + ".value.ender", enderDamage, ChatFormatting.DARK_PURPLE);
                mensaje(atacante, Wrs.MODID + ".value.blood", bloodDamage, ChatFormatting.DARK_RED);
                mensaje(atacante, Wrs.MODID + ".value.evocation", evocationDamage, ChatFormatting.LIGHT_PURPLE);
                mensaje(atacante, Wrs.MODID + ".value.nature", natureDamage, ChatFormatting.GREEN);
                mensaje(atacante, Wrs.MODID + ".value.eldritch", eldritchDamage, ChatFormatting.DARK_GREEN);
                mensaje(atacante, Wrs.MODID + ".final_damage", damageModificado, ChatFormatting.GREEN);

            }

            if (event.getEntity() instanceof ServerPlayer victima) {

                if (damageOriginal == 0){
                    String damageType = new ResourceLocation(event.getSource().getMsgId().toLowerCase(Locale.ROOT)).toString();

                    victima.sendSystemMessage(Component.translatable(Wrs.MODID + ".no_data", damageType)
                            .withStyle(ChatFormatting.WHITE));
                    return;
                }

                mensaje(victima, Wrs.MODID + ".initial_damage", damageOriginal, ChatFormatting.GREEN);
                mensaje(victima, Wrs.MODID + ".value.slash", slashDamage, ChatFormatting.GRAY);
                mensaje(victima, Wrs.MODID + ".value.strike", strikeDamage, ChatFormatting.GRAY);
                mensaje(victima, Wrs.MODID + ".value.pierce", pierceDamage, ChatFormatting.GRAY);
                mensaje(victima, Wrs.MODID + ".value.fire", fireDamage, ChatFormatting.RED);
                mensaje(victima, Wrs.MODID + ".value.ice", iceDamage, ChatFormatting.AQUA);
                mensaje(victima, Wrs.MODID + ".value.lighting", lightingDamage, ChatFormatting.YELLOW);
                mensaje(victima, Wrs.MODID + ".value.aqua", aquaDamage, ChatFormatting.BLUE);
                mensaje(victima, Wrs.MODID + ".value.holy", holyDamage, ChatFormatting.WHITE);
                mensaje(victima, Wrs.MODID + ".value.ender", enderDamage, ChatFormatting.DARK_PURPLE);
                mensaje(victima, Wrs.MODID + ".value.blood", bloodDamage, ChatFormatting.DARK_RED);
                mensaje(victima, Wrs.MODID + ".value.evocation", evocationDamage, ChatFormatting.LIGHT_PURPLE);
                mensaje(victima, Wrs.MODID + ".value.nature", natureDamage, ChatFormatting.GREEN);
                mensaje(victima, Wrs.MODID + ".value.eldritch", eldritchDamage, ChatFormatting.DARK_GREEN);
                mensaje(victima, Wrs.MODID + ".final_damage", damageModificado, ChatFormatting.GREEN);
            }

        }

        damageOriginal = 0;
        slashDamage = 0;
        strikeDamage = 0;
        pierceDamage = 0;
        fireDamage = 0;
        iceDamage = 0;
        lightingDamage = 0;
        aquaDamage = 0;
        holyDamage = 0;
        enderDamage = 0;
        bloodDamage = 0;
        evocationDamage = 0;
        natureDamage = 0;
        eldritchDamage = 0;
        damageModificado = 0;

    }

    public static void obtenerDatos(float damageOriginal, double slashDamage, double strikeDamage, double pierceDamage, double fireDamage, double iceDamage, double lightingDamage, double aquaDamage, double holyDamage, double enderDamage, double bloodDamage, double evocationDamage, double natureDamage, double eldritchDamage, double damageModificado){
        MostrarCalculo.damageOriginal = damageOriginal;
        MostrarCalculo.slashDamage = slashDamage;
        MostrarCalculo.strikeDamage = strikeDamage;
        MostrarCalculo.pierceDamage = pierceDamage;
        MostrarCalculo.fireDamage = fireDamage;
        MostrarCalculo.iceDamage = iceDamage;
        MostrarCalculo.lightingDamage = lightingDamage;
        MostrarCalculo.aquaDamage = aquaDamage;
        MostrarCalculo.holyDamage = holyDamage;
        MostrarCalculo.enderDamage = enderDamage;
        MostrarCalculo.bloodDamage = bloodDamage;
        MostrarCalculo.evocationDamage = evocationDamage;
        MostrarCalculo.natureDamage = natureDamage;
        MostrarCalculo.eldritchDamage = eldritchDamage;
        MostrarCalculo.damageModificado = damageModificado;
    }

    private static void mensaje(ServerPlayer serverPlayer, String translatable, double damage, ChatFormatting color){
        serverPlayer.sendSystemMessage(Component.translatable(translatable, damage)
                .withStyle(color));
    }

}
