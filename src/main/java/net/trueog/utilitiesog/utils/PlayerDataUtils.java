// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle, SKBotNL.
package net.trueog.utilitiesog.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.RocksDB;
import org.rocksdb.WriteBatch;

import net.trueog.utilitiesog.Internal;

// Reflection bridge to the fork's PlayerDataStorage. Tags are raw NMS objects.
public final class PlayerDataUtils {

    private final Object storage;
    private final Method getMethod;
    private final Method saveMethod;
    private final Method stageMethod;
    private final Method hasMethod;
    private final Method seenMethod;
    private final Method upgradeMethod;
    private final Method databaseMethod;
    private final Method isClosedMethod;
    private final Method getColumnFamilyMethod;
    private final Method createColumnFamilyMethod;
    private final Method dropColumnFamilyMethod;
    private final String unavailableReason;

    public PlayerDataUtils() {

        final Logger logger = Internal.getPlugin().getLogger();
        Object boundStorage = null;
        final Method[] bound = new Method[11];
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
                bound[2] = type.getMethod("stagePlayerData", WriteBatch.class, UUID.class, tagType);
                bound[3] = type.getMethod("hasPlayerData", UUID.class);
                // Vanilla getSeenPlayers() is obfuscated on the shipped jar, the fork adds this
                // name
                bound[4] = type.getMethod("getStoredPlayers");
                bound[5] = type.getMethod("upgradePlayerData", tagType);
                bound[6] = type.getMethod("getDatabase");
                bound[7] = type.getMethod("isClosed");
                bound[8] = type.getMethod("getColumnFamily", String.class);
                bound[9] = type.getMethod("createColumnFamily", String.class);
                bound[10] = type.getMethod("dropColumnFamily", String.class);

            }

        } catch (Throwable t) {

            reason = "Failed to bind the TrueOG Purpur player data API";
            logger.log(Level.WARNING, reason, t);

        }

        final boolean ok = (reason == null);
        this.storage = ok ? boundStorage : null;
        this.getMethod = ok ? bound[0] : null;
        this.saveMethod = ok ? bound[1] : null;
        this.stageMethod = ok ? bound[2] : null;
        this.hasMethod = ok ? bound[3] : null;
        this.seenMethod = ok ? bound[4] : null;
        this.upgradeMethod = ok ? bound[5] : null;
        this.databaseMethod = ok ? bound[6] : null;
        this.isClosedMethod = ok ? bound[7] : null;
        this.getColumnFamilyMethod = ok ? bound[8] : null;
        this.createColumnFamilyMethod = ok ? bound[9] : null;
        this.dropColumnFamilyMethod = ok ? bound[10] : null;
        this.unavailableReason = reason;
        if (reason != null) {

            logger.warning(reason + ", player data APIs are unavailable");

        }

    }

    // Field names are obfuscated, so find the field whose type declares
    // savePlayerData(UUID, tag).
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

            if (method.getName().equals("savePlayerData") && method.getParameterCount() == 2
                    && method.getParameterTypes()[0] == UUID.class && method.getReturnType() == boolean.class)
            {

                return method;

            }

        }

        return null;

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

    // Vanilla profile, null when absent or the storage is closed.
    public Object get(UUID uuid) {

        requireAvailable();
        return invoke(getMethod, uuid);

    }

    public boolean save(UUID uuid, Object tag) {

        requireAvailable();
        return Boolean.TRUE.equals(invoke(saveMethod, uuid, tag));

    }

    // Adds the vanilla profile write to a batch the caller writes. False when the
    // storage refused it.
    public boolean stage(WriteBatch batch, UUID uuid, Object tag) {

        requireAvailable();
        try {

            invoke(stageMethod, batch, uuid, tag);
            return true;

        } catch (RuntimeException e) {

            Internal.getPlugin().getLogger().log(Level.WARNING, "Failed to stage the player data of " + uuid, e);
            return false;

        }

    }

    public boolean has(UUID uuid) {

        requireAvailable();
        return Boolean.TRUE.equals(invoke(hasMethod, uuid));

    }

    @SuppressWarnings("unchecked")
    public List<UUID> seen() {

        requireAvailable();
        final Object result = invoke(seenMethod);
        return (result == null) ? Collections.<UUID>emptyList() : (List<UUID>) result;

    }

    public Object upgrade(Object tag) {

        requireAvailable();
        return invoke(upgradeMethod, tag);

    }

    public boolean isOpen() {

        return storage != null && !Boolean.TRUE.equals(invoke(isClosedMethod));

    }

    // Null once the storage is closed.
    public RocksDB database() {

        requireAvailable();
        return isOpen() ? (RocksDB) invoke(databaseMethod) : null;

    }

    public ColumnFamilyHandle columnFamily(String name, boolean create) {

        requireAvailable();
        return (ColumnFamilyHandle) invoke(create ? createColumnFamilyMethod : getColumnFamilyMethod, name);

    }

    public boolean dropColumnFamily(String name) {

        requireAvailable();
        return Boolean.TRUE.equals(invoke(dropColumnFamilyMethod, name));

    }

    private Object invoke(Method method, Object... args) {

        try {

            return method.invoke(storage, args);

        } catch (InvocationTargetException e) {

            // The fork throws IllegalStateException when the storage failed, callers must
            // see that
            if (e.getCause() instanceof RuntimeException) {

                throw (RuntimeException) e.getCause();

            }

            throw new RuntimeException("Player data API call " + method.getName() + " failed", e.getCause());

        } catch (IllegalAccessException e) {

            throw new RuntimeException(e);

        }

    }

}
