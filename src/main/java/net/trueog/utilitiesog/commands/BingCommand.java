// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Import libraries.
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.utils.TextUtils;

// Declare the /bing command in the Ping Module with Bukkit Commands.
public class BingCommand implements CommandExecutor {

    // Command execution event handler using Bukkit's CommandManager.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Store the required permission for the /ping command for later reference.
        final String pingPermission = "utilities.ping";

        // If the command was sent by an in-game player, do this...
        if (sender instanceof Player player) {

            // If the player has permission to use the Ping Module, do this...
            if (player.hasPermission(pingPermission)) {

                // Send player a connection confirmation message with formatting using the
                // TrueOG Message API.
                UtilitiesOG.trueogMessage(player, "<#FFFFFF> Bong!");

            }
            // If the player does not have permission to use the Ping Module, do this...
            else {

                // Send a detailed, formatted permissions error message to the player.
                TextUtils.permissionsErrorMessage(player, cmd.getName(), pingPermission);

            }

        }
        // If the command was sent by the server console, do this...
        else {

            // Send a detailed error message to the server console.
            TextUtils.logToConsole(cmd.getName() + " " + pingPermission);

        }

        // Healthy exit status.
        return true;

    }

}