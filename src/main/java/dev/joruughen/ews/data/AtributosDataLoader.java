package dev.joruughen.ews.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.joruughen.ews.Ews;
import dev.joruughen.ews.atributos.Atributos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtributosDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private Map<ResourceLocation, AtributosDataType> data = new HashMap<>();
    private final String ruta;

    public AtributosDataLoader(String ruta) {
        super(GSON, ruta);
        this.ruta = ruta;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        this.data.clear();

        jsons.forEach((fileId, json) -> {

            AtributosDataType.CODEC.parse(JsonOps.INSTANCE, json)
                    .resultOrPartial(error -> {
                        Ews.LOGGER.error("Error loading {}: {}", fileId, error);
                    })
                    .ifPresent(d -> {
                        validarReglas(fileId, d);

                        if (!d.ids().isEmpty() && !d.ids().iterator().next().getPath().isEmpty()) {
                            for (ResourceLocation dataId : d.ids()) {
                                if (this.data.containsKey(dataId)) {

                                    AtributosDataType anterior = this.data.get(dataId);

                                    List<Modificador> modificadoresFinales = new ArrayList<>(anterior.modifiers());
                                    modificadoresFinales.addAll(d.modifiers());

                                    List<ReglaDamageType> reglasFinales = new ArrayList<>(anterior.damageTypes());
                                    reglasFinales.addAll(d.damageTypes());

                                    // Si dos grupos definen weapon_mode para el mismo id, se queda el primero que se cargó
                                    AtributosDataType nuevo = new AtributosDataType(d.ids(), modificadoresFinales, reglasFinales,
                                            anterior.weaponMode().or(d::weaponMode));

                                    this.data.put(dataId, nuevo);
                                } else {
                                    this.data.put(dataId, d);
                                }
                            }
                        } else {
                            // Lógica nueva para archivos individuales
                            String path = fileId.getPath();
                            String[] pathParts = path.split("/");

                            if (pathParts.length >= 2) {
                                String targetNamespace = pathParts[0];
                                String targetPath = String.join("/", Arrays.copyOfRange(pathParts, 1, pathParts.length))
                                        .replace(".json", ""); // Eliminar extensión si existe

                                ResourceLocation targetId = new ResourceLocation(targetNamespace, targetPath);

                                this.data.put(targetId, d);
                            } else {
                                Ews.LOGGER.error("Invalid path for single file: {}", fileId);
                            }
                        }
                    });
        });

    }

    // Avisa en el log de reglas por damage type que no van a tener efecto, para que el autor del datapack lo note
    private void validarReglas(ResourceLocation fileId, AtributosDataType data) {
        boolean esItems = "items".equals(ruta);
        if (esItems && data.weaponMode().isPresent()) {
            Ews.LOGGER.warn("{}: \"weapon_mode\" solo tiene efecto en entities/groups de entidades, en items se ignora", fileId);
        }
        for (ReglaDamageType regla : data.damageTypes()) {
            if (esItems && regla.weaponMode().isPresent()) {
                Ews.LOGGER.warn("{}: \"weapon_mode\" dentro de una regla de un ítem se ignora", fileId);
            }
            if (regla.types().isEmpty()) {
                Ews.LOGGER.warn("{}: regla de damage_types con \"types\" vacío, nunca se va a aplicar", fileId);
            }
            for (Modificador modificador : regla.modifiers()) {
                if (Atributos.elementoDe(modificador.atributo()) == null) {
                    Ews.LOGGER.warn("{}: el atributo '{}' de una regla de damage_types no es de EWS y se ignora (solo valen los 13 elementos y sus _resist)",
                            fileId, modificador.atributo());
                }
            }
        }
    }

    public Map<ResourceLocation, AtributosDataType> getData() {
        return data;
    }
}