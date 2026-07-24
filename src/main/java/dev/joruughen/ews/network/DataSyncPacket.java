package dev.joruughen.ews.network;

import dev.joruughen.ews.data.AtributosDataType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class DataSyncPacket {
    private final DataCategory category;
    private final Map<ResourceLocation, AtributosDataType> data;

    public DataSyncPacket(DataCategory category, Map<ResourceLocation, AtributosDataType> data) {
        this.category = category;
        this.data = data;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(category); // Enviar la categoría primero
        buffer.writeMap(data,
                FriendlyByteBuf::writeResourceLocation,
                (buf, value) -> buf.writeJsonWithCodec(AtributosDataType.CODEC, value)
        );
    }

    public static DataSyncPacket decode(FriendlyByteBuf buffer) {
        DataCategory category = buffer.readEnum(DataCategory.class); // Leer la categoría
        Map<ResourceLocation, AtributosDataType> data = buffer.readMap(
                FriendlyByteBuf::readResourceLocation,
                buf -> buf.readJsonWithCodec(AtributosDataType.CODEC)
        );
        return new DataSyncPacket(category, data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Guardar los datos en el cliente según la categoría
            ClientDataHolder.setData(category, data);
        });
        ctx.get().setPacketHandled(true);
    }
}
