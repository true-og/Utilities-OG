// This is free and unencumbered software released into the public domain.
// Author: NotAlexNoyle.
package net.trueog.utilitiesog.commands.missingcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.UtilitiesOG;

// Stub /seed command: blocks vanilla seed reveal unless caller has seed.bypass.
public class SeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender.hasPermission("seed.bypass")) {

            sender.getServer().dispatchCommand(sender, "minecraft:seed");

            return true;

        }

        if (sender instanceof Player player) {

            UtilitiesOG.trueogMessage(player,
                    "&7[&a&lTrue&c&lOG &e&lNetwork&7] &6The season 1 seed, which covers terrain up to &e40K &6in the overworld, is: &a4725084288293652062&6. Seed hunting for other terrain is a &cbanable &6offense.");

        }

        return true;

    }

}