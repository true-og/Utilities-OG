// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle, SKBotNL.
package net.trueog.utilitiesog.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.tr7zw.nbtapi.NBTContainer;
import net.trueog.utilitiesog.Internal;

// Reflection bridge to the fork's PlayerDataStorage. Tags are raw NMS objects.
public final class PlayerDataUtils {

    private static final String DATA_VERSION = "DataVersion";

    private final Object storage;
    private final Method getMethod;
    private final Method saveMethod;
    private final Method hasMethod;
    private final Method convertMethod;
    private final Object playerType;
    private final String unavailableReason;

    public PlayerDataUtils() {

        final Logger logger = Internal.getPlugin().getLogger();
        Object boundStorage = null;
        final Method[] bound = new Method[4];
        Object boundPlayerType = null;
        String reason = null;
        try {

            final Object server = Bukkit.getServer();
            final Object playerList = server.getClass().getMethod("getHandle").invoke(server);
            boundStorage = findStorage(playerList);
            if (boundStorage == null) {

                reason = "Not running TrueOG Purpur jar";

            } else {

                final Class<?> type = boundStorage.getClass();
                final Method save = findSaveMethod(type);
                final Class<?> tagType = save.getParameterTypes()[1];
                bound[0] = type.getMethod("getPlayerData", UUID.class);
                bound[1] = save;
                bound[2] = type.getMethod("hasPlayerData", UUID.class);
                // Paper's data converter is not obfuscated, so plugins can run the player
                // fixes on tags they keep elsewhere
                bound[3] = findConvertMethod(tagType);
                boundPlayerType = Class.forName("ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry")
                        .getField("PLAYER").get(null);

            }

        } catch (Throwable t) {

            reason = "Failed to bind the TrueOG Purpur player data API";
            logger.log(Level.WARNING, reason, t);

        }

        final boolean ok = (reason == null);
        this.storage = ok ? boundStorage : null;
        this.getMethod = ok ? bound[0] : null;
        this.saveMethod = ok ? bound[1] : null;
        this.hasMethod = ok ? bound[2] : null;
        this.convertMethod = ok ? bound[3] : null;
        this.playerType = ok ? boundPlayerType : null;
        this.unavailableReason = reason;
        if (reason != null) {

            logger.warning(reason + ", player data APIs are unavailable");

        }

    }

    // Field names are obfuscated, so find the field whose type declares
    // saveCompoundTag(UUID, tag).
    private static Object findStorage(Object playerList) throws IllegalAccessException {

        for (Class<?> type = playerList.getClass(); type != null; type = type.getSuperclass()) {

            for (Field field : type.getDeclaredFields()) {

                if (Modifier.isStatic(field.getModifiers()) || findSaveMethod(field.getType()) == null) {

                    continue;

                }

                field.setAccessible(true);
                return field.get(playerList);

            }

        }

        return null;

    }

    // Resolved on the class, so a hook subclass still binds. The tag parameter
    // type is NMS, so it is matched by position rather than by name.
    private static Method findSaveMethod(Class<?> type) {

        for (Method method : type.getMethods()) {

            if (method.getName().equals("saveCompoundTag") && method.getParameterCount() == 2
                    && method.getParameterTypes()[0] == UUID.class)
            {

                return method;

            }

        }

        return null;

    }

    // MCDataConverter.convertTag(type, tag, fromVersion, toVersion)
    private static Method findConvertMethod(Class<?> tagType) throws ClassNotFoundException {

        final Class<?> converter = Class.forName("ca.spottedleaf.dataconverter.minecraft.MCDataConverter");
        for (Method method : converter.getMethods()) {

            if (method.getName().equals("convertTag") && method.getParameterCount() == 4
                    && method.getParameterTypes()[1] == tagType)
            {

                return method;

            }

        }

        throw new ClassNotFoundException("MCDataConverter.convertTag for " + tagType.getName());

    }

    public boolean isAvailable() {

        return storage != null;

    }

    // Item-NBT-API is a hard dependency, so tags can always become Bukkit item
    // stacks when the fork API is bound.
    public boolean isItemConversionAvailable() {

        return storage != null;

    }

    private void requireAvailable() {

        if (storage == null) {

            throw new UnsupportedOperationException(unavailableReason + ", player data APIs are unavailable");

        }

    }

    // Vanilla profile, null when absent or the storage is closed. The server
    // throws IllegalStateException for a profile it could not read.
    public Object get(UUID uuid) {

        requireAvailable();
        return invoke(getMethod, uuid);

    }

    // The server throws IllegalStateException when the write failed.
    public void save(UUID uuid, Object tag) {

        requireAvailable();
        invoke(saveMethod, uuid, tag);

    }

    public boolean has(UUID uuid) {

        requireAvailable();
        return Boolean.TRUE.equals(invoke(hasMethod, uuid));

    }

    // The server lists its stored profiles through Bukkit.getOfflinePlayers()
    public List<UUID> seen() {

        requireAvailable();
        final OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        final List<UUID> uuids = new ArrayList<>(players.length);
        for (OfflinePlayer player : players) {

            uuids.add(player.getUniqueId());

        }

        return uuids;

    }

    // Runs the server's player data fixes from the tag's DataVersion (absent
    // counts as the oldest, like vanilla) to the current one, then stamps it.
    public Object upgrade(Object tag) {

        requireAvailable();
        final int toVersion = Bukkit.getUnsafe().getDataVersion();
        final NBTContainer stored = new NBTContainer(tag);
        final int fromVersion = stored.hasTag(DATA_VERSION) ? stored.getInteger(DATA_VERSION) : -1;
        Object upgraded = tag;
        if (fromVersion < toVersion) {

            try {

                upgraded = convertMethod.invoke(null, playerType, tag, fromVersion, toVersion);

            } catch (InvocationTargetException e) {

                throw new RuntimeException("Player data upgrade failed", e.getCause());

            } catch (IllegalAccessException e) {

                throw new RuntimeException(e);

            }

        }

        new NBTContainer(upgraded).setInteger(DATA_VERSION, toVersion);
        return upgraded;

    }

    private Object invoke(Method method, Object... args) {

        try {

            return method.invoke(storage, args);

        } catch (InvocationTargetException e) {

            if (e.getCause() instanceof RuntimeException) {

                throw (RuntimeException) e.getCause();

            }

            throw new RuntimeException("Player data API call " + method.getName() + " failed", e.getCause());

        } catch (IllegalAccessException e) {

            throw new RuntimeException(e);

        }

    }

}
