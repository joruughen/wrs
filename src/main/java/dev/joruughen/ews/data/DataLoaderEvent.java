package dev.joruughen.ews.data;

import dev.joruughen.ews.Ews;
import dev.joruughen.ews.network.DataCategory;
import dev.joruughen.ews.network.DataSyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.PacketDistributor;

public class DataLoaderEvent {
    public static final AtributosDataLoader GROUPS = new AtributosDataLoader("groups");
    public static final AtributosDataLoader ENTITIES = new AtributosDataLoader("entities");
    public static final AtributosDataLoader ITEMS = new AtributosDataLoader("items");
    public static final DamageTypeDataLoader DAMAGE_TYPES = new DamageTypeDataLoader("damage_types");

    /** Datos de una entidad (grupo + específico ya combinados), o null si el datapack no la define. */
    public static AtributosDataType datosEntidad(ResourceLocation entityId) {
        if (entityId == null) return null;
        return AtributosDataType.resolver(GROUPS.getData().get(entityId), ENTITIES.getData().get(entityId));
    }

    /** Datos de un ítem (grupo + específico ya combinados), o null si el datapack no lo define. */
    public static AtributosDataType datosItem(ResourceLocation itemId) {
        if (itemId == null) return null;
        return AtributosDataType.resolver(GROUPS.getData().get(itemId), ITEMS.getData().get(itemId));
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(GROUPS);
        event.addListener(ENTITIES);
        event.addListener(ITEMS);
        event.addListener(DAMAGE_TYPES);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Ews.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new DataSyncPacket(DataCategory.GROUPS, GROUPS.getData()));

            Ews.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new DataSyncPacket(DataCategory.ENTITIES, ENTITIES.getData()));

            Ews.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                    new DataSyncPacket(DataCategory.ITEMS, ITEMS.getData()));
        }
    }

}