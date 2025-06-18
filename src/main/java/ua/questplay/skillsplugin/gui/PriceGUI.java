package ua.questplay.skillsplugin.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PriceGUI implements InventoryHolder {
    private final Inventory inventory;

    public PriceGUI(Component title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void addItem(int slot, Material material, Component displayname, List<Component> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(displayname);

        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }

        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }

    public void addItemCount(int slot, Material material, Component displayname, List<Component> lore, int count) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(displayname);

        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }

        item.setAmount(count);
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }


    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
