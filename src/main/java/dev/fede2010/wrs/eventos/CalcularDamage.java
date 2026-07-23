package dev.fede2010.wrs.eventos;

import dev.fede2010.wrs.Wrs;
import dev.fede2010.wrs.atributos.Atributos;
import dev.fede2010.wrs.data.DataLoaderEvent;
import dev.fede2010.wrs.data.DamageTypeData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = Wrs.MODID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class CalcularDamage {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        // Obtenemos la entidad que está recibiendo daño
        LivingEntity victima = event.getEntity();
        LivingEntity atacante = (LivingEntity) event.getSource().getEntity();

        float damageOriginal = event.getAmount();

        double slashDamage;
        double strikeDamage;
        double pierceDamage;
        double fireDamage;
        double iceDamage;
        double lightingDamage;
        double aquaDamage;
        double holyDamage;
        double enderDamage;
        double bloodDamage;
        double evocationDamage;
        double natureDamage;
        double eldritchDamage;
        double atributosMinimos = 100;

        boolean especial = false;

        double damageModificado;

        if (victima == null) return;

        ResourceKey<DamageType> damageTypeResourceKey = event.getSource().typeHolder().unwrapKey().orElse(null);
        ResourceLocation damagetype = new ResourceLocation("");
        if (damageTypeResourceKey != null){
            damagetype = damageTypeResourceKey.location();
        }

        DamageTypeData dataDamage = DataLoaderEvent.DAMAGE_TYPES.getData().get(damagetype);
        if (dataDamage != null){
            especial = dataDamage.special();
        }

        if (atacante != null && !especial){

            if (event.getSource().isIndirect()) {
                if (event.getSource().getDirectEntity() instanceof LivingEntity atacanteDirecto) {
                    atacante = atacanteDirecto;
                }
            }

            slashDamage = calcularDamage(atacante.getAttributeValue(Atributos.SLASH.get()), victima.getAttributeValue(Atributos.SLASH_RESIST.get()), damageOriginal);
            strikeDamage = calcularDamage(atacante.getAttributeValue(Atributos.STRIKE.get()), victima.getAttributeValue(Atributos.STRIKE_RESIST.get()), damageOriginal);
            pierceDamage = calcularDamage(atacante.getAttributeValue(Atributos.PIERCE.get()), victima.getAttributeValue(Atributos.PIERCE_RESIST.get()), damageOriginal);
            fireDamage = calcularDamage(atacante.getAttributeValue(Atributos.FIRE.get()), victima.getAttributeValue(Atributos.FIRE_RESIST.get()), damageOriginal);
            iceDamage = calcularDamage(atacante.getAttributeValue(Atributos.ICE.get()), victima.getAttributeValue(Atributos.ICE_RESIST.get()), damageOriginal);
            lightingDamage = calcularDamage(atacante.getAttributeValue(Atributos.LIGHTING.get()), victima.getAttributeValue(Atributos.LIGHTING_RESIST.get()), damageOriginal);
            aquaDamage = calcularDamage(atacante.getAttributeValue(Atributos.AQUA.get()), victima.getAttributeValue(Atributos.AQUA_RESIST.get()), damageOriginal);
            holyDamage = calcularDamage(atacante.getAttributeValue(Atributos.HOLY.get()), victima.getAttributeValue(Atributos.HOLY_RESIST.get()), damageOriginal);
            enderDamage = calcularDamage(atacante.getAttributeValue(Atributos.ENDER.get()), victima.getAttributeValue(Atributos.ENDER_RESIST.get()), damageOriginal);
            bloodDamage = calcularDamage(atacante.getAttributeValue(Atributos.BLOOD.get()), victima.getAttributeValue(Atributos.BLOOD_RESIST.get()), damageOriginal);
            evocationDamage = calcularDamage(atacante.getAttributeValue(Atributos.EVOCATION.get()), victima.getAttributeValue(Atributos.EVOCATION_RESIST.get()), damageOriginal);
            natureDamage = calcularDamage(atacante.getAttributeValue(Atributos.NATURE.get()), victima.getAttributeValue(Atributos.NATURE_RESIST.get()), damageOriginal);
            eldritchDamage = calcularDamage(atacante.getAttributeValue(Atributos.ELDRITCH.get()), victima.getAttributeValue(Atributos.ELDRITCH_RESIST.get()), damageOriginal);

            damageModificado = slashDamage + strikeDamage + pierceDamage + fireDamage + iceDamage + lightingDamage + aquaDamage + holyDamage + enderDamage + bloodDamage + evocationDamage + natureDamage + eldritchDamage;

            atributosMinimos = atributosMinimos - Atributos.atributosTotales(atacante);

            if (atributosMinimos > 0){
                damageModificado = damageModificado + (damageOriginal * (atributosMinimos / 100));
            }

            event.setAmount((float) damageModificado);
        }else {

            if (dataDamage == null)return;

            slashDamage = calcularDamage(dataDamage.damage().getSlash(), victima.getAttributeValue(Atributos.SLASH_RESIST.get()), damageOriginal);
            strikeDamage = calcularDamage(dataDamage.damage().getStrike(), victima.getAttributeValue(Atributos.STRIKE_RESIST.get()), damageOriginal);
            pierceDamage = calcularDamage(dataDamage.damage().getPierce(), victima.getAttributeValue(Atributos.PIERCE_RESIST.get()), damageOriginal);
            fireDamage = calcularDamage(dataDamage.damage().getFire(), victima.getAttributeValue(Atributos.FIRE_RESIST.get()), damageOriginal);
            iceDamage = calcularDamage(dataDamage.damage().getIce(), victima.getAttributeValue(Atributos.ICE_RESIST.get()), damageOriginal);
            lightingDamage = calcularDamage(dataDamage.damage().getLighting(), victima.getAttributeValue(Atributos.LIGHTING_RESIST.get()), damageOriginal);
            aquaDamage = calcularDamage(dataDamage.damage().getAqua(), victima.getAttributeValue(Atributos.AQUA_RESIST.get()), damageOriginal);
            holyDamage = calcularDamage(dataDamage.damage().getHoly(), victima.getAttributeValue(Atributos.HOLY_RESIST.get()), damageOriginal);
            enderDamage = calcularDamage(dataDamage.damage().getEnder(), victima.getAttributeValue(Atributos.ENDER_RESIST.get()), damageOriginal);
            bloodDamage = calcularDamage(dataDamage.damage().getBlood(), victima.getAttributeValue(Atributos.BLOOD_RESIST.get()), damageOriginal);
            evocationDamage = calcularDamage(dataDamage.damage().getEvocation(), victima.getAttributeValue(Atributos.EVOCATION_RESIST.get()), damageOriginal);
            natureDamage = calcularDamage(dataDamage.damage().getNature(), victima.getAttributeValue(Atributos.NATURE_RESIST.get()), damageOriginal);
            eldritchDamage = calcularDamage(dataDamage.damage().getEldritch(), victima.getAttributeValue(Atributos.ELDRITCH_RESIST.get()), damageOriginal);

            damageModificado = slashDamage + strikeDamage + pierceDamage + fireDamage + iceDamage + lightingDamage + aquaDamage + holyDamage + enderDamage + bloodDamage + evocationDamage + natureDamage + eldritchDamage;

            atributosMinimos = atributosMinimos - Atributos.atributosTotales(dataDamage);

            if (atributosMinimos > 0){
                damageModificado = damageModificado + (damageOriginal * (atributosMinimos / 100));
            }

            event.setAmount((float) damageModificado);

        }

        MostrarCalculo.obtenerDatos(damageOriginal, slashDamage, strikeDamage, pierceDamage, fireDamage, iceDamage, lightingDamage, aquaDamage, holyDamage, enderDamage, bloodDamage, evocationDamage, natureDamage, eldritchDamage, damageModificado);
    }

    public static double calcularDamage(double ataqueValor, double defensaValor, float damageOriginal){

        double damageFinal = 0.0;

        if (ataqueValor > 0){
            damageFinal = damageOriginal * (ataqueValor / 100);
            damageFinal = calcularResistencia(damageFinal, defensaValor);
        }

        return damageFinal;
    }

    public static double calcularResistencia(double damage, double resistencia){
        if (resistencia > 0 && resistencia <= 100){
            damage *= 1 - (resistencia / 100);
        }else if (resistencia > 0){
            damage = 0;
        }else damage *= 1 + (Math.abs(resistencia) / 100);

        return damage;
    }

}