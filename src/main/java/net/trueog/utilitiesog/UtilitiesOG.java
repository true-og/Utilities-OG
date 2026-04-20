// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jasync.sql.db.pool.ConnectionPool;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection;
import com.github.jasync.sql.db.postgresql.PostgreSQLConnectionBuilder;

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
import net.trueog.utilitiesog.listeners.AdvancementsOnlyInSurvivalListener;
import net.trueog.utilitiesog.listeners.DisableEntityCrammingListener;
import net.trueog.utilitiesog.listeners.NoFlippyListener;
import net.trueog.utilitiesog.listeners.PhantomState;
import net.trueog.utilitiesog.listeners.TogglePhantomsListener;
import net.trueog.utilitiesog.misc.FlagRegistrationException;
import net.trueog.utilitiesog.modules.ChainArmorModule;
import net.trueog.utilitiesog.modules.MockBambooModule;
import net.trueog.utilitiesog.utils.MessageFormat;
import net.trueog.utilitiesog.utils.PlaceholderUtils;
import net.trueog.utilitiesog.utils.TextUtils;

// Declare main plugin class.
public final class UtilitiesOG extends JavaPlugin {

    // Formatted plugin prefix used on every console log line emitted from
    // Utilities-OG API. Reached internally via Internal.getPrefix(),
    private static final String PREFIX = "&7[&6Utilities&f-&4OG&7] ";

    // Declare live plugin instance to be initialized in onEnable().
    private static UtilitiesOG instance;

    // Declare live PostgreSQL connection pool to be initialized in onEnable().
    private static ConnectionPool<PostgreSQLConnection> POOL;

    // Package-private hooks consumed by net.trueog.utilitiesog.Internal.
    static UtilitiesOG getPluginInstance() {

        return instance;

    }

    static String getPluginPrefix() {

        return PREFIX;

    }

    static ConnectionPool<PostgreSQLConnection> getPluginPostgres() {

        return POOL;

    }

    @Override
    public void onEnable() {

        instance = this;

        final File configFile = new File(this.getDataFolder(), "config.yml");
        if (!configFile.exists()) {

            this.saveDefaultConfig();

        }

        POOL = initPsql();

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

    // Initialize postgres connection;
    private ConnectionPool<PostgreSQLConnection> initPsql() {

        // Reads the values from config.yml;
        final String baseUrl = getConfig().getString("postgresUrl"); // e.g. jdbc:postgresql://localhost:5432/diamond
        final String user = getConfig().getString("postgresUser"); // e.g. postgres
        final String password = getConfig().getString("postgresPassword"); // e.g. postgresPassword

        // Appends credentials to the URL (only if they are not already present)
        final String jdbcUrl = "%s?user=%s&password=%s".formatted(baseUrl, user, password);

        // Create postgres connection.
        return PostgreSQLConnectionBuilder.createConnectionPool(jdbcUrl);

    }

}