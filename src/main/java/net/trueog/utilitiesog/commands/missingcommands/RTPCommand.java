// This is free and unencumbered software released into the public domain.
// Author: NotAlexNoyle.
package net.trueog.utilitiesog.commands.missingcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.UtilitiesOG;

// Stub /rtp command: TrueOG does not implement random teleport.
public class RTPCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player) {

            UtilitiesOG.trueogMessage(player,
                    "&7[&a&lTrue&c&lOG &e&lNetwork&7] &e/rtp &6will not be implemented! Escape spawn yourself!");

        }

        return true;

    }

}