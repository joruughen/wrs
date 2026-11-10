package dev.joruughen.ews.eventos;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Elemento;
import dev.joruughen.ews.atributos.PerfilElemental;
import dev.joruughen.ews.data.DamageTypeData;
import dev.joruughen.ews.data.DataLoaderEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
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

        DamageSource source = event.getSource();

        // La fuente de daño no siempre es una entidad viviente (ej. TNT, cactus): evitamos el ClassCastException.
        LivingEntity atacante = source.getEntity() instanceof LivingEntity fuente ? fuente : null;
        if (atacante != null && source.isIndirect() && source.getDirectEntity() instanceof LivingEntity atacanteDirecto) {
            atacante = atacanteDirecto;
        }

        float damageOriginal = event.getAmount();

        ResourceKey<DamageType> damageTypeResourceKey = source.typeHolder().unwrapKey().orElse(null);
        ResourceLocation damagetype = damageTypeResourceKey != null ? damageTypeResourceKey.location() : null;

        DamageTypeData dataDamage = damagetype != null ? DataLoaderEvent.DAMAGE_TYPES.getData().get(damagetype) : null;
        boolean especial = dataDamage != null && dataDamage.special();

        // Perfil de ataque: damage_types "special" > atacante (con sus reglas por damage type) > damage_types normal
        PerfilElemental.Resultado ataque;
        if (atacante != null && !especial) {
            ataque = PerfilElemental.ataque(atacante, source);
        } else if (dataDamage != null) {
            ataque = PerfilElemental.desdeDamageType(dataDamage, damagetype);
        } else {
            return;
        }

        // Las resistencias de la víctima (con sus reglas por damage type) se aplican en todos los casos
        PerfilElemental.Resultado resistencia = PerfilElemental.resistencia(victima, source);

        Map<Elemento, Double> desglose = new EnumMap<>(Elemento.class);
        for (Elemento elemento : Elemento.values()) {
            desglose.put(elemento, calcularDamage(ataque.valor(elemento), resistencia.valor(elemento), damageOriginal));
        }

        double damageModificado = desglose.values().stream().mapToDouble(Double::doubleValue).sum();

        // Si el perfil de ataque no llega a 100, el resto del golpe entra como daño sin tipo (sin resistencias)
        double atributosMinimos = 100 - ataque.total();
        if (atributosMinimos > 0) {
            damageModificado = damageModificado + (damageOriginal * (atributosMinimos / 100));
        }

        event.setAmount((float) damageModificado);

        MostrarCalculo.obtenerDatos(damageOriginal, desglose, damageModificado, damagetype, atacante, victima,
                ataque.descripcion(), resistencia.descripcion());
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
