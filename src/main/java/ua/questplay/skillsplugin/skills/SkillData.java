package ua.questplay.skillsplugin.skills;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SkillData {
    private final Component name;
    private final Material icon;
    private final List<Component> description;// Например, "skill_thief_speed"
    private final List<ItemStack> price; // Цена в предметах


    public SkillData(Component name, Material icon, List<Component> description, List<ItemStack> price) {
        this.name = name;
        this.icon = icon;
        this.description = description;
        this.price = price;
    }

    // Геттеры
    public Component getName() { return name; }
    public Material getIcon() { return icon; }
    public List<Component> getDescription() { return description; }
    public List<ItemStack> getPrice() { return price; }
}
