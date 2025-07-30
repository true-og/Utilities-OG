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

// Declare the About Module with Bukkit Commands.
public class AboutCommand implements CommandExecutor {

    // Command execution event handler using Bukkit's CommandManager.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Store the required permission for the "about" (root /utilities) command for
        // later reference.
        final String aboutPermission = "utilities.about";

        // If the command was sent by an in-game player, do this...
        if (sender instanceof Player player) {

            // If the player has permission to use /about, do this...
            if (player.hasPermission(aboutPermission)) {

                // Send a formatted about message to the player using the TrueOG Message API.
                UtilitiesOG.trueogMessage(player,
                        "<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#FFAA00>A collection of utilities and APIs used by <#00AA00>True<#AA0000>OG<#FFAA00> <#FFFF55>Network<#FFAA00>. Developed by <#FFFF55>christianniehaus <#FFAA00>& <#FFFF55>NotAlexNoyle<#FFAA00>.");

            }
            // If the player does not have permission to use /about, do this...
            else {

                // Send a detailed, formatted permissions error message to the player.
                TextUtils.permissionsErrorMessage(player, cmd.getName(), aboutPermission);

            }

        }
        // If the command was sent by the server console, do this...
        else {

            // Send a detailed error message to the server console.
            TextUtils.logToConsole(cmd.getName() + " " + aboutPermission);

        }

        // Healthy exit status.
        return true;

    }

}