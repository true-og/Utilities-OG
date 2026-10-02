// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import net.trueog.utilitiesog.utils.InventoryNbtCodec;
import net.trueog.utilitiesog.utils.PlayerDataUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.PrefixNode;
import net.trueog.utilitiesog.commands.AboutCommand;
import net.trueog.utilitiesog.commands.BingCommand;
import net.trueog.utilitiesog.commands.ColorCodesCommand;
import net.trueog.utilitiesog.commands.PingCommand;
import net.trueog.utilitiesog.commands.RanksCommand;
import net.trueog.utilitiesog.commands.ToggleCrammingCommand;
import net.trueog.utilitiesog.commands.TogglePhantomsCommand;
import net.trueog.utilitiesog.commands.missingcommands.AuctionCommand;
import net.trueog.utilitiesog.commands.missingcommands.FCommand;
import net.trueog.utilitiesog.commands.missingcommands.FactionCommand;
import net.trueog.utilitiesog.commands.missingcommands.GuildCommand;
import net.trueog.utilitiesog.commands.missingcommands.InfoCommand;
import net.trueog.utilitiesog.commands.missingcommands.KitCommand;
import net.trueog.utilitiesog.commands.missingcommands.RTPCommand;
import net.trueog.utilitiesog.commands.missingcommands.SeedCommand;
import net.trueog.utilitiesog.commands.missingcommands.WildCommand;
import net.trueog.utilitiesog.listeners.AdvancementsOnlyInSurvivalListener;
import net.trueog.utilitiesog.listeners.DisableEntityCrammingListener;
import net.trueog.utilitiesog.listeners.NoFlippyListener;
import net.trueog.utilitiesog.listeners.PhantomState;
import net.trueog.utilitiesog.listeners.TogglePhantomsListener;
import net.trueog.utilitiesog.misc.FlagRegistrationException;
import net.trueog.utilitiesog.modules.ChainArmorModule;
import net.trueog.utilitiesog.modules.MockBambooModule;
import net.trueog.utilitiesog.modules.RandomDropsModule;
import net.trueog.utilitiesog.utils.MessageFormat;
import net.trueog.utilitiesog.utils.PlaceholderUtils;
import net.trueog.utilitiesog.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Declare main plugin class.
public final class UtilitiesOG extends JavaPlugin {

    // Formatted plugin prefix used on every console log line emitted from
    // Utilities-OG API. Reached internally via Internal.getPrefix(),
    private static final String PREFIX = "&7[&6Utilities&f-&4OG&7] ";

    // Declare live plugin instance to be initialized in onEnable().
    private static UtilitiesOG instance;

    private static PlayerDataUtils playerDataUtils;

    // Package-private hooks consumed by net.trueog.utilitiesog.Internal.
    static UtilitiesOG getPluginInstance() {

        return instance;

    }

    static String getPluginPrefix() {

        return PREFIX;

    }

