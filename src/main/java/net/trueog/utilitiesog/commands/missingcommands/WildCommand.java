// This is free and unencumbered software released into the public domain.
// Author: NotAlexNoyle.
package net.trueog.utilitiesog.commands.missingcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.UtilitiesOG;

// Stub /wild command: TrueOG does not implement teleport-to-wilderness.
public class WildCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player) {

            UtilitiesOG.trueogMessage(player,
                    "&7[&a&lTrue&c&lOG &e&lNetwork&7] &e/wild &6will not be implemented! &cEscape spawn!");

        }

        return true;

    }

}
