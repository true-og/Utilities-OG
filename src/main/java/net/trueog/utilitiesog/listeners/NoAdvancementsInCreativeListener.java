package net.trueog.utilitiesog.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import net.trueog.utilitiesog.FastOfflinePlayer;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NoAdvancementsInCreativeListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancementProgress(PlayerAdvancementCriterionGrantEvent event) {

        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {

            event.setCancelled(true);

        }

        final FastOfflinePlayer fop = new FastOfflinePlayer(event.getPlayer().getUniqueId());

    }

}
