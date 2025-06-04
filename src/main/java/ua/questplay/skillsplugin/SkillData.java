package ua.questplay.skillsplugin;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SkillData {
    private final Component name;
    private final Material icon;
    private final List<Component> description;
    private final String skillTag; // Например, "skill_thief_speed"
    private final List<ItemStack> price; // Цена в предметах


    public SkillData(Component name, Material icon, List<Component> description, String skillTag, List<ItemStack> price) {
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.skillTag = skillTag;
        this.price = price;
    }

    // Геттеры
    public Component getName() { return name; }
    public Material getIcon() { return icon; }
    public List<Component> getDescription() { return description; }
    public String getSkillTag() { return skillTag; }
    public List<ItemStack> getPrice() { return price; }

    // Создаёт предмет для меню
    public ItemStack createMenuItem(Player player) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(name);

        /*
        if (db.hasSkill(player.getUniqueId(), skillTag)) {
            lore.add("§a✔ Purchased!");
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            lore.add("§7Price:");
            for (ItemStack cost : price) {
                lore.add("§8- " + cost.getAmount() + "x " + getItemName(cost));
            }
        }


        //OLD Method
        if (player.getScoreboardTags().contains(skillTag)) {
            lore.add("§a✔ Purchased!");
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            lore.add("§7Price:");
            for (ItemStack cost : price) {
                lore.add("§8- " + cost.getAmount() + "x " + getItemName(cost));
            }
        }

         */
        item.setItemMeta(meta);
        return item;
    }

    private String getItemName(ItemStack item) {
        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            return item.getItemMeta().getDisplayName();
        }
        return item.getType().toString().toLowerCase().replace("_", " ");
    }
}
