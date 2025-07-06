package ua.questplay.skillsplugin.commands;

import net.kyori.adventure.text.Component;
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

import java.util.List;

public class SkillsCommand implements CommandExecutor {
    private final SkillsPlugin plugin;
    private final String skillTag = null;

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
            String[] thiefSkills  = {
                    "KILLER_SPEED",
                    "KILLER_MURDER",
                    "KILLER_HASTE",
                    "KILLER_VAMPIRISM",
                    "KILLER_RECOVERY"
            };
            for (String skillName : thiefSkills) {
                SkillType type = Enum.valueOf(SkillType.class, skillName);
                SkillData skill = plugin.getSkillManager().getSkill(type);

                skillsGUI.addItem(slots, skill.icon(), skill.name(), skill.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), skill.tag()));
                slots++;
            }
        }
        //-------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            int slots = 9;
            String[] thiefSkills  = {
                    "MERCHANT_LUCK",
                    "MERCHANT_EXP",
                    "MERCHANT_RUN",
                    "MERCHANT_HERO",
                    "MERCHANT_BLESSING"
            };
            for (String skillName : thiefSkills) {
                SkillType type = Enum.valueOf(SkillType.class, skillName);
                SkillData skill = plugin.getSkillManager().getSkill(type);

                skillsGUI.addItem(slots, skill.icon(), skill.name(), skill.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), skill.tag()));
                slots++;
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            int slots = 9;
            String[] thiefSkills  = {
                    "THIEF_SPEED",
                    "THIEF_HASTE",
                    "THIEF_EXP",
                    "THIEF_CAUTION",
                    "THIEF_SPECIALIZATION"
            };
            for (String skillName : thiefSkills) {
                SkillType type = Enum.valueOf(SkillType.class, skillName);
                SkillData skill = plugin.getSkillManager().getSkill(type);

                skillsGUI.addItem(slots, skill.icon(), skill.name(), skill.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), skill.tag()));
                slots++;
            }
        }


        skillsGUI.open(player);

        return true;
    }

}
