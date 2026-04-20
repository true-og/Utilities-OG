// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.listeners;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

// Holds the persisted per-player phantom-disable preferences loaded from
// phantomDisabledUsers.yml. Owned by the TogglePhantoms listener/command.
public final class PhantomState {

    private static File file;
    private static YamlConfiguration preferences;

    private PhantomState() {

    }

    // Initialize the on-disk backing file and the in-memory YAML object.
    public static void load(JavaPlugin plugin) {

        file = new File(plugin.getDataFolder(), "phantomDisabledUsers.yml");

        try {

            if (!file.exists()) {

                file.createNewFile();

            }

        } catch (IOException error) {

            plugin.getLogger().severe("ERROR: Failed to create the phantom toggle cache file! " + error.getMessage());

        }

        preferences = YamlConfiguration.loadConfiguration(file);

    }

    // Backing file on disk.
    public static File getFile() {

        return file;

    }

    // Parsed YAML preferences object, mutated in-place by the toggle command.
    public static YamlConfiguration getPreferences() {

        return preferences;

    }

}