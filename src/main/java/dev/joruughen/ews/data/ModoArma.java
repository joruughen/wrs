package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

/**
 * Cómo se combina el ataque base de una entidad con lo que aporta el ítem de su mano principal
 * ("weapon_mode" en entities/groups o dentro de una regla de damage_types).
 */
public enum ModoArma {
    /** Se suman (comportamiento de siempre). */
    ADD("add"),
    /** Se suman y, si el total de ataque pasa de 100, se escala proporcionalmente a 100. */
    SHARE("share"),
    /** Si el arma aporta algún elemento de ataque, el ataque base de la entidad se ignora. */
    WEAPON("weapon");

    private final String id;

    ModoArma(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static final Codec<ModoArma> CODEC = Codec.STRING.flatXmap(
            texto -> {
                for (ModoArma modo : values()) {
                    if (modo.id.equals(texto)) return DataResult.success(modo);
                }
                return DataResult.error(() -> "weapon_mode desconocido: '" + texto + "' (usar add, share o weapon)");
            },
            modo -> DataResult.success(modo.id)
    );
}
