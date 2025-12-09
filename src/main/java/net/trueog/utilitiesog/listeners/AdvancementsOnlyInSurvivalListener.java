// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.listeners;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;

public class AdvancementsOnlyInSurvivalListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancementProgress(PlayerAdvancementCriterionGrantEvent event) {

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {

            event.setCancelled(true);

        }

    }

}