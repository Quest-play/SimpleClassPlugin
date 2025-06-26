package ua.questplay.skillsplugin.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SkillsGUI implements InventoryHolder {
    private final Inventory inventory;

    public SkillsGUI(Component title, int size) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void addItem(int slot, Material material, Component displayname, List<Component> lore, boolean glint) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayname);

        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore);
        }

        if (glint) {
            meta.addEnchant(Enchantment.UNBREAKING, 1 ,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
}
