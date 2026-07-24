package dev.joruughen.ews.network;

import dev.joruughen.ews.data.AtributosDataType;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ClientDataHolder {
    private static Map<ResourceLocation, AtributosDataType> CLIENT_GROUPS_DATA = new HashMap<>();
    private static Map<ResourceLocation, AtributosDataType> CLIENT_ENTITIES_DATA = new HashMap<>();
    private static Map<ResourceLocation, AtributosDataType> CLIENT_ITEMS_DATA = new HashMap<>();

    public static void setData(DataCategory category, Map<ResourceLocation, AtributosDataType> data) {
        switch (category) {
            case GROUPS -> CLIENT_GROUPS_DATA = data;
            case ENTITIES -> CLIENT_ENTITIES_DATA = data;
            case ITEMS -> CLIENT_ITEMS_DATA = data;
        }
    }

    public static AtributosDataType getGroupsData(ResourceLocation id) {
        return CLIENT_GROUPS_DATA.get(id);
    }
    public static AtributosDataType getEntitiesData(ResourceLocation id) {
        return CLIENT_ENTITIES_DATA.get(id);
    }
    public static AtributosDataType getItemsData(ResourceLocation id) {
        return CLIENT_ITEMS_DATA.get(id);
    }
}
