// This is free and unencumbered software released into the public domain.
// Author: NotAlexNoyle.
package net.trueog.utilitiesog.commands.missingcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

// Stub /info command: forwards to /help with the same arguments.
public class InfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        final String helpCommand = args.length == 0 ? "help" : "help " + String.join(" ", args);

        sender.getServer().dispatchCommand(sender, helpCommand);

        return true;

    }

}
