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

import java.util.Arrays;
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
        SkillsGUI skillsGUI = new SkillsGUI(plugin.formattedFromKey("skills_gui.title"),54);

        Component frameName = Component.text("");
        List<Component> frameLore = List.of(Component.text(""));

        for (int i = 0; i < 9; i++) {
            skillsGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, frameLore);
        }

        for (int i = 45; i < 54; i++) {
            skillsGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, frameLore);
        }

        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_killer")) {
            SkillData killerSpeed = plugin.getSkillManager().getSkill(SkillType.KILLER_SPEED);
            SkillData killerMurder =  plugin.getSkillManager().getSkill(SkillType.KILLER_MURDER);
            SkillData killerHaste = plugin.getSkillManager().getSkill(SkillType.KILLER_HASTE);
            SkillData killerVampirism = plugin.getSkillManager().getSkill(SkillType.KILLER_VAMPIRISM);
            SkillData killerRecovery = plugin.getSkillManager().getSkill(SkillType.KILLER_RECOVERY);


            skillsGUI.addItem(9, killerSpeed.getIcon(), killerSpeed.getName(), killerSpeed.getDescription());
            skillsGUI.addItem(10, killerMurder.getIcon(), killerMurder.getName(), killerMurder.getDescription());
            skillsGUI.addItem(11, killerHaste.getIcon(), killerHaste.getName(), killerHaste.getDescription());
            skillsGUI.addItem(12, killerVampirism.getIcon(), killerVampirism.getName(), killerVampirism.getDescription());
            skillsGUI.addItem(13, killerRecovery.getIcon(), killerRecovery.getName(), killerRecovery.getDescription());
        }
        //-------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            SkillData merchant_luck = plugin.getSkillManager().getSkill(SkillType.MERCHANT_LUCK);
            SkillData merchant_exp =  plugin.getSkillManager().getSkill(SkillType.MERCHANT_EXP);
            SkillData merchant_run = plugin.getSkillManager().getSkill(SkillType.MERCHANT_RUN);
            SkillData merchant_hero = plugin.getSkillManager().getSkill(SkillType.MERCHANT_HERO);
            SkillData merchant_blessing = plugin.getSkillManager().getSkill(SkillType.MERCHANT_BLESSING);


            skillsGUI.addItem(9, merchant_luck.getIcon(), merchant_luck.getName(), merchant_luck.getDescription());
            skillsGUI.addItem(10, merchant_exp.getIcon(), merchant_exp.getName(), merchant_exp.getDescription());
            skillsGUI.addItem(11, merchant_run.getIcon(), merchant_run.getName(), merchant_run.getDescription());
            skillsGUI.addItem(12, merchant_hero.getIcon(), merchant_hero.getName(), merchant_hero.getDescription());
            skillsGUI.addItem(13, merchant_blessing.getIcon(), merchant_blessing.getName(), merchant_blessing.getDescription());
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            SkillData thief_speed = plugin.getSkillManager().getSkill(SkillType.THIEF_SPEED);
            SkillData thief_haste =  plugin.getSkillManager().getSkill(SkillType.THIEF_HASTE);
            SkillData thief_exp = plugin.getSkillManager().getSkill(SkillType.THIEF_EXP);
            SkillData thief_caution = plugin.getSkillManager().getSkill(SkillType.THIEF_CAUTION);
            SkillData thief_specialization = plugin.getSkillManager().getSkill(SkillType.THIEF_SPECIALIZATION);


            skillsGUI.addItem(9, thief_speed.getIcon(), thief_speed.getName(), thief_speed.getDescription());
            skillsGUI.addItem(10, thief_haste.getIcon(), thief_haste.getName(), thief_haste.getDescription());
            skillsGUI.addItem(11, thief_exp.getIcon(), thief_exp.getName(), thief_exp.getDescription());
            skillsGUI.addItem(12, thief_caution.getIcon(), thief_caution.getName(), thief_caution.getDescription());
            skillsGUI.addItem(13, thief_specialization.getIcon(), thief_specialization.getName(), thief_specialization.getDescription());
        }


        skillsGUI.open(player);

        return true;
    }

}
