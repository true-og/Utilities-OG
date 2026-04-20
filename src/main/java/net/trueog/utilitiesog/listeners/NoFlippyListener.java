// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.listeners;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import net.trueog.utilitiesog.Internal;
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.misc.FlagRegistrationException;

// Declare the NoFlippy Module with Bukkit Listeners.
public class NoFlippyListener implements Listener {

    // The can-flippy StateFlag owned by this module. Registered in onLoad via
    // registerFlag() and consulted on every trap-door interaction.
    private static StateFlag flippyFlag;

    // Listen for a player interacting with a trap-door.
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {

        final Action action = event.getAction();
        final Block blockClicked = event.getClickedBlock();
        final Player player = event.getPlayer();

        String blockContainerAsString = null;
        try {

            blockContainerAsString = blockClicked.getType().toString();

        } catch (NullPointerException error) {

            return;

        }

        final boolean condition = StringUtils.contains(blockContainerAsString, "TRAPDOOR") && action.isRightClick()
                && !player.hasPermission("noflippy.bypass");
        if (!condition) {

            return;

        }

        final Location blockLocation = BukkitAdapter.adapt(blockClicked.getLocation());
        final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final RegionQuery query = container.createQuery();
        final ApplicableRegionSet set = query.getApplicableRegions(blockLocation);
        if (!set.testState(null, flippyFlag)) {

            event.setCancelled(true);

        }

    }

    // Register the can-flippy WorldGuard StateFlag. Must be called from
    // JavaPlugin.onLoad — WorldGuard refuses flag registration after its own
    // onEnable completes. On conflict the already-registered flag is reused
    // when it is a StateFlag.
    public static void registerFlag() throws FlagRegistrationException {

        final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {

            final StateFlag flag = new StateFlag("can-flippy", true);

            registry.register(flag);

            flippyFlag = flag;

        } catch (FlagConflictException error) {

            UtilitiesOG.logToConsole(Internal.getPrefix(),
                    "ERROR: can-flippy of the NoFlippy Module has a WorldGuard Flag conflict! " + error.getMessage());

            final Flag<?> existing = registry.get("can-flippy");
            if (existing instanceof StateFlag stateFlag) {

                flippyFlag = stateFlag;

            } else {

                throw new FlagRegistrationException("Failed to register can-flippy flag due to conflict.", error);

            }

        }

    }

}
