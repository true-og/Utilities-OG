// This is free and unencumbered software released into the public domain.
// Authors: christianniehaus, NotAlexNoyle.
package net.trueog.utilitiesog.listeners;

import io.papermc.paper.event.block.DragonEggFormEvent;
import net.kyori.adventure.text.Component;
import net.trueog.utilitiesog.UtilitiesOG;
import net.trueog.utilitiesog.modules.RandomDropsModule;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

/**
 * Event handling retained from RandomDrops-OG. Every handler ignores already
 * cancelled events, so protection plugins retain complete control.
 */
public final class RandomDropsListener implements Listener {

    private final UtilitiesOG plugin;

    public RandomDropsListener(final UtilitiesOG plugin) {

        this.plugin = plugin;

    }

    @EventHandler(ignoreCancelled = true)
    public void onDragonEggForm(final DragonEggFormEvent event) {

        if (!RandomDropsModule.randomDragonEggDropsEnabled(this.plugin)) {

            return;

        }

        if (!RandomDropsModule.succeedsChanceRoll(RandomDropsModule.dragonEggDropChance(this.plugin))) {

            event.setCancelled(true);

        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerBreak(final BlockBreakEvent event) {

        if (event.getBlock().getType() != Material.SPAWNER || !RandomDropsModule.randomSpawnerDropsEnabled(this.plugin)
                || !event.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)
                || !RandomDropsModule.succeedsChanceRoll(RandomDropsModule.spawnerDropChance(this.plugin)))
        {

            return;

        }

        final BlockState sourceBlockState = event.getBlock().getState();
        if (!(sourceBlockState instanceof CreatureSpawner)) {

            return;

        }

        final CreatureSpawner sourceSpawner = (CreatureSpawner) sourceBlockState;
        final EntityType spawnedType = sourceSpawner.getSpawnedType();
        if (spawnedType == null) {

            return;

        }

        final ItemStack droppedSpawner = new ItemStack(Material.SPAWNER);
        if (!(droppedSpawner.getItemMeta() instanceof BlockStateMeta)) {

            return;

        }

        final BlockStateMeta droppedMeta = (BlockStateMeta) droppedSpawner.getItemMeta();
        final BlockState droppedBlockState = droppedMeta.getBlockState();
        if (!(droppedBlockState instanceof CreatureSpawner)) {

            return;

        }

        final CreatureSpawner droppedSpawnerState = (CreatureSpawner) droppedBlockState;
        droppedSpawnerState.setSpawnedType(spawnedType);
        droppedMeta.setBlockState(droppedSpawnerState);
        droppedSpawner.setItemMeta(droppedMeta);
        droppedSpawner.lore(java.util.List.of(Component.text(spawnedType.toString())));

        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), droppedSpawner);

    }

    @EventHandler(ignoreCancelled = true)
    public void onSpawnerPlace(final BlockPlaceEvent event) {

        if (event.getBlock().getType() != Material.SPAWNER || event.getItemInHand().getType() != Material.SPAWNER
                || !(event.getItemInHand().getItemMeta() instanceof BlockStateMeta))
        {

            return;

        }

        final BlockStateMeta placedItemMeta = (BlockStateMeta) event.getItemInHand().getItemMeta();
        final BlockState placedItemState = placedItemMeta.getBlockState();
        final BlockState placedBlockState = event.getBlock().getState();
        if (!(placedItemState instanceof CreatureSpawner) || !(placedBlockState instanceof CreatureSpawner)) {

            return;

        }

        final EntityType spawnedType = ((CreatureSpawner) placedItemState).getSpawnedType();
        if (spawnedType == null) {

            return;

        }

        final CreatureSpawner placedSpawner = (CreatureSpawner) placedBlockState;
        placedSpawner.setSpawnedType(spawnedType);
        placedSpawner.update();

    }

}
