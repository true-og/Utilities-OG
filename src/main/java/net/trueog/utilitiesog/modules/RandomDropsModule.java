// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.modules;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.file.FileConfiguration;

import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.listeners.RandomDropsListener;

/**
 * Integrates the former RandomDrops-OG spawner and dragon egg mechanics into
 * Utilities-OG.
 */
public final class RandomDropsModule {

    public static final String RANDOM_SPAWNER_DROPS = "RandomSpawnerDrops";
    public static final String SPAWNER_DROP_CHANCE = "SpawnerDropChance";
    public static final String RANDOM_DRAGON_EGG_DROPS = "RandomDragonEggDrops";
    public static final String DRAGON_EGG_DROP_CHANCE = "DragonEggDropChance";

    private RandomDropsModule() {

    }

    /**
     * Adds defaults for servers upgrading from earlier Utilities-OG versions and
     * registers the gameplay listener. The listener is always registered so
     * state-bearing spawner items continue to restore their mob type even when
     * random spawner awards are disabled.
     *
     * @param plugin the running Utilities-OG plugin
     */
    public static void enable(final UtilitiesOG plugin) {

        final FileConfiguration config = plugin.getConfig();
        final boolean missingDefaults = !config.contains(RANDOM_SPAWNER_DROPS, true)
                || !config.contains(SPAWNER_DROP_CHANCE, true) || !config.contains(RANDOM_DRAGON_EGG_DROPS, true)
                || !config.contains(DRAGON_EGG_DROP_CHANCE, true);

        config.addDefault(RANDOM_SPAWNER_DROPS, true);
        config.addDefault(SPAWNER_DROP_CHANCE, 0.5D);
        config.addDefault(RANDOM_DRAGON_EGG_DROPS, true);
        config.addDefault(DRAGON_EGG_DROP_CHANCE, 0.1D);

        if (missingDefaults) {

            config.options().copyDefaults(true);
            plugin.saveConfig();

        }

        plugin.getServer().getPluginManager().registerEvents(new RandomDropsListener(plugin), plugin);

    }

    public static boolean randomSpawnerDropsEnabled(final UtilitiesOG plugin) {

        return plugin.getConfig().getBoolean(RANDOM_SPAWNER_DROPS);

    }

    public static double spawnerDropChance(final UtilitiesOG plugin) {

        return boundedChance(plugin.getConfig().getDouble(SPAWNER_DROP_CHANCE));

    }

    public static boolean randomDragonEggDropsEnabled(final UtilitiesOG plugin) {

        return plugin.getConfig().getBoolean(RANDOM_DRAGON_EGG_DROPS);

    }

    public static double dragonEggDropChance(final UtilitiesOG plugin) {

        return boundedChance(plugin.getConfig().getDouble(DRAGON_EGG_DROP_CHANCE));

    }

    public static boolean succeedsChanceRoll(final double chance) {

        if (chance <= 0.0D) {

            return false;

        }

        if (chance >= 1.0D) {

            return true;

        }

        return ThreadLocalRandom.current().nextDouble() < chance;

    }

    static double boundedChance(final double chance) {

        return Math.max(0.0D, Math.min(1.0D, chance));

    }

}
