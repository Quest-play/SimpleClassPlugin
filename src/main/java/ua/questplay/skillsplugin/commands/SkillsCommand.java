package ua.questplay.skillsplugin.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.gui.SkillsGUI;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.skills.SkillType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkillsCommand implements BasicCommand {
    private final SkillsPlugin plugin;
    private final List<Component> desc = new ArrayList<>();

    public SkillsCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Player player = (Player) commandSourceStack.getSender();

        if (!plugin.getDbManager().hasClass(player.getUniqueId())) {
            player.sendMessage(plugin.formattedFromKey("class_command.choose_class"));
            return;
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
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_tank")) {
            int slots = 9;
            String[] tankSkills  = plugin.getDbManager().getSkillNames("TANK");


            for (String skillName : tankSkills) {
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
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return false;
        }

        if (!BasicCommand.super.canUse(sender)) {
            return false;
        }

        return true;
    }

    @Override
    public @Nullable String permission() {
        return "skillsplugin.skills";
    }
}
