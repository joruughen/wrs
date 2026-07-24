package dev.joruughen.ews.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;

public record AtributosData(
        Optional<Double> slash,
        Optional<Double> strike,
        Optional<Double> pierce,
        Optional<Double> fire,
        Optional<Double> ice,
        Optional<Double> lighting,
        Optional<Double> aqua,
        Optional<Double> holy,
        Optional<Double> ender,
        Optional<Double> blood,
        Optional<Double> evocation,
        Optional<Double> nature,
        Optional<Double> eldritch
) {
    public static final Codec<AtributosData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.DOUBLE.optionalFieldOf("slash").forGetter(AtributosData::slash),
                    Codec.DOUBLE.optionalFieldOf("strike").forGetter(AtributosData::strike),
                    Codec.DOUBLE.optionalFieldOf("pierce").forGetter(AtributosData::pierce),
                    Codec.DOUBLE.optionalFieldOf("fire").forGetter(AtributosData::fire),
                    Codec.DOUBLE.optionalFieldOf("ice").forGetter(AtributosData::ice),
                    Codec.DOUBLE.optionalFieldOf("lighting").forGetter(AtributosData::lighting),
                    Codec.DOUBLE.optionalFieldOf("aqua").forGetter(AtributosData::aqua),
                    Codec.DOUBLE.optionalFieldOf("holy").forGetter(AtributosData::holy),
                    Codec.DOUBLE.optionalFieldOf("ender").forGetter(AtributosData::ender),
                    Codec.DOUBLE.optionalFieldOf("blood").forGetter(AtributosData::blood),
                    Codec.DOUBLE.optionalFieldOf("evocation").forGetter(AtributosData::evocation),
                    Codec.DOUBLE.optionalFieldOf("nature").forGetter(AtributosData::nature),
                    Codec.DOUBLE.optionalFieldOf("eldritch").forGetter(AtributosData::eldritch)
            ).apply(instance, AtributosData::new)
    );

    public static final AtributosData ATRIBUTOS_VACIOS = new AtributosData(
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty()
    );

    public Double getSlash() { return slash().orElse(0.0); }
    public Double getStrike() { return strike().orElse(0.0); }
    public Double getPierce() { return pierce().orElse(0.0); }
    public Double getFire() { return fire().orElse(0.0); }
    public Double getIce() { return ice().orElse(0.0); }
    public Double getLighting() { return lighting().orElse(0.0); }
    public Double getAqua() { return aqua().orElse(0.0); }
    public Double getHoly() { return holy().orElse(0.0); }
    public Double getEnder() { return ender().orElse(0.0); }
    public Double getBlood() { return blood().orElse(0.0); }
    public Double getEvocation() { return evocation().orElse(0.0); }
    public Double getNature() { return nature().orElse(0.0); }
    public Double getEldritch() { return eldritch().orElse(0.0); }

    public AtributosData combinar(AtributosData atributosDataNuevo) {
        return new AtributosData(
                Optional.of(this.getSlash() + atributosDataNuevo.getSlash()),
                Optional.of(this.getStrike() + atributosDataNuevo.getStrike()),
                Optional.of(this.getPierce() + atributosDataNuevo.getPierce()),
                Optional.of(this.getFire() + atributosDataNuevo.getFire()),
                Optional.of(this.getIce() + atributosDataNuevo.getIce()),
                Optional.of(this.getLighting() + atributosDataNuevo.getLighting()),
                Optional.of(this.getAqua() + atributosDataNuevo.getAqua()),
                Optional.of(this.getHoly() + atributosDataNuevo.getHoly()),
                Optional.of(this.getEnder() + atributosDataNuevo.getEnder()),
                Optional.of(this.getBlood() + atributosDataNuevo.getBlood()),
                Optional.of(this.getEvocation() + atributosDataNuevo.getEvocation()),
                Optional.of(this.getNature() + atributosDataNuevo.getNature()),
                Optional.of(this.getEldritch() + atributosDataNuevo.getEldritch())
        );
    }
}