    @Override
    public void onEnable() {

        instance = this;

        playerDataUtils = new PlayerDataUtils();

        final File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {

            this.saveDefaultConfig();

        }

        if (this.getConfig().getBoolean("ChainArmor")) {

            ChainArmorModule.Enable();

        }

        if (this.getConfig().getBoolean("ColorCodes")) {

            this.getCommand("colorcodes").setExecutor(new ColorCodesCommand());

        }

        if (this.getConfig().getBoolean("DisableEntityCramming")) {

            getServer().getPluginManager().registerEvents(new DisableEntityCrammingListener(), this);

            this.getCommand("togglecramming").setExecutor(new ToggleCrammingCommand());

        }

        // Built-in MiniPlaceholders; disable in config.yml to let another plugin own
        // these names instead
        if (this.getConfig().getBoolean("RegisterPlaceholders", true)) {

            // Registering a global MiniPlaceholder.
            registerGlobalPlaceholder("servers_name", () -> "&aTrue&cOG &eNetwork");

            // Registering an Audience MiniPlaceholder.
            registerAudiencePlaceholder("player_display_name", (Player player) -> {

                final LuckPerms luckPerms = LuckPermsProvider.get();
                final User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {

                    getLogger().info("ERROR: MiniPlaceholder processing error. Player: " + player.getName()
                            + " has no User object in LuckPerms!");

                    return player.getName();

                }

                final String primaryGroup = user.getPrimaryGroup();
                final Group group = luckPerms.getGroupManager().getGroup(primaryGroup);
                if (group == null) {

                    getLogger().info("ERROR: MiniPlaceholder processing error. User: " + player.getName()
                            + " has no Group assignment in LuckPerms!");

                    return player.getName();

                }

                final String prefix = group.getNodes().stream().filter(node -> node instanceof PrefixNode).findFirst()
                        .map(node -> ((PrefixNode) node).getMetaValue()).orElse(null);

                if (prefix == null) {

                    return player.getName();

                }

                final String colorCode = prefix.replaceAll(".*\\](.*)", "$1");

                return "<luckperms_prefix>" + colorCode + " " + player.getName();

            });

        }

        if (this.getConfig().getBoolean("MockBamboo")) {

            MockBambooModule.Enable();

        }

        if (this.getConfig().getBoolean("NoFlippy")) {

            if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {

                getServer().getPluginManager().registerEvents(new NoFlippyListener(), this);

            } else {

                this.getLogger().severe("WorldGuard is not installed! Disabling NoFlippy...");

            }

        }

        RandomDropsModule.enable(this);

        if (this.getConfig().getBoolean("Ping")) {

            this.getCommand("ping").setExecutor(new PingCommand());
            this.getCommand("bing").setExecutor(new BingCommand());

        }

        if (this.getConfig().getBoolean("RanksMenu")) {

            this.getCommand("ranks").setExecutor(new RanksCommand());

        }

        if (this.getConfig().getBoolean("TogglePhantoms")) {

            PhantomState.load(this);

            getServer().getPluginManager().registerEvents(new TogglePhantomsListener(), this);

            this.getCommand("togglephantoms").setExecutor(new TogglePhantomsCommand());

        }

        if (this.getConfig().getBoolean("AdvancementsOnlyInSurvival")) {

            getServer().getPluginManager().registerEvents(new AdvancementsOnlyInSurvivalListener(), this);

        }

        if (this.getConfig().getBoolean("MissingCommands")) {

            this.getCommand("wild").setExecutor(new WildCommand());
            this.getCommand("rtp").setExecutor(new RTPCommand());
            this.getCommand("seed").setExecutor(new SeedCommand());
            this.getCommand("f").setExecutor(new FCommand());
            this.getCommand("faction").setExecutor(new FactionCommand());
            this.getCommand("guild").setExecutor(new GuildCommand());
            this.getCommand("kit").setExecutor(new KitCommand());
            this.getCommand("ah").setExecutor(new AuctionCommand());
            this.getCommand("auction").setExecutor(new AuctionCommand());
            this.getCommand("info").setExecutor(new InfoCommand());

        }

        this.getCommand("utilities").setExecutor(new AboutCommand());

    }

    @Override
    public void onLoad() {

        try {

            NoFlippyListener.registerFlag();

        } catch (FlagRegistrationException error) {

            this.getLogger()
                    .severe("ERROR: Failed to register the can-flippy Flag with WorldGuard. " + error.getMessage());

        }

    }

    @Override
    public void onDisable() {

        PlaceholderUtils.unregisterAll();

    }

    // Sends a fully formatted message (every supported tag active).
    public static void trueogMessage(Player player, String message) {

        TextUtils.trueogMessage(player, message);

    }

    // Sends a selectively formatted message. Disabled tags stay as literal
    // MiniMessage
    // markup in the rendered output.
    public static void trueogMessage(Player player, String message, MessageFormat format) {

        TextUtils.trueogMessage(player, message, format);

    }

    // Sends a fully formatted message (every supported tag active) without
    // expanding MiniPlaceholders.
    public static void trueogRawMessage(Player player, String message) {

        TextUtils.trueogRawMessage(player, message);

    }

    // Sends a selectively formatted message without expanding MiniPlaceholders.
    public static void trueogRawMessage(Player player, String message, MessageFormat format) {

        TextUtils.trueogRawMessage(player, message, format);

    }

    // Sends a pre-built Component to a player on the caller's thread.
    public static void trueogMessage(Player player, Component message) {

        TextUtils.trueogMessage(player, message);

    }

    // Sends a pre-built Component after selectively formatting.
    // Rainbow and gradient are indistinguishable from color at the
    // Component level, so those flags have no effect here beyond what color() does.
    public static void trueogMessage(Player player, Component message, MessageFormat format) {

        TextUtils.trueogMessage(player, message, format);

    }

    // Handle messages to players based on UUID. Useful when the caller doesn't have
    // a Player object on hand.
    public static void trueogMessage(UUID playerUUID, String message) {

        trueogMessage(playerUUID, message, MessageFormat.full());

    }

    // Format-aware UUID based message send.
    public static void trueogMessage(UUID playerUUID, String message, MessageFormat format) {

        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {

            TextUtils.trueogMessage(player, message, format);

        } else {

            logToConsole("[Utilities-OG]", "Player with UUID " + playerUUID + " is not online.");

        }

    }

