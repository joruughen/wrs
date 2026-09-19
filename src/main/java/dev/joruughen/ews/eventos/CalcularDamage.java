package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import dev.joruughen.ews.atributos.Elemento;
import dev.joruughen.ews.data.DataLoaderEvent;
import dev.joruughen.ews.data.DamageTypeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumMap;
import java.util.Map;

@Mod.EventBusSubscriber(
        modid = Ews.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class CalcularDamage {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity victima = event.getEntity();
        if (victima == null) return;

        // La fuente de daño no siempre es una entidad viviente (ej. TNT, cactus): evitamos el ClassCastException.
        LivingEntity atacante = event.getSource().getEntity() instanceof LivingEntity fuente ? fuente : null;

        float damageOriginal = event.getAmount();
        double atributosMinimos = 100;

        ResourceKey<DamageType> damageTypeResourceKey = event.getSource().typeHolder().unwrapKey().orElse(null);
        ResourceLocation damagetype = damageTypeResourceKey != null ? damageTypeResourceKey.location() : null;

        DamageTypeData dataDamage = damagetype != null ? DataLoaderEvent.DAMAGE_TYPES.getData().get(damagetype) : null;
        boolean especial = dataDamage != null && dataDamage.special();

        Map<Elemento, Double> desglose = new EnumMap<>(Elemento.class);
        double damageModificado;

        if (atacante != null && !especial) {

            if (event.getSource().isIndirect() && event.getSource().getDirectEntity() instanceof LivingEntity atacanteDirecto) {
                atacante = atacanteDirecto;
            }

            for (Elemento elemento : Elemento.values()) {
                double ataqueValor = atacante.getAttributeValue(Atributos.damage(elemento));
                double defensaValor = victima.getAttributeValue(Atributos.resist(elemento));
                desglose.put(elemento, calcularDamage(ataqueValor, defensaValor, damageOriginal));
            }

            damageModificado = desglose.values().stream().mapToDouble(Double::doubleValue).sum();

            atributosMinimos = atributosMinimos - Atributos.atributosTotales(atacante);

            if (atributosMinimos > 0) {
                damageModificado = damageModificado + (damageOriginal * (atributosMinimos / 100));
            }

            event.setAmount((float) damageModificado);
        } else {

            if (dataDamage == null) return;

            for (Elemento elemento : Elemento.values()) {
                double ataqueValor = elemento.valorEn(dataDamage.damage());
                double defensaValor = victima.getAttributeValue(Atributos.resist(elemento));
                desglose.put(elemento, calcularDamage(ataqueValor, defensaValor, damageOriginal));
            }

            damageModificado = desglose.values().stream().mapToDouble(Double::doubleValue).sum();

            atributosMinimos = atributosMinimos - Atributos.atributosTotales(dataDamage);

            if (atributosMinimos > 0) {
                damageModificado = damageModificado + (damageOriginal * (atributosMinimos / 100));
            }

            event.setAmount((float) damageModificado);
        }

        MostrarCalculo.obtenerDatos(damageOriginal, desglose, damageModificado, damagetype, atacante, victima);
    }

    public static double calcularDamage(double ataqueValor, double defensaValor, float damageOriginal) {

        double damageFinal = 0.0;

        if (ataqueValor > 0) {
            damageFinal = damageOriginal * (ataqueValor / 100);
            damageFinal = calcularResistencia(damageFinal, defensaValor);
        }

        return damageFinal;
    }

    public static double calcularResistencia(double damage, double resistencia) {
        if (resistencia > 0 && resistencia <= 100) {
            damage *= 1 - (resistencia / 100);
        } else if (resistencia > 0) {
            damage = 0;
        } else damage *= 1 + (Math.abs(resistencia) / 100);

        return damage;
    }

}
