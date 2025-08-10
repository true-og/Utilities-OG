// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.InternalFunctions;
// Import libraries.
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.utils.TextUtils;

// Declare the /togglecramming command in the DisableEntityCramming Module with Bukkit Commands.
public class ToggleCrammingCommand implements CommandExecutor {

    // Command execution event handler using Bukkit's CommandManager.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Store the required permission for the /togglecramming command for later
        // reference.
        final String toggleCrammingPermission = "utilities.togglecramming";

        // If the command executor is a player, do this...
        if (sender instanceof Player player) {

            // If the command runner has permission to toggle entity cramming, do this...
            if (player.hasPermission(toggleCrammingPermission)) {

                // Fetch config file from data folder.
                final FileConfiguration config = InternalFunctions.getPlugin().getConfig();

                // Flip the true/false value to the opposite of what it currently is.
                config.set("doEntityCramming", !config.getBoolean("doEntityCramming"));

                // Send confirmation message with formatting using the TrueOG Message API.
                UtilitiesOG.trueogMessage(player,
                        ("<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#FFAA00>Entity cramming has been set to <#FFFF55>"
                                + config.getBoolean("doEntityCramming") + "<#FFAA00>."));

            }
            // If the player does not have permission to use /togglecramming, do this...
            else {

                // Send a detailed, formatted permissions error message to the player.
                TextUtils.permissionsErrorMessage(player, cmd.getName(), toggleCrammingPermission);

            }

        }
        // If the command was sent by the server console, do this...
        else {

            // Send a detailed error message to the server console.
            TextUtils.logToConsole(cmd.getName() + " " + toggleCrammingPermission);

        }

        // Healthy exit status.
        return true;

    }

}
