package net.trueog.utilitiesog.Listeners;
// Import libraries.
import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import org.bukkit.GameMode;
// Hook into Bukkit's Listener.
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NoAdvancementsInCreativeListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAdvancementProgress(PlayerAdvancementCriterionGrantEvent event) {
        if (event.getPlayer().getGameMode() != GameMode.SURVIVAL) {
            event.setCancelled(true);
        }
    }
}
