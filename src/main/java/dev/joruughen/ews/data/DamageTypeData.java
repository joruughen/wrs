package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record DamageTypeData(
        AtributosData damage,
        boolean special
) {
    public static final Codec<DamageTypeData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    AtributosData.CODEC.optionalFieldOf("damage", AtributosData.ATRIBUTOS_VACIOS).forGetter(DamageTypeData::damage),
                    Codec.BOOL.optionalFieldOf("special", false).forGetter(DamageTypeData::special)
            ).apply(instance, DamageTypeData::new)
    );
}
