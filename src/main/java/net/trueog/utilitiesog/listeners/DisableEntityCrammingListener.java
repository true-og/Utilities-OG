// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.trueog.utilitiesog.InternalFunctions;

// Declare the EntityCrammingToggle Module with Bukkit Listeners.
public class DisableEntityCrammingListener implements Listener {

    // Listen for entities being damaged.
    @EventHandler
    public void disableEntityCramming(EntityDamageEvent event) {

        // If the Disable Entity Cramming Module is enabled in config.yml, do this...
        // Cancel damage from entity cramming.
        // If the damage is caused by entity cramming, do this...
        if (event.getCause() == DamageCause.CRAMMING) {

            // If the Disable Entity Cramming Module is enabled in config.yml, do this...
            if (InternalFunctions.getConfig().getBoolean("EntityCrammingDisable") == true) {

                // Cancel damage from entity cramming.
                event.setCancelled(true);

            }

        }

    }

}