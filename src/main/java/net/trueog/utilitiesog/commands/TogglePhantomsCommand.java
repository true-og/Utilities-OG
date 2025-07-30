// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.commands;

// Import libraries.
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.InternalFunctions;
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.utils.TextUtils;

// Declare the /togglephantoms command in the Ping Module with Bukkit Commands.
public class TogglePhantomsCommand implements CommandExecutor {

    // Command execution event handler using Bukkit's CommandManager.
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // Store the required permission for the /togglephantoms command for later
        // reference.
        final String togglePhantomsPermission = "utilities.togglephantoms";

        // If the command was sent by an in-game player, do this...
        if (sender instanceof Player player) {

            // If the player has permission to use /togglephantoms, do this...
            if (player.hasPermission(togglePhantomsPermission)) {

                // Store the contents of phantomDisabledUsers.yml file as a YAML object.
                final YamlConfiguration phantomPreferences = InternalFunctions.getPhantomPreferences();

                // Cherry pick the command runner's phantom status from the YAML data set.
                final boolean phantomToggleState = phantomPreferences.getBoolean(player.getUniqueId().toString());

                // Flip the true/false value to the opposite of what it currently is.
                phantomPreferences.set(player.getUniqueId().toString(), !phantomToggleState);

                // If the player's phantom spawning is turned on, do this...
                if (phantomToggleState) {

                    // Send a formatted "true" message with formatting using the TrueOG Message API.
                    UtilitiesOG.trueogMessage(player,
                            "<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#55FF55>Phantom spawning turned <#00AA00>ON<#55FF55>.");

                }
                // If the command sender's phantom spawning is turned off, do this...
                else {

                    // Send a formatted "false" message with formatting using the TrueOG Message
                    // API.
                    UtilitiesOG.trueogMessage(player,
                            "<#AAAAAA>[<#00AA00>Utilities<#AA0000>-OG<#AAAAAA>] <#FFAA00>Phantom spawning turned <#FF5555>OFF<#FFAA00>.");

                }

                try {

                    // Save changes to YAML data to disk.
                    phantomPreferences.save(InternalFunctions.getPhantomDisabledPlayersFile());

                } catch (IOException error) {

                    // Throw a runtime error if the YAML data could not be saved to disk.
                    throw new RuntimeException(error);

                }

            }
            // If the player does not have permission to use /togglephantoms, do this...
            else {

                // Send a detailed, formatted permissions error message to the player.
                TextUtils.permissionsErrorMessage(player, cmd.getName(), togglePhantomsPermission);

            }

        }
        // If the command was sent by the server console, do this...
        else {

            // Send a detailed error message to the server console.
            TextUtils.logToConsole(cmd.getName() + " " + togglePhantomsPermission);

        }

        // Healthy exit status.
        return true;

    }

}
