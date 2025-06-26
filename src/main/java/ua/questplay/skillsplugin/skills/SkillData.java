package ua.questplay.skillsplugin.skills;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @param description Example, "skill_thief_speed"
 * @param price       Price in currency
 */
public record SkillData(Component name, Material icon, List<Component> description, int price) { }