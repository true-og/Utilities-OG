// This is free and unencumbered software released into the public domain.
// Author: NotAlexNoyle.
package net.trueog.utilitiesog.commands.missingcommands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.trueog.utilitiesog.UtilitiesOG;

// Stub /ah and /auction command: TrueOG has no auction house, only the physical player market.
public class AuctionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (sender instanceof Player player) {

            UtilitiesOG.trueogMessage(player,
                    "&7[&a&lTrue&c&lOG &e&lNetwork&7] &6There is no auction house on &2True&4OG&6. Visit the player market in the center of the warzone, where players rent shop plots and trade using a &bdiamond &6economy.");

        }

        return true;

    }

}
