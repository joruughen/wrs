package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Config;
import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Elemento;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

@Mod.EventBusSubscriber(
        modid = Ews.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class MostrarCalculo {

    private static float damageOriginal;
    private static Map<Elemento, Double> desglose = new EnumMap<>(Elemento.class);
    private static double damageModificado;
    private static ResourceLocation damageType;
    private static LivingEntity origenAtacante;
    private static LivingEntity origenVictima;

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent event) {

        if (Config.debug) {
            if (event.getSource().getEntity() instanceof ServerPlayer atacante) {
                mostrar(atacante, false);
            }

            if (event.getEntity() instanceof ServerPlayer victima) {

                if (damageOriginal == 0) {
                    String noDataType = new ResourceLocation(event.getSource().getMsgId().toLowerCase(Locale.ROOT)).toString();

                    victima.sendSystemMessage(Component.translatable(Ews.MODID + ".no_data", noDataType)
                            .withStyle(ChatFormatting.WHITE));
                    return;
                }

                mostrar(victima, true);
            }
        }

        damageOriginal = 0;
        desglose = new EnumMap<>(Elemento.class);
        damageModificado = 0;
        damageType = null;
        origenAtacante = null;
        origenVictima = null;

    }

    private static void mostrar(ServerPlayer jugador, boolean esVictima) {
        mensaje(jugador, Ews.MODID + ".initial_damage", damageOriginal, ChatFormatting.GREEN);
        for (Elemento elemento : Elemento.values()) {
            mensaje(jugador, Ews.MODID + ".value." + elemento.id(), desglose.getOrDefault(elemento, 0.0), elemento.color());
        }
        mensaje(jugador, Ews.MODID + ".final_damage", damageModificado, ChatFormatting.GREEN);

        if (esVictima) {
            jugador.sendSystemMessage(Component.translatable(Ews.MODID + ".debug.damage_type",
                            damageType != null ? damageType.toString() : "?")
                    .withStyle(ChatFormatting.AQUA));

            if (origenAtacante != null) {
                jugador.sendSystemMessage(Component.translatable(Ews.MODID + ".debug.attacker", origenAtacante.getName())
                        .withStyle(ChatFormatting.AQUA));
            }
        } else if (origenVictima != null) {
            jugador.sendSystemMessage(Component.translatable(Ews.MODID + ".debug.target", origenVictima.getName())
                    .withStyle(ChatFormatting.AQUA));
        }
    }

    public static void obtenerDatos(float damageOriginal, Map<Elemento, Double> desglose, double damageModificado,
                                     ResourceLocation damageType, LivingEntity atacante, LivingEntity victima) {
        MostrarCalculo.damageOriginal = damageOriginal;
        MostrarCalculo.desglose = desglose;
        MostrarCalculo.damageModificado = damageModificado;
        MostrarCalculo.damageType = damageType;
        MostrarCalculo.origenAtacante = atacante;
        MostrarCalculo.origenVictima = victima;
    }

    private static void mensaje(ServerPlayer serverPlayer, String translatable, double damage, ChatFormatting color) {
        serverPlayer.sendSystemMessage(Component.translatable(translatable, damage)
                .withStyle(color));
    }

}
