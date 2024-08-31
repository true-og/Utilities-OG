// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package me.barny1094875.utilitiesog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldguard.protection.flags.StateFlag;

import me.barny1094875.utilitiesog.Listeners.DisableEntityCrammingListener;
import me.barny1094875.utilitiesog.Listeners.NoFlippyListener;
import me.barny1094875.utilitiesog.Listeners.TogglePhantomsListener;
import me.barny1094875.utilitiesog.commands.AboutCommand;
import me.barny1094875.utilitiesog.commands.BingCommand;
import me.barny1094875.utilitiesog.commands.ColorCodesCommand;
import me.barny1094875.utilitiesog.commands.PingCommand;
import me.barny1094875.utilitiesog.commands.RanksCommand;
import me.barny1094875.utilitiesog.commands.ToggleCrammingCommand;
import me.barny1094875.utilitiesog.commands.TogglePhantomsCommand;
import me.barny1094875.utilitiesog.internal.FlagRegistrationException;
import me.barny1094875.utilitiesog.internal.PlaceholderType;
import me.barny1094875.utilitiesog.modules.ChainArmorModule;
import me.barny1094875.utilitiesog.modules.MockBambooModule;
import me.barny1094875.utilitiesog.utils.PlaceholderUtils;
import me.barny1094875.utilitiesog.utils.TextUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

// Declare main plugin class.
public final class UtilitiesOG extends JavaPlugin {

	// Declare empty fields for use later.
	private File file;
	private static UtilitiesOG plugin;
	private static FileConfiguration config;
	private PlaceholderUtils playerDisplayNameMiniPlaceholder;
	private static File phantomPreferencesFile;
	private static YamlConfiguration phantomPreferences;
	private static StateFlag FlippyFlag;

	// Maintain a map of registered placeholders.
	private static final Map<String, PlaceholderUtils> registeredPlaceholders = new HashMap<>();

	// When the plugin is enabled, do this...
	@Override
	public void onEnable() {

		// Populate the main class getter getPlugin().
		plugin = this;

		// Attempt to populate the file object with the config file.
		this.file = new File(this.getDataFolder(), "config.yml");

		// If the config file was not found, do this...
		if (! this.file.exists()) {

			// Create the config file in the filesystem using the one in the assembled plugin .jar.
			this.saveDefaultConfig();

		}

		// Cast the config file from a File to a FileConfiguration.
		config = this.getConfig();

		// Attempt to populate the file object with the phantom toggle cache file.
		phantomPreferencesFile = new File(this.getDataFolder(), "phantomDisabledUsers.yml");

		try {

			// If the phantom toggle cache file was not found, do this...
			if (! phantomPreferencesFile.exists()) {

				// Create the phantom toggle cache file in the filesystem using the one in the assembled plugin .jar.
				phantomPreferencesFile.createNewFile();

			}

		}
		// If there is a file IO error, do this...
		catch (IOException error) {

			// Log the problem with creating the phantom toggle cache file to the server console.
			this.getLogger().severe("ERROR: Failed to create the phantom toggle cache file! " + error.getMessage());

		}

		// Cast the phantom toggle cache file from a File to a YamlConfiguration.
		phantomPreferences = YamlConfiguration.loadConfiguration(phantomPreferencesFile);

		// TrueOG Network Contributors: set up each feature below, using the config file to enable only that which is desirable by default.
		// Be sure to label, using comments, which feature(s) each block is enabling.

		// FEATURE: Enable crafting chain armor using chains.
		// If the Chain Armor Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("ChainArmor")) {

			// Enable the Chain Armor Module.
			ChainArmorModule.Enable();

		}

		// FEATURE: Display information to the player about the syntax for bukkit's color codes.
		// If the Color Codes Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("ColorCodes")) {

