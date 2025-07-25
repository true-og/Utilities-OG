// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog;

import com.sk89q.worldguard.protection.flags.StateFlag;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PrefixNode;
import net.trueog.utilitiesog.commands.AboutCommand;
import net.trueog.utilitiesog.commands.BingCommand;
import net.trueog.utilitiesog.commands.ColorCodesCommand;
import net.trueog.utilitiesog.commands.PingCommand;
import net.trueog.utilitiesog.commands.RanksCommand;
import net.trueog.utilitiesog.commands.ToggleCrammingCommand;
import net.trueog.utilitiesog.commands.TogglePhantomsCommand;
import net.trueog.utilitiesog.listeners.DisableEntityCrammingListener;
import net.trueog.utilitiesog.listeners.NoAdvancementsInCreativeListener;
import net.trueog.utilitiesog.listeners.NoFlippyListener;
import net.trueog.utilitiesog.listeners.TogglePhantomsListener;
import net.trueog.utilitiesog.misc.FlagRegistrationException;
import net.trueog.utilitiesog.modules.ChainArmorModule;
import net.trueog.utilitiesog.modules.MockBambooModule;
import net.trueog.utilitiesog.utils.PlaceholderUtils;
import net.trueog.utilitiesog.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

// Declare main plugin class.
public final class UtilitiesOG extends JavaPlugin {

    private File file;
    private static UtilitiesOG plugin;
    private static FileConfiguration config;
    private static File phantomPreferencesFile;
    private static YamlConfiguration phantomPreferences;
    private static StateFlag FlippyFlag;

    @Override
    public void onEnable() {

        plugin = this;

        this.file = new File(this.getDataFolder(), "config.yml");
        if (!this.file.exists()) {

            this.saveDefaultConfig();
        }

        config = this.getConfig();
        phantomPreferencesFile = new File(this.getDataFolder(), "phantomDisabledUsers.yml");

        try {

            if (!phantomPreferencesFile.exists()) {

                phantomPreferencesFile.createNewFile();
            }

        } catch (IOException error) {

            this.getLogger().severe("ERROR: Failed to create the phantom toggle cache file! " + error.getMessage());
        }

        phantomPreferences = YamlConfiguration.loadConfiguration(phantomPreferencesFile);

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

        if (this.getConfig().getBoolean("MiniPlaceholders")) {

            // Registering a global MiniPlaceholder.
            registerGlobalPlaceholder("servers_name", () -> "&aTrue&cOG &eNetwork");

            // Registering an Audience MiniPlaceholder.
            registerAudiencePlaceholder("player_display_name", (Player player) -> {
                LuckPerms luckPerms = LuckPermsProvider.get();
                User user = luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {

                    getPlugin()
                            .getLogger()
                            .info("ERROR: MiniPlaceholder processing error. Player: " + player.getName()
                                    + " has no User object in LuckPerms!");

                    return player.getName();
                }

                String primaryGroup = user.getPrimaryGroup();
                Group group = luckPerms.getGroupManager().getGroup(primaryGroup);
                if (group == null) {

                    getPlugin()
                            .getLogger()
                            .info("ERROR: MiniPlaceholder processing error. User: " + player.getName()
                                    + " has no Group assignment in LuckPerms!");

                    return player.getName();
                }

                String prefix = null;
                for (Node node : group.getNodes()) {

                    if (node instanceof PrefixNode) {

                        prefix = ((PrefixNode) node).getMetaValue();

                        break;
                    }
                }

                if (prefix == null) {

                    return player.getName();
                }

                String colorCode = prefix.replaceAll(".*\\](.*)", "$1");

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

        if (this.getConfig().getBoolean("Ping")) {

            this.getCommand("ping").setExecutor(new PingCommand());
            this.getCommand("bing").setExecutor(new BingCommand());
        }

        if (this.getConfig().getBoolean("RanksMenu")) {

            this.getCommand("ranks").setExecutor(new RanksCommand());
        }

        if (this.getConfig().getBoolean("TogglePhantoms")) {

            getServer().getPluginManager().registerEvents(new TogglePhantomsListener(), this);

            this.getCommand("togglephantoms").setExecutor(new TogglePhantomsCommand());
        }

        if (this.getConfig().getBoolean("NoAdvancementsInCreative")) {

            getServer().getPluginManager().registerEvents(new NoAdvancementsInCreativeListener(), this);
        }

        this.getCommand("utilities").setExecutor(new AboutCommand());
    }

    @Override
    public void onLoad() {

        try {

            FlippyFlag = NoFlippyListener.registerNoFlippyWorldGuardFlag(FlippyFlag);

        } catch (FlagRegistrationException error) {

            this.getLogger()
                    .severe("ERROR: Failed to register the can-flippy Flag with WorldGuard. " + error.getMessage());
        }
    }

    @Override
    public void onDisable() {

        PlaceholderUtils.unregisterAll();
    }

    static FileConfiguration config() {

        return config;
    }

    static String prefix() {

        return "&7[&6Utilities&f-&4OG&7] ";
    }

    static StateFlag flippyFlag() {

        return FlippyFlag;
    }

    static File phantomDisabledPlayersFile() {

        return phantomPreferencesFile;
    }

    static YamlConfiguration phantomPreferences() {

        return phantomPreferences;
    }

    // Unified method for sending a message to a player with placeholders processed.
    public static void trueogMessage(Player player, String message) {

        TextUtils.trueogMessage(player, message);
    }

    // Handle messages to offline players (based on UUID).
    public static void trueogMessage(UUID playerUUID, String message) {

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {

            TextUtils.trueogMessage(player, message);

        } else {

            logToConsole("[Utilities-OG]", "Player with UUID " + playerUUID + " is not online.");
        }
    }

    // Formats a message without MiniPlaceholder expansion.
    public static TextComponent trueogColorize(String message) {

        return (TextComponent) MiniMessage.miniMessage().deserialize(TextUtils.processColorCodes(message));
    }

    // Expands Global MiniPlaceholders.
    public static TextComponent trueogExpand(String message) {

        // MiniPlaceholder expansion with legacy and modern formatting.
        return TextUtils.expandTextWithPlaceholders(message);
    }

    // Expands Audience MiniPlaceholders.
    public static TextComponent trueogExpand(String message, Player player) {

        // MiniPlaceholder expansion with legacy and modern formatting.
        return TextUtils.expandTextWithPlaceholders(message, player);
    }

    // Expands Relational MiniPlaceholders.
    public static TextComponent trueogExpand(String message, Player player, Player target) {

        // MiniPlaceholder expansion with legacy and modern formatting.
        return TextUtils.expandTextWithPlaceholders(message, player, target);
    }

    // Variation of MiniPlaceholder expansion for when Bukkit API is unavailable.
    public static TextComponent trueogExpand(String message, UUID playerUUID) {

        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {

            return TextUtils.expandTextWithPlaceholders(message, player);

        } else {

            logToConsole("[Utilities-OG]", "Player with UUID " + playerUUID + " is not online.");

            return TextUtils.expandTextWithPlaceholders(message);
        }
    }

    // Global placeholder without arguments.
    public static void registerGlobalPlaceholder(String name, Supplier<String> valueSupplier) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name, placeholder -> {
            placeholder.setGlobalPlaceholder(valueSupplier);
        });
    }

    // Global placeholder with arguments.
    public static void registerGlobalPlaceholder(String name, Function<List<String>, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name, placeholder -> {
            placeholder.setGlobalPlaceholder(valueFunction);
        });
    }

