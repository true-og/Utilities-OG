package net.trueog.utilitiesog;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.sk89q.worldguard.protection.flags.StateFlag;

public class InternalFunctions {

    public static FileConfiguration getConfig() {

        return UtilitiesOG.config();

    }

    public static String getPrefix() {

        return UtilitiesOG.prefix();

    }

    public static StateFlag getFlippyFlag() {

        return UtilitiesOG.flippyFlag();

    }

    public static File getPhantomDisabledPlayersFile() {

        return UtilitiesOG.phantomDisabledPlayersFile();

    }

    public static YamlConfiguration getPhantomPreferences() {

        return UtilitiesOG.phantomPreferences();

    }

}
