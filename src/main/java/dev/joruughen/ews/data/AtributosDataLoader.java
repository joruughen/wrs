package dev.joruughen.ews.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.joruughen.ews.Ews;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class AtributosDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    private Map<ResourceLocation, AtributosDataType> data = new HashMap<>();

    public AtributosDataLoader(String ruta) {
        super(GSON, ruta);
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

                        if (!d.ids().isEmpty() && !d.ids().iterator().next().getPath().isEmpty()) {
                            for (ResourceLocation dataId : d.ids()) {
                                if (this.data.containsKey(dataId)) {

                                    AtributosData damageFinal = d.damage().combinar(this.data.get(dataId).damage());
                                    AtributosData resistenciaFinal = d.resistance().combinar(this.data.get(dataId).resistance());

                                    AtributosDataType nuevo = new AtributosDataType(d.ids(), damageFinal, resistenciaFinal);

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

    public Map<ResourceLocation, AtributosDataType> getData() {
        return data;
    }
}