    // Audience placeholder without arguments.
    public static void registerAudiencePlaceholder(String name, Function<Player, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name, placeholder -> {
            placeholder.setAudiencePlaceholder(valueFunction);
        });
    }

    // Audience placeholder with arguments.
    public static void registerAudiencePlaceholder(
            String name, BiFunction<Player, List<String>, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name, placeholder -> {
            placeholder.setAudiencePlaceholder(valueFunction);
        });
    }

    // Relational placeholder without arguments.
    public static void registerRelationalPlaceholder(String name, BiFunction<Player, Player, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name, placeholder -> {
            placeholder.setRelationalPlaceholder(valueFunction);
        });
    }

    // Relational placeholder with arguments.
    public static void registerRelationalPlaceholder(
            String name, TriFunction<Player, Player, List<String>, String> valueFunction) {

        PlaceholderUtils.trueogRegisterMiniPlaceholder(name, placeholder -> {
            placeholder.setRelationalPlaceholder(valueFunction);
        });
    }

    // API to strip legacy and modern formatting in Strings.
    public static String stripFormatting(String content) {

        return TextUtils.stripColors(content);
    }

    // Console logging API that automatically strips legacy and modern formatting.
    public static void logToConsole(String prefix, String message) {

        Bukkit.getLogger().info(TextUtils.stripColors(prefix + " " + message));
    }

    public static UtilitiesOG getPlugin() {

        return plugin;
    }

    public static BukkitTask runTaskAsynchronously(final Runnable run) {

        return getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), run);
    }

    // Functional interface to support TriFunction.
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {

        R apply(T t, U u, V v);
    }
}
