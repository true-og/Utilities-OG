package net.trueog.utilitiesog;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

public class FastOfflinePlayer implements OfflinePlayer {

    UUID uuid;

    public FastOfflinePlayer(UUID inputtedUUID) {

        uuid = inputtedUUID;

    }

    @Override
    public boolean isOp() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public void setOp(boolean value) {

        // TODO Auto-generated method stub

    }

    @Override
    public @NotNull Map<String, Object> serialize() {

        // TODO Auto-generated method stub
        UtilitiesOG.config().set("players." + getUniqueId(), this);
        UtilitiesOG.getPlugin().saveConfig();
        return null;

    }

    @Override
    public boolean isOnline() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public @Nullable String getName() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public @NotNull UUID getUniqueId() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public @NotNull PlayerProfile getPlayerProfile() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public boolean isBanned() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public boolean isWhitelisted() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public void setWhitelisted(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    public @Nullable Player getPlayer() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public long getFirstPlayed() {

        // TODO Auto-generated method stub
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

        // TODO Auto-generated method stub
        return getLastSeen();

    }

    @Override
    public boolean hasPlayedBefore() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public @Nullable Location getBedSpawnLocation() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public long getLastLogin() {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public long getLastSeen() {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, int amount) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, int newValue) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getStatistic(@NotNull Statistic statistic) throws IllegalArgumentException {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull Material material) throws IllegalArgumentException {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull Material material, int amount)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull Material material, int newValue)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public int getStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType)
            throws IllegalArgumentException
    {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public void incrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount)
            throws IllegalArgumentException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void decrementStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int amount) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setStatistic(@NotNull Statistic statistic, @NotNull EntityType entityType, int newValue) {
        // TODO Auto-generated method stub

    }

    @Override
    public @Nullable Location getLastDeathLocation() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public boolean getAllowFlight() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public void setAllowFlight(boolean flight) {

        // TODO Auto-generated method stub

    }

    @Override
    public boolean isFlying() {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public void setFlying(boolean value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFlySpeed(float value) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setWalkSpeed(float value) throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public float getFlySpeed() {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public float getWalkSpeed() {

        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public @Nullable Location getLocation() {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public boolean teleportOffline(@NotNull Location destination) {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public boolean teleportOffline(@NotNull Location destination, @NotNull TeleportCause cause) {

        // TODO Auto-generated method stub
        return false;

    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportOfflineAsync(@NotNull Location destination) {

        // TODO Auto-generated method stub
        return null;

    }

    @Override
    public @NotNull CompletableFuture<Boolean> teleportOfflineAsync(@NotNull Location destination,
            @NotNull TeleportCause cause)
    {

        // TODO Auto-generated method stub
        return null;

    }

}
