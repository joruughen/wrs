package dev.fede2010.wrs.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record AtributosDataType(
        List<ResourceLocation>ids,
        AtributosData damage,
        AtributosData resistance
) {
    public static final Codec<AtributosDataType> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.listOf()
                            .optionalFieldOf("ids", List.of()) // Hace que "ids" sea opcional con una lista vacía por defecto
                            .forGetter(AtributosDataType::ids),
                    AtributosData.CODEC.optionalFieldOf("damage", AtributosData.ATRIBUTOS_VACIOS).forGetter(AtributosDataType::damage),
                    AtributosData.CODEC.optionalFieldOf("resistance", AtributosData.ATRIBUTOS_VACIOS).forGetter(AtributosDataType::resistance)
            ).apply(instance, AtributosDataType::new)
    );

    public static AtributosDataType reemplazarValores(AtributosDataType grupo, AtributosDataType entidad) {

        Optional<Double> slash     = Optional.of(entidad.damage().slash().orElse(grupo.damage().getSlash()));
        Optional<Double> strike    = Optional.of(entidad.damage().strike().orElse(grupo.damage().getStrike()));
        Optional<Double> pierce    = Optional.of(entidad.damage().pierce().orElse(grupo.damage().getPierce()));
        Optional<Double> fire      = Optional.of(entidad.damage().fire().orElse(grupo.damage().getFire()));
        Optional<Double> ice       = Optional.of(entidad.damage().ice().orElse(grupo.damage().getIce()));
        Optional<Double> lighting  = Optional.of(entidad.damage().lighting().orElse(grupo.damage().getLighting()));
        Optional<Double> aqua      = Optional.of(entidad.damage().aqua().orElse(grupo.damage().getAqua()));
        Optional<Double> holy      = Optional.of(entidad.damage().holy().orElse(grupo.damage().getHoly()));
        Optional<Double> ender     = Optional.of(entidad.damage().ender().orElse(grupo.damage().getEnder()));
        Optional<Double> blood     = Optional.of(entidad.damage().blood().orElse(grupo.damage().getBlood()));
        Optional<Double> evocation = Optional.of(entidad.damage().evocation().orElse(grupo.damage().getEvocation()));
        Optional<Double> nature    = Optional.of(entidad.damage().nature().orElse(grupo.damage().getNature()));
        Optional<Double> eldritch  = Optional.of(entidad.damage().eldritch().orElse(grupo.damage().getEldritch()));

        Optional<Double> slashResist     = Optional.of(entidad.resistance().slash().orElse(grupo.resistance().getSlash()));
        Optional<Double> strikeResist    = Optional.of(entidad.resistance().strike().orElse(grupo.resistance().getStrike()));
        Optional<Double> pierceResist    = Optional.of(entidad.resistance().pierce().orElse(grupo.resistance().getPierce()));
        Optional<Double> fireResist      = Optional.of(entidad.resistance().fire().orElse(grupo.resistance().getFire()));
        Optional<Double> iceResist       = Optional.of(entidad.resistance().ice().orElse(grupo.resistance().getIce()));
        Optional<Double> lightingResist  = Optional.of(entidad.resistance().lighting().orElse(grupo.resistance().getLighting()));
        Optional<Double> aquaResist      = Optional.of(entidad.resistance().aqua().orElse(grupo.resistance().getAqua()));
        Optional<Double> holyResist      = Optional.of(entidad.resistance().holy().orElse(grupo.resistance().getHoly()));
        Optional<Double> enderResist     = Optional.of(entidad.resistance().ender().orElse(grupo.resistance().getEnder()));
        Optional<Double> bloodResist     = Optional.of(entidad.resistance().blood().orElse(grupo.resistance().getBlood()));
        Optional<Double> evocationResist = Optional.of(entidad.resistance().evocation().orElse(grupo.resistance().getEvocation()));
        Optional<Double> natureResist    = Optional.of(entidad.resistance().nature().orElse(grupo.resistance().getNature()));
        Optional<Double> eldritchResist  = Optional.of(entidad.resistance().eldritch().orElse(grupo.resistance().getEldritch()));

        return new AtributosDataType(
                List.of(),
                new AtributosData(slash, strike, pierce, fire, ice, lighting, aqua, holy, ender, blood, evocation, nature, eldritch),
                new AtributosData(slashResist, strikeResist, pierceResist, fireResist, iceResist, lightingResist, aquaResist, holyResist, enderResist, bloodResist, evocationResist, natureResist, eldritchResist)
        );
    }

}