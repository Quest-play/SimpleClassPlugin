package ua.questplay.skillsplugin.skills;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @param description Например, "skill_thief_speed"
 * @param price       Цена в предметах
 * @param currencyPrice Цена в валюте
 */
public record SkillData(Component name, Material icon, List<Component> description, List<ItemStack> price, int currencyPrice) { }