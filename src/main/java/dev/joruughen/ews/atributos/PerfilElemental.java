package dev.joruughen.ews.atributos;

import dev.joruughen.ews.data.AtributosDataType;
import dev.joruughen.ews.data.DamageTypeData;
import dev.joruughen.ews.data.DataLoaderEvent;
import dev.joruughen.ews.data.Modificador;
import dev.joruughen.ews.data.ModoArma;
import dev.joruughen.ews.data.ReglaDamageType;
import dev.joruughen.ews.eventos.AgregarModificadorItem;
import dev.joruughen.ews.eventos.AgregarModificadorItem.ModificadorAplicado;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Calcula los valores elementales (ataque o resistencia) que una entidad tiene contra un damage type
 * concreto, aplicando las reglas "damage_types" de su archivo de entities/groups y de los ítems que
 * tiene equipados. Si nada define reglas para ese damage type, el resultado es exactamente el valor
 * actual de sus atributos (comportamiento de siempre).
 */
public final class PerfilElemental {

    /** Valores por elemento y descripción de las reglas que se usaron (para el modo debug). */
    public record Resultado(Map<Elemento, Double> valores, List<String> reglas) {
        public double valor(Elemento elemento) {
            return valores.getOrDefault(elemento, 0.0);
        }

        public double total() {
            return valores.values().stream().mapToDouble(Double::doubleValue).sum();
        }

        public String descripcion() {
            return reglas.isEmpty() ? "default" : String.join(" | ", reglas);
        }
    }

    private PerfilElemental() {
    }

    public static Resultado ataque(LivingEntity atacante, DamageSource source) {
        return calcular(atacante, source, false);
    }

    public static Resultado resistencia(LivingEntity victima, DamageSource source) {
        return calcular(victima, source, true);
    }

    /** Perfil de ataque fijo de un archivo de la carpeta damage_types. */
    public static Resultado desdeDamageType(DamageTypeData data, ResourceLocation damageType) {
        Map<Elemento, Double> valores = new EnumMap<>(Elemento.class);
        for (Elemento elemento : Elemento.values()) {
            valores.put(elemento, elemento.valorEn(data.damage()));
        }
        return new Resultado(valores, List.of("damage_types/" + damageType));
    }

