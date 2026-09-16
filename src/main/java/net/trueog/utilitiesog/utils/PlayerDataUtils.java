package net.trueog.utilitiesog.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.trueog.utilitiesog.Internal;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftServer;

import java.lang.reflect.Method;
import java.util.UUID;

public class PlayerDataUtils {

    private final PlayerDataStorage playerIo = ((CraftServer) Bukkit.getServer()).getServer().getPlayerList().playerIo;
    private Method getPlayerData;
    private Method saveCompoundTag;

    public PlayerDataUtils() {

        try {

            getPlayerData = playerIo.getClass().getDeclaredMethod("getPlayerData", UUID.class);
            saveCompoundTag = playerIo.getClass().getDeclaredMethod("saveCompoundTag", UUID.class, CompoundTag.class);

        } catch (NoSuchMethodException e) {

            getPlayerData = null;
            saveCompoundTag = null;
            Internal.getPlugin().getLogger().warning("Not running TrueOG Purpur jar, player data APIs are unavailable");

        }

    }

    public CompoundTag getPlayerData(UUID uuid) {

        if (getPlayerData == null) {

            throw new UnsupportedOperationException("Not running TrueOG Purpur jar, player data APIs are unavailable");

        }

        try {

            return (CompoundTag) getPlayerData.invoke(playerIo, uuid);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    private void saveCompoundTag(UUID uuid, CompoundTag tag) {

        if (saveCompoundTag == null) {

            throw new UnsupportedOperationException("Not running TrueOG Purpur jar, player data APIs are unavailable");

        }

        try {

            saveCompoundTag.invoke(playerIo, uuid, tag);

        } catch (Exception e) {

            throw new RuntimeException(e);

        }

    }

    public void setInventoryData(UUID uuid, ListTag inventoryData) {

        final CompoundTag playerData = getPlayerData(uuid);
        playerData.put("Inventory", inventoryData);
        saveCompoundTag(uuid, playerData);

    }

}