    // Handle non-expanded messages to offline players (based on UUID).
    public static void trueogRawMessage(UUID playerUUID, String message) {

        trueogRawMessage(playerUUID, message, MessageFormat.full());

    }

    // Format-aware UUID raw send.
    public static void trueogRawMessage(UUID playerUUID, String message, MessageFormat format) {

        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {

            TextUtils.trueogRawMessage(player, message, format);

        } else {

            logToConsole("[Utilities-OG]", "Player with UUID " + playerUUID + " is not online.");

        }

    }

    // Formats a message without MiniPlaceholder expansion, every tag active.
    public static TextComponent trueogColorize(String message) {

        return trueogColorize(message, MessageFormat.full());

    }

    // Format-aware colorize. Disabled tags stay literal in the output.
    public static TextComponent trueogColorize(String message, MessageFormat format) {

        return (TextComponent) TextUtils.miniMessage(format).deserialize(TextUtils.processColorCodes(message));

    }

    // Expands Global MiniPlaceholders with every tag active.
    public static TextComponent trueogExpand(String message) {

        return TextUtils.expandTextWithPlaceholders(message);

    }

    // Format-aware global expansion.
    public static TextComponent trueogExpand(String message, MessageFormat format) {

        return TextUtils.expandTextWithPlaceholders(message, format);

    }

    // Expands Audience MiniPlaceholders with every tag active.
    public static TextComponent trueogExpand(String message, Player player) {

        return TextUtils.expandTextWithPlaceholders(message, player);

    }

    // Format-aware audience expansion.
    public static TextComponent trueogExpand(String message, Player player, MessageFormat format) {

        return TextUtils.expandTextWithPlaceholders(message, player, format);

    }

    // Expands Relational MiniPlaceholders with every tag active.
    public static TextComponent trueogExpand(String message, Player player, Player target) {

        return TextUtils.expandTextWithPlaceholders(message, player, target);

    }

    // Format-aware relational expansion.
    public static TextComponent trueogExpand(String message, Player player, Player target, MessageFormat format) {

        return TextUtils.expandTextWithPlaceholders(message, player, target, format);

    }

    // Variation of MiniPlaceholder expansion for when Bukkit API is unavailable.
    public static TextComponent trueogExpand(String message, UUID playerUUID) {

        return trueogExpand(message, playerUUID, MessageFormat.full());

    }

    // Format-aware UUID expansion. Falls back to global context when the
    // player is offline.
    public static TextComponent trueogExpand(String message, UUID playerUUID, MessageFormat format) {

        final Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {

            return TextUtils.expandTextWithPlaceholders(message, player, format);

        } else {

            logToConsole("[Utilities-OG]", "Player with UUID " + playerUUID + " is not online.");

            return TextUtils.expandTextWithPlaceholders(message, format);

        }

    }