    private static Resultado calcular(LivingEntity entidad, DamageSource source, boolean resist) {
        Holder<DamageType> tipo = source.typeHolder();
        List<String> descripcion = new ArrayList<>();

        // 1) Regla de la propia entidad (entities / groups)
        ResourceLocation entityId = ForgeRegistries.ENTITY_TYPES.getKey(entidad.getType());
        AtributosDataType datosEntidad = DataLoaderEvent.datosEntidad(entityId);
        ReglaDamageType reglaEntidad = datosEntidad != null ? ReglaDamageType.buscar(datosEntidad.damageTypes(), tipo, resist) : null;
        boolean conservarBonos = reglaEntidad == null || reglaEntidad.keepBonuses();
        // Una regla con solo weapon_mode (sin modificadores) no toca el perfil base
        ReglaDamageType reglaBase = reglaEntidad != null && reglaEntidad.tieneModificadores(resist) ? reglaEntidad : null;
        ModoArma modoArma = modoArma(datosEntidad, reglaEntidad, resist);

        if (reglaEntidad != null) {
            descripcion.add(entityId + " [" + describir(reglaEntidad) + "]");
        }

        // 2) Reglas de los ítems equipados: cambian solo lo que aporta ese ítem
        Set<UUID> quitar = new HashSet<>();
        List<ModificadorAplicado> agregar = new ArrayList<>();
        List<ModificadorAplicado> agregarMano = new ArrayList<>();

        if (conservarBonos) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                ItemStack stack = entidad.getItemBySlot(slot);
                if (stack.isEmpty()) continue;

                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(stack.getItem());
                AtributosDataType datosItem = DataLoaderEvent.datosItem(itemId);
                if (datosItem == null || datosItem.damageTypes().isEmpty()) continue;

                ReglaDamageType regla = ReglaDamageType.buscar(datosItem.damageTypes(), tipo, resist);
                if (regla == null) continue;

                for (ModificadorAplicado base : AgregarModificadorItem.modificadoresBase(stack, slot, false)) {
                    if (reemplaza(regla, base.idAtributo(), resist)) {
                        quitar.add(base.modificador().getId());
                    }
                }

                List<Modificador> modificadores = regla.modifiers();
                for (int i = 0; i < modificadores.size(); i++) {
                    Modificador modificador = modificadores.get(i);
                    if (!esDeCategoria(modificador.atributo(), resist) || !modificador.aplicaEnSlot(slot)) continue;

                    Attribute atributo = ForgeRegistries.ATTRIBUTES.getValue(modificador.atributo());
                    if (atributo == null) continue;

                    String nombre = "regla:" + itemId + ":" + slot + ":" + i;
                    ModificadorAplicado aplicado = new ModificadorAplicado(modificador.atributo(), atributo, new AttributeModifier(
                            UUID.nameUUIDFromBytes(nombre.getBytes()), nombre, modificador.valorParaModificador(), modificador.operacion()));
                    agregar.add(aplicado);
                    if (slot == EquipmentSlot.MAINHAND) agregarMano.add(aplicado);
                }

                descripcion.add(itemId + " (" + slot.getName() + ") [" + describir(regla) + "]");
            }
        }

        // 3) weapon_mode: ¿el arma de la mano principal aporta algún elemento de ataque para este daño?
        boolean armaAporta = false;
        if (modoArma != ModoArma.ADD && conservarBonos) {
            for (ModificadorAplicado base : AgregarModificadorItem.modificadoresBase(entidad.getMainHandItem(), EquipmentSlot.MAINHAND, false)) {
                if (!quitar.contains(base.modificador().getId()) && aportaAtaque(base)) armaAporta = true;
            }
            for (ModificadorAplicado aplicado : agregarMano) {
                if (aportaAtaque(aplicado)) armaAporta = true;
            }
        }
        boolean ignorarBase = armaAporta && modoArma == ModoArma.WEAPON;

        // 4) Valor final por elemento
        Map<Elemento, Double> valores = new EnumMap<>(Elemento.class);
        boolean sinReglas = reglaBase == null && quitar.isEmpty() && agregar.isEmpty() && !ignorarBase;

        for (Elemento elemento : Elemento.values()) {
            Attribute atributo = Atributos.de(elemento, resist);
            AttributeInstance instancia = entidad.getAttribute(atributo);

            if (instancia == null) {
                valores.put(elemento, 0.0);
                continue;
            }
            if (sinReglas) {
                valores.put(elemento, instancia.getValue());
                continue;
            }

            ResourceLocation idAtributo = ForgeRegistries.ATTRIBUTES.getKey(atributo);
            double base = ignorarBase ? 0.0 : baseConRegla(instancia.getBaseValue(), reglaBase, idAtributo, resist);

            List<AttributeModifier> modificadores = new ArrayList<>();
            if (conservarBonos) {
                for (AttributeModifier modificador : instancia.getModifiers()) {
                    if (!quitar.contains(modificador.getId())) modificadores.add(modificador);
                }
                for (ModificadorAplicado aplicado : agregar) {
                    if (aplicado.atributo() == atributo) modificadores.add(aplicado.modificador());
                }
            }

            valores.put(elemento, calcularValor(atributo, base, modificadores));
        }

        // 5) weapon_mode share: si mob + arma pasan de 100, se reparte el golpe proporcionalmente
        if (armaAporta && modoArma == ModoArma.SHARE) {
            double total = valores.values().stream().mapToDouble(Double::doubleValue).sum();
            if (total > 100.0) {
                double factor = 100.0 / total;
                valores.replaceAll((elemento, valor) -> valor * factor);
                descripcion.add(String.format(Locale.ROOT, "%s [weapon_mode=share, x%.2f]", entityId, factor));
            } else {
                descripcion.add(entityId + " [weapon_mode=share, sin escalar]");
            }
        } else if (ignorarBase) {
            descripcion.add(entityId + " [weapon_mode=weapon]");
        }

        return new Resultado(valores, descripcion);
    }

    /** weapon_mode efectivo para el ataque: el de la regla si lo define, si no el de la entidad, si no "add". */
    private static ModoArma modoArma(AtributosDataType datosEntidad, ReglaDamageType reglaEntidad, boolean resist) {
        if (resist) return ModoArma.ADD;
        if (reglaEntidad != null && reglaEntidad.weaponMode().isPresent()) return reglaEntidad.weaponMode().get();
        return datosEntidad != null ? datosEntidad.weaponMode().orElse(ModoArma.ADD) : ModoArma.ADD;
    }

    /** Un modificador de arma "aporta" si suma (addition) un valor positivo a un atributo de ataque de EWS. */
    private static boolean aportaAtaque(ModificadorAplicado aplicado) {
        AttributeModifier modificador = aplicado.modificador();
        return Atributos.ataqueDe(aplicado.idAtributo()) != null
                && modificador.getOperation() == AttributeModifier.Operation.ADDITION
                && modificador.getAmount() > 0;
    }

    /**
     * Valor base de la entidad para este atributo una vez aplicada su regla (solo cuentan los
     * modificadores "addition" de la regla, igual que en el perfil default de entities).
     */
    private static double baseConRegla(double base, ReglaDamageType regla, ResourceLocation idAtributo, boolean resist) {
        if (regla == null) return base;

        Double valorRegla = null;
        for (Modificador modificador : regla.modifiers()) {
            if (modificador.operacion() != AttributeModifier.Operation.ADDITION || !modificador.atributo().equals(idAtributo)) continue;
            valorRegla = (valorRegla == null ? 0.0 : valorRegla) + modificador.valor();
        }

        if (regla.mode() == ReglaDamageType.Modo.ADD) {
            return base + (valorRegla == null ? 0.0 : valorRegla);
        }
        // REPLACE: el ataque se reemplaza entero (lo que no nombra la regla queda en 0);
        // las resistencias se reemplazan solo si la regla las nombra.
        if (valorRegla != null) return valorRegla;
        return resist ? base : 0.0;
    }

    /** Si la regla (de un ítem) reemplaza lo que ese ítem aporta a este atributo. */
    private static boolean reemplaza(ReglaDamageType regla, ResourceLocation idAtributo, boolean resist) {
        if (regla.mode() != ReglaDamageType.Modo.REPLACE || !esDeCategoria(idAtributo, resist)) return false;
        if (!resist) return true;
        for (Modificador modificador : regla.modifiers()) {
            if (modificador.atributo().equals(idAtributo)) return true;
        }
        return false;
    }

    private static boolean esDeCategoria(ResourceLocation idAtributo, boolean resist) {
        return (resist ? Atributos.resistenciaDe(idAtributo) : Atributos.ataqueDe(idAtributo)) != null;
    }

    /** Misma fórmula que {@link AttributeInstance#getValue()}, pero con base y modificadores propios. */
    private static double calcularValor(Attribute atributo, double base, List<AttributeModifier> modificadores) {
        double sumado = base;
        for (AttributeModifier modificador : modificadores) {
            if (modificador.getOperation() == AttributeModifier.Operation.ADDITION) sumado += modificador.getAmount();
        }
        double resultado = sumado;
        for (AttributeModifier modificador : modificadores) {
            if (modificador.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE) resultado += sumado * modificador.getAmount();
        }
        for (AttributeModifier modificador : modificadores) {
            if (modificador.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) resultado *= 1.0 + modificador.getAmount();
        }
        return atributo.sanitizeValue(resultado);
    }

    private static String describir(ReglaDamageType regla) {
        String texto = regla.tiposTexto() + ", " + (regla.mode() == ReglaDamageType.Modo.ADD ? "add" : "replace");
        if (!regla.keepBonuses()) texto += ", keep_bonuses=false";
        if (regla.weaponMode().isPresent()) texto += ", weapon_mode=" + regla.weaponMode().get().id();
        return texto;
    }
}
