// This is free and unencumbered software released into the public domain.
// Authors: NotAlexNoyle, SKBotNL.
package net.trueog.utilitiesog.utils;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;

// Profile Inventory list to and from Bukkit items, through Item-NBT-API.
public final class InventoryNbtCodec {

    // Kept apart from UtilitiesOG so that class loads without Item-NBT-API.
    private InventoryNbtCodec() {

    }

    // 41 slots in PlayerInventory.getContents() order
    public static ItemStack[] read(Object tag) {

        final ItemStack[] inventoryContents = new ItemStack[41];
        final NBTContainer playerData = new NBTContainer(tag);
        for (ReadWriteNBT slotTag : playerData.getCompoundList("Inventory")) {

            final int index = toContentsIndex(slotTag.getByte("Slot"));
            if (index >= 0) {

                inventoryContents[index] = NBT.itemStackFromNBT(slotTag);

            }

        }

        return inventoryContents;

    }

    // Replaces the Inventory list in place and returns the tag to save
    public static Object write(Object tag, ItemStack[] items) {

        final NBTContainer playerData = new NBTContainer(tag);
        playerData.removeKey("Inventory");
        final NBTCompoundList inventoryData = playerData.getCompoundList("Inventory");
        for (int i = 0; i < items.length && i <= 40; i++) {

            final ItemStack item = items[i];
            if (item == null || item.getType().isAir()) {

                continue;

            }

            final ReadWriteNBT slotTag = inventoryData.addCompound(NBT.itemStackToNBT(item));
            slotTag.setByte("Slot", toNbtSlot(i));

        }

        return playerData.getCompound();

    }

    public static int heldItemSlot(Object tag) {

        return new NBTContainer(tag).getInteger("SelectedItemSlot");

    }

    // Vanilla Slot byte to getContents() index, -1 for unexposed slots.
    private static int toContentsIndex(byte slot) {

        if (slot >= 0 && slot <= 35) {

            return slot;

        } else if (slot >= 100 && slot <= 103) {

            return slot - 64;

        } else if (slot == -106) {

            return 40;

        }

        return -1;

    }

    private static byte toNbtSlot(int index) {

        if (index <= 35) {

            return (byte) index;

        } else if (index <= 39) {

            return (byte) (index + 64);

        }

        return (byte) -106;

    }

}