    // Global placeholder without arguments.
    public static void registerGlobalPlaceholder(String name, Supplier<String> valueSupplier) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name,
                placeholder -> placeholder.setGlobalPlaceholder(valueSupplier));

    }

    // Global placeholder with arguments.
    public static void registerGlobalPlaceholder(String name, Function<List<String>, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name,
                placeholder -> placeholder.setGlobalPlaceholder(valueFunction));

    }

    // Audience placeholder without arguments.
    public static void registerAudiencePlaceholder(String name, Function<Player, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name,
                placeholder -> placeholder.setAudiencePlaceholder(valueFunction));

    }

    // Audience placeholder with arguments.
    public static void registerAudiencePlaceholder(String name,
            BiFunction<Player, List<String>, String> valueFunction)
    {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name,
                placeholder -> placeholder.setAudiencePlaceholder(valueFunction));

    }

    // Relational placeholder without arguments.
    public static void registerRelationalPlaceholder(String name, BiFunction<Player, Player, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name,
                placeholder -> placeholder.setRelationalPlaceholder(valueFunction));

    }

    // Relational placeholder with arguments.
    public static void registerRelationalPlaceholder(String name,
            Internal.TriFunction<Player, Player, List<String>, String> valueFunction)
    {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name,
                placeholder -> placeholder.setRelationalPlaceholder(valueFunction));

    }

    // Strips every legacy Bukkit code and every supported MiniMessage tag,
    // returning plain text.
    public static String stripFormatting(String content) {

        return TextUtils.stripFormatting(content);

    }

    // Flattens a Component to plain text, discarding every style and tag.
    public static String stripFormatting(Component component) {

        return TextUtils.stripFormatting(component);

    }

    // Console logging API that strips every supported tag and legacy code.
    public static void logToConsole(String prefix, String message) {

        Bukkit.getLogger().info(TextUtils.stripFormatting(prefix + " " + message));

    }

    // TrueOG Purpur RocksDB player data, tags are raw NMS compound objects.

    public static boolean isPlayerDataApiAvailable() {

        return playerDataUtils != null && playerDataUtils.isAvailable();

    }

    // Profile on a world, null when absent. Null world is the default storage.
    public static @Nullable Object getPlayerData(@Nullable String world, UUID uuid) {

        return playerDataUtils.get(world, uuid);

    }

    // False when the server refused the write.
    public static boolean savePlayerData(@Nullable String world, UUID uuid, Object tag) {

        return playerDataUtils.save(world, uuid, tag);

    }

    // Atomic write across worlds. Use a HashMap, a null key is the default.
    public static boolean savePlayerData(UUID uuid, Map<String, Object> perWorld) {

        return playerDataUtils.saveAll(uuid, perWorld);

    }

    public static boolean hasPlayerData(@Nullable String world, UUID uuid) {

        return playerDataUtils.has(world, uuid);

    }

    // Every player with a profile on a world. Empty for an unknown world.
    public static List<UUID> getSeenPlayers(@Nullable String world) {

        return playerDataUtils.seen(world);

    }

    // Deletes a world's own storage. The default storage can not be dropped.
    public static boolean dropWorldPlayerData(String world) {

        return playerDataUtils.drop(world);

    }

    // Copies one storage into another. Count, 0 when nothing, -1 on failure.
    public static int copyWorldPlayerData(@Nullable String from, String to) {

        return playerDataUtils.copy(from, to);

    }

    // Forces buffered player data writes to durable storage. False when unsupported
    // or it failed.
    public static boolean flushPlayerData() {

        return playerDataUtils != null && playerDataUtils.flush();

    }

    // Per player locks so an offline edit here and a MyWorlds save never interleave
    // their read-modify-writes.
    private static final ConcurrentHashMap<UUID, ReentrantLock> playerDataLocks = new ConcurrentHashMap<>();

    // Hold this around any read-modify-write of one player's stored data, released
    // in a finally.
    public static void lockPlayerData(UUID uuid) {

        playerDataLocks.computeIfAbsent(uuid, u -> new ReentrantLock()).lock();

    }

    public static void unlockPlayerData(UUID uuid) {

        final ReentrantLock lock = playerDataLocks.get(uuid);
        if (lock != null && lock.isHeldByCurrentThread()) {

            lock.unlock();

        }

    }

    // Offline inventory helpers on the default storage. Need Item-NBT-API.

    public static boolean isPlayerInventoryApiAvailable() {

        return playerDataUtils != null && playerDataUtils.isItemConversionAvailable();

    }

    private static void requireInventoryApi() {

        if (!isPlayerInventoryApiAvailable()) {

            throw new UnsupportedOperationException("Offline inventory helpers need TrueOG Purpur and Item-NBT-API");

        }

    }

    // 41 slots in getContents() order. All null without a stored profile.
    public static @Nullable ItemStack @NotNull [] getInventoryData(UUID uuid) {

        requireInventoryApi();
        final Object tag = playerDataUtils.get(null, uuid);
        return (tag == null) ? new ItemStack[41] : InventoryNbtCodec.read(tag);

    }

    // Replaces the stored inventory. False if no profile or the write failed.
    public static boolean setInventoryData(UUID uuid, @Nullable ItemStack @NotNull [] items) {

        requireInventoryApi();
        // Lock the whole read-modify-write so a concurrent save cannot land between get
        // and save
        lockPlayerData(uuid);
        try {

            final Object tag = playerDataUtils.get(null, uuid);
            if (tag == null) {

                logToConsole(PREFIX, "&cNo stored player data for " + uuid + ", inventory not written.");
                return false;

            }

            final boolean saved = playerDataUtils.save(null, uuid, InventoryNbtCodec.write(tag, items));
            if (!saved) {

                logToConsole(PREFIX, "&cThe server refused the player data write for " + uuid + ".");

            }

            return saved;

        } finally {

            unlockPlayerData(uuid);

        }

    }

    // Held hotbar slot of the stored profile, 0 without a stored profile.
    public static int getHeldItemSlot(UUID uuid) {

        requireInventoryApi();
        final Object tag = playerDataUtils.get(null, uuid);
        return (tag == null) ? 0 : InventoryNbtCodec.heldItemSlot(tag);

    }

}
