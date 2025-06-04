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

import java.awt.*;
import java.util.List;

public class ClassGUI implements InventoryHolder {
    private final Inventory inventory;
    private final String title;

    public ClassGUI(String title, int size) {
        this.title = title;
        this.inventory = Bukkit.createInventory(this,size, title);
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


    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public String getTitle() {
        return title;
    }
}
