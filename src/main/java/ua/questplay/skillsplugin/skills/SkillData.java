package ua.questplay.skillsplugin.skills;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;

import java.util.List;

/**
 * @param name Skill Name. Use net.kyori.adventure.text.Component.
 * @param icon Skill Icon. Use org.bukkit.Material.
 * @param description Skill Description. Use net.kyori.adventure.text.Component.
 * @param price Price in currency
 * @param tag Skill tag.
 */
public record SkillData(Component name, Material icon, List<Component> description, int price, String tag) { }