			// If the Color Codes Module is enabled in the config file, do this...
			this.getCommand("colorcodes").setExecutor(new ColorCodesCommand());

		}

		// FEATURE: Disable entity cramming server-wide. Entity cramming is when mobs pile up and cause damage to each other.
		// WARNING: Make sure there is another plugin that establishes entity limits if this module is enabled. Otherwise the server will lag.
		// If the Entity Cramming Toggle Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("DisableEntityCramming")) {

			// Activate the Entity Cramming Toggle Listener.
			getServer().getPluginManager().registerEvents(new DisableEntityCrammingListener(), this);

			// Activate the Entity Cramming Toggle Command.
			this.getCommand("togglecramming").setExecutor(new ToggleCrammingCommand());

		}

		// FEATURE: A custom, easy to use MiniPlaceholders API.
		// If the built-in MiniPlaceholders Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("MiniPlaceholderAPI")) {

			trueOGRegisterMiniPlaceholder("player_display_name", PlaceholderType.AUDIENCE, "<dark_purple><luckperms_prefix><player></dark_purple>");

		}

		// FEATURE: Pre-1.20 Mock Bamboo Wood. Makes Bamboo craft into Oak Planks called "Bamboo Wood".
		// If the Bamboo Wood Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("MockBamboo")) {

			// Enable the Bamboo Wood Module.
			MockBambooModule.Enable();

		}

		// FEATURE: Prevent trap doors from being flipped in any WorldGuard regions with the "can-flippy" flag set to DENY.
		// If the NoFlippy Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("NoFlippy")) {

			// If WorldGuard is installed, do this...
			if (getServer().getPluginManager().getPlugin("WorldGuard") != null) {

				// Activate the NoFlippy Listener.
				getServer().getPluginManager().registerEvents(new NoFlippyListener(), this);

			}
			// If WorldGuard is not installed, do this...
			else {

				// Log WorldGuard not being found to the server console.
				this.getLogger().severe("WorldGuard is not installed! Disabling NoFlippy...");

			}

		}

		// FEATURE: A /ping command that returns a player's real ping.
		// FEATURE: A /bing command that replicates the functionality of the default /ping command.
		// If the Ping Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("Ping")) {

			// Enable the Ping command.
			this.getCommand("ping").setExecutor(new PingCommand());

			// Enable the Bing command.
			this.getCommand("bing").setExecutor(new BingCommand());

		}

		// FEATURE: Display information to the player about the ranks available at the TrueOG Network Store (https://store.true-og.net/).
		// If the Ranks Menu Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("RanksMenu")) {

			// Activate the Ranks Command.
			this.getCommand("ranks").setExecutor(new RanksCommand());

		}

		// FEATURE: Enable individual players to toggle phantom spawning on or off with the command /togglephantoms.
		// If the Phantom Toggle Module is enabled in the config file, do this...
		if (this.getConfig().getBoolean("TogglePhantoms")) {

			// Activate the Phantom Toggle Listener.
			getServer().getPluginManager().registerEvents(new TogglePhantomsListener(), this);

			// Activate the Phantom Toggle Command.
			this.getCommand("togglephantoms").setExecutor(new TogglePhantomsCommand());

		}

		// FEATURE: Display meta-information about Utilities-OG. Always enabled.
		// Enable the root /utilities command to display information about the plugin to the player.
		this.getCommand("utilities").setExecutor(new AboutCommand());

	}

	// After the server loads, do this...
	@Override
	public void onLoad() {

		try {

			// Register the Flag "can-flippy" with WorldGuard, and make the it retrievable.
			FlippyFlag = NoFlippyListener.registerNoFlippyWorldGuardFlag(FlippyFlag);

		}
		// If the WorldGuard Flag registration failed, do this...
		catch (FlagRegistrationException error) {

			// Log the WorldGuard Flag registration error to the server console.
			this.getLogger().severe("ERROR: Failed to register the can-flippy Flag with WorldGuard. " + error.getMessage());

		}

	}

	// As the server un-loads, do this...
	@Override
	public void onDisable() {

		// Unregister the MiniPlaceholderAPI if it was enabled.
		disableMiniPlaceholderAPI();

	}

	// Unregister the placeholder.
	private void disableMiniPlaceholderAPI() {

		if (playerDisplayNameMiniPlaceholder != null) {

			String placeholderName = playerDisplayNameMiniPlaceholder.getFullPlaceholderName();

			trueOGUnregisterMiniPlaceholder(placeholderName);

			playerDisplayNameMiniPlaceholder = null;

		}

	}

	// Getter for the config file.
	public static FileConfiguration config() {

		// Return the config file in FileConfiguration form.
		return config;

	}

	// Getter for the FlippyFlag state.
	public static StateFlag getFlippyFlag() {

		// Return the FlippyFlag for WorldGuard.
		return FlippyFlag;

	}

	// Getter for the phantom toggle cache file.
	public static File getPhantomDisabledPlayersFile() {

		// Return the phantom toggle cache file.
		return phantomPreferencesFile;

	}

	// Getter for the phantom toggle cache file in YAML form.
	public static YamlConfiguration getPhantomPreferences() {

		// Return the phantom toggle cache file in YAML form.
		return phantomPreferences;

	}

	// API for sending TrueOG Player messages. Supports modern color codes, and legacy Bukkit color codes (case INsensitive).
	public static void trueOGMessage(Player player, String message) {

		// Forward the message to the server.
		TextUtils.utilitiesOGMessage(player, message);

	}

	// API for TrueOG MiniPlaceholderAPI expansion (with color code processing from trueOGColorize).
	public static TextComponent trueOGExpandMiniPlaceholders(Player player, String input) {

		// Expand all the MiniPlaceholders in a given String.
		return TextUtils.expandPlayerMiniPlaceholders(player, input);

	}

	// API for the TrueOG text colorizer.
	public static TextComponent trueogColorize(Player player, String message) {

		// Convert the String's legacy color codes to modern ones.
		String processedMessage = TextUtils.processColorCodes(message);

		// Use MiniMessage to parse the modern colors in the String and convert it to a Component.
		Component component = MiniMessage.miniMessage().deserialize(processedMessage);

		// If the result is a TextComponent, do this...
		if (component instanceof TextComponent) {

			// Cast and send off.
			return (TextComponent) component;

		}
		// If the result is not a TextComponent, do this...
		else {

			// Convert the Component to a TextComponent.
			return Component.text().append(component).build();

		}

	}

	// API for easy creation and registration of a MiniPlaceholder.
	public static void trueOGRegisterMiniPlaceholder(String placeholderName, PlaceholderType placeholderType, String content) {

		int lastIndex = placeholderName.lastIndexOf('_');

		String placeholderPrefix = "";
		String placeholderSuffix = "";
		if (lastIndex != -1) {

			// Split the placeholder name into its prefix and suffix.
			placeholderPrefix = placeholderName.substring(0, lastIndex);
			placeholderSuffix = placeholderName.substring(lastIndex + 1);

		}

		// Register the placeholder with MiniPlaceholders
		PlaceholderUtils placeholderUtils = new PlaceholderUtils(placeholderPrefix, placeholderSuffix, placeholderType, TextUtils.expandGlobalMiniPlaceholders(content));

		// Register the placeholder and store it in the map.
		placeholderUtils.register();
		registeredPlaceholders.put(placeholderName, placeholderUtils);

	}

	// API to unregister a specific MiniPlaceholder by its name.
	public static void trueOGUnregisterMiniPlaceholder(String placeholderName) {

		PlaceholderUtils placeholder = registeredPlaceholders.remove(placeholderName);
		if (placeholder != null) {

			placeholder.unregister();

		}
		else {

			TextUtils.logToConsole("TrueOG MiniPlaceholdersAPI: Placeholder " + placeholderName + " not found.");

		}

	}

	// API to check if a MiniPlaceholder is already registered based on its name.
	public static boolean isMiniPlaceholderRegistered(String placeholderName) {

		// Check if the placeholder is in the registeredPlaceholders map.
		return registeredPlaceholders.containsKey(placeholderName);

	}

	// API for getting the Utilities-OG instance.
	public static UtilitiesOG getPlugin() {

		// Return the current Utilities-OG instance.
		return plugin;

	}

	// API for basic async tasks using the BukkitTask API.
	public static BukkitTask runTaskAsynchronously(final Runnable run) {

		// Schedule processes.
		return getPlugin().getServer().getScheduler().runTaskAsynchronously(getPlugin(), run);

	}

}