// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle, SKBotNL.
package net.trueog.utilitiesog.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import net.trueog.utilitiesog.Internal;

// Reflection bridge to the fork's PlayerDataApi. Tags are raw NMS objects.
public final class PlayerDataUtils {

    private final Object api;
    private final Method getMethod;
    private final Method saveAllMethod;
    private final Method hasMethod;
    private final Method seenMethod;
    private final Method dropMethod;
    private final Method copyMethod;
    private final Method flushMethod;
    private final String unavailableReason;
    private final String levelName;

    public PlayerDataUtils() {

        final Logger logger = Internal.getPlugin().getLogger();
        Object boundApi = null;
        Method[] bound = new Method[6];
        Method flushLocal = null;
        String reason = null;
        try {

            final Object server = Bukkit.getServer();
            final Object playerList = server.getClass().getMethod("getHandle").invoke(server);
            final Object storage = findStorage(playerList);
            if (storage == null) {

                reason = "Not running TrueOG Purpur jar";

            } else {

                boundApi = findApiMethod(storage.getClass()).invoke(storage);
                final Class<?> apiType = boundApi.getClass();
                bound[0] = apiType.getMethod("get", String.class, UUID.class);
                bound[1] = apiType.getMethod("saveAll", UUID.class, Map.class);
                bound[2] = apiType.getMethod("has", String.class, UUID.class);
                bound[3] = apiType.getMethod("seen", String.class);
                bound[4] = apiType.getMethod("drop", String.class);
                bound[5] = apiType.getMethod("copy", String.class, String.class);
                // Optional: older forks lack flush(), so its absence must not disable the whole
                // API
                try {

                    flushLocal = apiType.getMethod("flush");

                } catch (NoSuchMethodException ignored) {

                }

            }

        } catch (Throwable t) {

            reason = "Failed to bind the TrueOG Purpur player data API";
            logger.log(Level.WARNING, reason, t);

        }

        final boolean ok = (reason == null);
        this.api = ok ? boundApi : null;
        this.getMethod = ok ? bound[0] : null;
        this.saveAllMethod = ok ? bound[1] : null;
        this.hasMethod = ok ? bound[2] : null;
        this.seenMethod = ok ? bound[3] : null;
        this.dropMethod = ok ? bound[4] : null;
        this.copyMethod = ok ? bound[5] : null;
        this.flushMethod = ok ? flushLocal : null;
        this.unavailableReason = reason;
        this.levelName = Bukkit.getWorlds().get(0).getName();
        if (reason != null) {

            logger.warning(reason + ", player data APIs are unavailable");

        }

    }

    // Field names are obfuscated, so find the field whose type declares api().
    private static Object findStorage(Object playerList) throws IllegalAccessException {

        for (Class<?> type = playerList.getClass(); type != null; type = type.getSuperclass()) {

            for (Field field : type.getDeclaredFields()) {

                if (Modifier.isStatic(field.getModifiers()) || findApiMethod(field.getType()) == null) {

                    continue;

                }

                field.setAccessible(true);
                return field.get(playerList);

            }

        }

        return null;

    }

    // Resolved on the class, so a hook subclass still binds.
    private static Method findApiMethod(Class<?> type) {

        try {

            final Method method = type.getMethod("api");
            return method.getReturnType().getSimpleName().equals("PlayerDataApi") ? method : null;

        } catch (NoSuchMethodException e) {

            return null;

        }

    }

    public boolean isAvailable() {

        return api != null;

    }

    // Item-NBT-API is a hard dependency, so tags can always become Bukkit item
    // stacks when the fork API is bound.
    public boolean isItemConversionAvailable() {

        return api != null;

    }

    private void requireAvailable() {

        if (api == null) {

            throw new UnsupportedOperationException(unavailableReason + ", player data APIs are unavailable");

        }

    }

    // The main worlds never lose their storage: the level itself, its nether and
    // its end.
    private boolean isProtected(String world) {

        return world != null && (world.equalsIgnoreCase(levelName) || world.equalsIgnoreCase(levelName + "_nether")
                || world.equalsIgnoreCase(levelName + "_the_end"));

    }

    // The server's own level name maps to null here, the fork knows only null as
    // default.
    private String key(String world) {

        return (world == null || world.equalsIgnoreCase(levelName)) ? null : world;

    }

    // Null world or the server's own level name is the default storage.
    public Object get(String world, UUID uuid) {

        requireAvailable();
        return invoke(getMethod, key(world), uuid);

    }

    // A single write is a one entry batch, the fork only has saveAll.
    public boolean save(String world, UUID uuid, Object tag) {

        return saveAll(uuid, Collections.singletonMap(world, tag));

    }

    // One atomic write across worlds. A null key is the default storage.
    public boolean saveAll(UUID uuid, Map<String, Object> perWorld) {

        requireAvailable();
        final Map<String, Object> keyed = new HashMap<>(perWorld.size() * 2);
        for (Map.Entry<String, Object> entry : perWorld.entrySet()) {

            keyed.put(key(entry.getKey()), entry.getValue());

        }

        if (keyed.size() != perWorld.size()) {

            throw new IllegalArgumentException(
                    "The batch names the default storage twice, as null and as the level name");

        }

        return Boolean.TRUE.equals(invoke(saveAllMethod, uuid, keyed));

    }

    public boolean has(String world, UUID uuid) {

        requireAvailable();
        return Boolean.TRUE.equals(invoke(hasMethod, key(world), uuid));

    }

    @SuppressWarnings("unchecked")
    public List<UUID> seen(String world) {

        requireAvailable();
        final Object result = invoke(seenMethod, key(world));
        return (result == null) ? Collections.<UUID>emptyList() : (List<UUID>) result;

    }

    // The default storage can not be dropped, so the level name is refused here.
    public boolean drop(String world) {

        requireAvailable();
        if (world == null || isProtected(world)) {

            return false;

        }

        return Boolean.TRUE.equals(invoke(dropMethod, world));

    }

    // The default storage can not be a copy target, -1 when the level name is
    // given.
    public int copy(String from, String to) {

        requireAvailable();
        if (to == null || isProtected(to)) {

            return -1;

        }

        final Object result = invoke(copyMethod, key(from), to);
        return (result == null) ? -1 : ((Integer) result).intValue();

    }

    // Forces buffered writes to durable storage. False when the fork is too old to
    // support it.
    public boolean flush() {

        requireAvailable();
        return flushMethod != null && Boolean.TRUE.equals(invoke(flushMethod));

    }

    private Object invoke(Method method, Object... args) {

        try {

            return method.invoke(api, args);

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
