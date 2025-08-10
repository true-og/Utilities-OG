// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle.
package net.trueog.utilitiesog;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.protection.flags.StateFlag;

// Makes functions in the main class accessible internally without cluttering up suggestions for UtilitiesOG API users.
public class InternalFunctions {

    // Getter for the plugin prefix.
    public static String getPrefix() {

        return UtilitiesOG.prefix();

    }

    // Getter for the config.yml object.
    public static FileConfiguration getConfig() {

        return UtilitiesOG.config();

    }

    // Getter for the plugin object.
    public static Plugin getPlugin() {

        return UtilitiesOG.plugin();

    }

    // Getter for the NoFlippy state flag.
    public static StateFlag getFlippyFlag() {

        return UtilitiesOG.flippyFlag();

    }

    // Getter for the phantom disabled players file.
    // TODO: Replace with database.
    public static File getPhantomDisabledPlayersFile() {

        return UtilitiesOG.phantomDisabledPlayersFile();

    }

    // Getter for the phantom preferences object.
    public static YamlConfiguration getPhantomPreferences() {

        return UtilitiesOG.phantomPreferences();

    }

}