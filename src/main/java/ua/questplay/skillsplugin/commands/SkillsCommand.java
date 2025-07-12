package ua.questplay.skillsplugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.gui.SkillsGUI;
import ua.questplay.skillsplugin.skills.SkillType;

import java.util.ArrayList;
import java.util.List;

public class SkillsCommand implements CommandExecutor {
    private final SkillsPlugin plugin;
    private final List<Component> desc = new ArrayList<>();

    public SkillsCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return true;
        }

        if (!plugin.getDbManager().hasClass(player.getUniqueId())) {
            sender.sendMessage(plugin.formattedFromKey("class_command.choose_class"));
            return true;
        }
        SkillsGUI skillsGUI = new SkillsGUI(plugin.formattedFromKey("skills_gui.title"),54);

        Component frameName = Component.text("");
        List<Component> frameLore = List.of(Component.text(""));

        for (int i = 0; i < 9; i++) {
            skillsGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, frameLore, false);
        }

        for (int i = 45; i < 54; i++) {
            skillsGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, frameLore, false);
        }

        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_killer")) {
            int slots = 9;
            String[] killerSkills  = plugin.getDbManager().getSkillNames("KILLER");

            for (String skillName : killerSkills) {
                SkillType type = Enum.valueOf(SkillType.class, skillName);
                SkillData skill = plugin.getSkillManager().getSkill(type);

                desc.addAll(skill.description());
                if (plugin.getDbManager().hasSkill(player.getUniqueId(), skill.tag())) {
                    desc.addLast(plugin.formattedFromKey("skills_gui.tick"));
                    skillsGUI.addItem(slots, skill.icon(), skill.name(), desc, true);
                } else {
                    skillsGUI.addItem(slots, skill.icon(), skill.name(), skill.description(), false);
                }
                desc.clear();
                slots++;
            }
        }
        //-------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            int slots = 9;
            String[] merchantSkills  = plugin.getDbManager().getSkillNames("MERCHANT");

            for (String skillName : merchantSkills) {
                SkillType type = Enum.valueOf(SkillType.class, skillName);
                SkillData skill = plugin.getSkillManager().getSkill(type);

                desc.addAll(skill.description());
                if (plugin.getDbManager().hasSkill(player.getUniqueId(), skill.tag())) {
                    desc.addLast(plugin.formattedFromKey("skills_gui.tick"));
                    skillsGUI.addItem(slots, skill.icon(), skill.name(), desc, true);
                } else {
                    skillsGUI.addItem(slots, skill.icon(), skill.name(), skill.description(), false);
                }
                desc.clear();
                slots++;
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            int slots = 9;
            String[] thiefSkills  = plugin.getDbManager().getSkillNames("THIEF");


            for (String skillName : thiefSkills) {
                SkillType type = Enum.valueOf(SkillType.class, skillName);
                SkillData skill = plugin.getSkillManager().getSkill(type);

                desc.addAll(skill.description());
                if (plugin.getDbManager().hasSkill(player.getUniqueId(), skill.tag())) {
                    desc.addLast(plugin.formattedFromKey("skills_gui.tick"));
                    skillsGUI.addItem(slots, skill.icon(), skill.name(), desc, true);
                } else {
                    skillsGUI.addItem(slots, skill.icon(), skill.name(), skill.description(), false);
                }
                desc.clear();
                slots++;
            }
        }


        skillsGUI.open(player);

        return true;
    }

}
