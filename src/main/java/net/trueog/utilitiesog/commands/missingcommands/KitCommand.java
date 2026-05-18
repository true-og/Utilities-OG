// This is free and unencumbered software released into the public domain.
// Author: NotAlexNoyle.
package net.trueog.utilitiesog.commands.missingcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.UtilitiesOG;

// Stub /kit command: TrueOG has no rank kits.
public class KitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player) {

            UtilitiesOG.trueogMessage(player,
                    "&7[&a&lTrue&c&lOG &e&lNetwork&7] &6There are no kits on &2True&4OG &6for any rank.");

        }

        return true;

    }

}