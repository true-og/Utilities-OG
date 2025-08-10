// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle.
package net.trueog.utilitiesog;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.jasync.sql.db.Connection;

// FAST async Postgres and cache based re-implementation of Bukkit's OfflinePlayer.
// Designed for 1.19.4, OfflinePlayer behavior may be different on other versions.
// TODO: Add in-game integration testing.
public class FastOfflinePlayer implements OfflinePlayer {

    // Container for player UUID (always available).
    private final UUID uuid;
    // Container for player username (requires lookup).
    private String cachedName;
    // Container for safe concurrent updates to async futures.
    private final AtomicReference<CompletableFuture<String>> lookup = new AtomicReference<>();
    // Fake Postgres connection without using UtilitiesOG.java.
    private static volatile Supplier<Connection> connectionSupplier;

    // Test-only supplier hook.
    static void overrideConnectionSupplierForTests(Supplier<Connection> supplier) {

        // Enable the test supplier.
        connectionSupplier = supplier;

    }

    // Supply a connection to jasync Postgres.
    private Connection connection() {

        // If a test supplier is available, do this...
        final Supplier<Connection> s = connectionSupplier;
        if (s != null) {

            // Return the test supplier.
            return s.get();

        }

        // Return the real provider.
        return UtilitiesOG.getPostgres();

    }

    // Runs after each individual unit test.
    static void resetConnectionSupplierForTests() {

        // Reset the jasync Postgres connection supplier.
        connectionSupplier = null;

    }

    // Class constructor.
    public FastOfflinePlayer(UUID inputtedUUID) {

        // Cache the provided UUID.
        this.uuid = inputtedUUID;

    }

    // Internal constructor for deserialization.
    private FastOfflinePlayer(@NotNull UUID uuid, @Nullable String username) {

        // Pass the provided UUID.
        this.uuid = uuid;
        // Pass the username (will be set to 'usernameLookupError' if not found).
        this.cachedName = username;

    }

    @Override
    public boolean isOp() {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public void setOp(boolean value) {

        // TODO Auto-generated method stub.

    }

    // Fast re-implementation of OfflinePlayer.serialize() from Bukkit API.
    @Override
    public @NotNull Map<String, Object> serialize() {

        // Construct a map to hold player data.
        final Map<String, Object> map = new LinkedHashMap<>();

        // Add the player data to the map.
        map.put("name", getName());

        // Return the combined serialized object.
        return map;

    }

    // Fast re-implementation of OfflinePlayer.deserialize() from Bukkit API.
    public static @NotNull FastOfflinePlayer deserialize(@NotNull UUID uuid, @NotNull Map<String, Object> values) {

        // Extract the player name from the serialized map as a String.
        final String name = (String) values.get("name");

        // Return the UUID from cache.
        return new FastOfflinePlayer(uuid, name);

    }

    @Override
    public boolean isOnline() {

        // TODO Auto-generated method stub.
        return false;

    }

    // Fast re-implementation of OfflinePlayer.getName() from Bukkit API.
    @Override
    public @Nullable String getName() {

        // If the name is already in the cache, do this...
        if (cachedName != null) {

            // Return the name from cache instead of querying the database.
            return cachedName;

        }

        // Get existing lookup future if present.
        CompletableFuture<String> future = lookup.get();

        // If there is no existing lookup future, do this...
        if (future == null) {

            // Create new jasync Postgres query for username.
            CompletableFuture<String> created = connection()
                    .sendPreparedStatement("SELECT username FROM offline_players WHERE uuid = $1 LIMIT 1;",
                            Collections.singletonList(uuid))
                    // On success, cache and return username.
                    .thenApply(res ->
                    {

                        // If the query returned rows, do this...
                        if (!res.getRows().isEmpty()) {

                            // Extract the "username" value from the first row.
                            final String name = res.getRows().get(0).getString("username");

                            // Store the retrieved username in the cache.
                            cachedName = name;

                            // Return the retrieved username.
                            return name;

                        }

                        // If no rows are found, return null.
                        return null;

                    })

                    // If there was an error with finding the name, do this...
                    .exceptionally(ex ->
                    {

                        // Set a placeholder name to avoid a crash.
                        cachedName = "usernameLookupError";

                        UtilitiesOG.logToConsole(InternalFunctions.getPrefix(),
                                "ERROR: FastOfflinePlayer getName() failed!" + ex.getMessage());

                        // Return the placeholder name.
                        return cachedName;

                    });

            // Attempt to set the lookup future only if it is currently null.
            if (!lookup.compareAndSet(null, created)) {

                // If another thread already set the future, use the existing one instead.
                created = lookup.get();

            }

            // Store created future.
            future = created;

        }

        // Return cached name (may still be null until async query completes).
        return cachedName;

    }

    // Non-cached version of getName() for testing
    public @NotNull CompletableFuture<@Nullable String> getNameAsync() {

        // If cached name exists, return it as completed future
        if (cachedName != null) {

            return CompletableFuture.completedFuture(cachedName);

        }

        // Ensure lookup starts by calling getName()
        getName();

        // Return lookup future
        return lookup.get();

    }

    // Fast re-implementation OfflinePlayer.getUniqueId() from Bukkit API.
    @Override
    public @NotNull UUID getUniqueId() {

        // Pass on UUID from constructor.
        return uuid;

    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {

        // TODO Auto-generated method stub.
        return null;

    }

    @Override
    public boolean isBanned() {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public boolean isWhitelisted() {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public void setWhitelisted(boolean value) {
        // TODO Auto-generated method stub.

    }

    @Override
    public @Nullable Player getPlayer() {

        // TODO Auto-generated method stub.
        return null;

    }

    @Override
    public long getFirstPlayed() {

        // TODO Auto-generated method stub.
        return 0;

    }

    /**
     * @deprecated The getLastPlayed API contract is ambiguous and the
     *             implementation may or may not return the correct value. Use
     *             {@link #getLastSeen()} instead.
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    @Override
    public long getLastPlayed() {

        // TODO Auto-generated method stub.
        return getLastSeen();

    }

    @Override
    public boolean hasPlayedBefore() {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public @Nullable Location getBedSpawnLocation() {

        // TODO Auto-generated method stub.
        return null;

    }

    @Override
    public long getLastLogin() {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public long getLastSeen() {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int newValue) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int newValue)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType)
            throws IllegalArgumentException
    {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub.

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) {
        // TODO Auto-generated method stub.

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int newValue) {
        // TODO Auto-generated method stub.

    }

    @Override
    public @Nullable Location getLastDeathLocation() {

        // TODO Auto-generated method stub.
        return null;

    }

    @Override
    public boolean getAllowFlight() {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public void setAllowFlight(boolean flight) {

        // TODO Auto-generated method stub.

    }

    @Override
    public boolean isFlying() {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public void setFlying(boolean value) {
        // TODO Auto-generated method stub.

    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        // TODO Auto-generated method stub.

    }

    @Override
    public float getFlySpeed() {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public float getWalkSpeed() {

        // TODO Auto-generated method stub.
        return 0;

    }

    @Override
    public @Nullable Location getLocation() {

        // TODO Auto-generated method stub.
        return null;

    }

    @Override
    public boolean teleportOffline(@NotNull Location destination) {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public boolean teleportOffline(@NotNull Location destination, @NotNull TeleportCause cause) {

        // TODO Auto-generated method stub.
        return false;

    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportOfflineAsync(@NotNull Location destination) {

        // TODO Auto-generated method stub.
        return null;

    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportOfflineAsync(@NotNull Location destination,
            @NotNull TeleportCause cause)
    {

        // TODO Auto-generated method stub.
        return null;

    }

}