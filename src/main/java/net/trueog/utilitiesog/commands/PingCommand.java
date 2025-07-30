// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// Import libraries.
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.utils.TextUtils;

// Declare the /ping command in the Ping Module with Bukkit Commands.
public class PingCommand implements CommandExecutor {

    // Command execution event handler using Bukkit's CommandManager.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Store the required permission for the /ping command for later reference.
        final String pingPermission = "utilities.ping";

        // If the command was sent by an in-game player, do this...
        if (sender instanceof Player player) {

            // If the player has permission to use /ping, do this...
            if (player.hasPermission(pingPermission)) {

                // If the player did not provide any arguments, do this...
                if (args.length == 0) {

                    // Send player their own ping with formatting using the TrueOG Message API.
                    UtilitiesOG.trueogMessage(player,
                            ("<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#00AA00>Your current ping is: <#FFAA00>"
                                    + player.getPing() + "<#00AA00>."));

                }
                // If the player provided one or more arguments, do this...
                else if (args.length == 1) {

                    // Try to get the player that was specified.
                    final Player target = Bukkit.getPlayer(args[0]);

                    // If the provided argument was a valid player, do this...
                    if (target != null) {

                        // Send player their specified player's ping with formatting using the TrueOG
                        // Message API.
                        UtilitiesOG.trueogMessage(player,
                                ("<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#00AA00>" + target.getName()
                                        + "'s current ping is: <#FFAA00>" + target.getPing() + "<#00AA00>."));

                    }
                    // If the provided argument was not a valid player, do this...
                    else {

                        // Send a "player not found" error message with formatting using the TrueOG
                        // Message API.
                        UtilitiesOG.trueogMessage(player,
                                ("<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#AA0000> ERROR: The player "
                                        + args[0] + " was not found!"));

                    }

                }
                // If there is more than one provided argument, do this...
                else {

                    // Send a "too many arguments" error message with formatting using the TrueOG
                    // Message API.
                    UtilitiesOG.trueogMessage(player,
                            ("<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#AA0000> ERROR: Too many arguments provided. Syntax: <#FFFF55>/ping <player>"));

                }

            }
            // If the player does not have permission to use /ping, do this...
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
