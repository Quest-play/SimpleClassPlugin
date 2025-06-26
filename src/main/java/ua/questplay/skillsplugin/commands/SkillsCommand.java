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
            SkillData killerSpeed = plugin.getSkillManager().getSkill(SkillType.KILLER_SPEED);
            SkillData killerMurder =  plugin.getSkillManager().getSkill(SkillType.KILLER_MURDER);
            SkillData killerHaste = plugin.getSkillManager().getSkill(SkillType.KILLER_HASTE);
            SkillData killerVampirism = plugin.getSkillManager().getSkill(SkillType.KILLER_VAMPIRISM);
            SkillData killerRecovery = plugin.getSkillManager().getSkill(SkillType.KILLER_RECOVERY);


            skillsGUI.addItem(9, killerSpeed.icon(), killerSpeed.name(), killerSpeed.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "killer_speed"));
            skillsGUI.addItem(10, killerMurder.icon(), killerMurder.name(), killerMurder.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "killer_murder"));
            skillsGUI.addItem(11, killerHaste.icon(), killerHaste.name(), killerHaste.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "killer_haste"));
            skillsGUI.addItem(12, killerVampirism.icon(), killerVampirism.name(), killerVampirism.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "killer_vampirism"));
            skillsGUI.addItem(13, killerRecovery.icon(), killerRecovery.name(), killerRecovery.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "killer_recovery"));
        }
        //-------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            SkillData merchant_luck = plugin.getSkillManager().getSkill(SkillType.MERCHANT_LUCK);
            SkillData merchant_exp =  plugin.getSkillManager().getSkill(SkillType.MERCHANT_EXP);
            SkillData merchant_run = plugin.getSkillManager().getSkill(SkillType.MERCHANT_RUN);
            SkillData merchant_hero = plugin.getSkillManager().getSkill(SkillType.MERCHANT_HERO);
            SkillData merchant_blessing = plugin.getSkillManager().getSkill(SkillType.MERCHANT_BLESSING);


            skillsGUI.addItem(9, merchant_luck.icon(), merchant_luck.name(), merchant_luck.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_luck"));
            skillsGUI.addItem(10, merchant_exp.icon(), merchant_exp.name(), merchant_exp.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_exp"));
            skillsGUI.addItem(11, merchant_run.icon(), merchant_run.name(), merchant_run.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_run"));
            skillsGUI.addItem(12, merchant_hero.icon(), merchant_hero.name(), merchant_hero.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_hero"));
            skillsGUI.addItem(13, merchant_blessing.icon(), merchant_blessing.name(), merchant_blessing.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_blessing"));
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            SkillData thief_speed = plugin.getSkillManager().getSkill(SkillType.THIEF_SPEED);
            SkillData thief_haste =  plugin.getSkillManager().getSkill(SkillType.THIEF_HASTE);
            SkillData thief_exp = plugin.getSkillManager().getSkill(SkillType.THIEF_EXP);
            SkillData thief_caution = plugin.getSkillManager().getSkill(SkillType.THIEF_CAUTION);
            SkillData thief_specialization = plugin.getSkillManager().getSkill(SkillType.THIEF_SPECIALIZATION);


            skillsGUI.addItem(9, thief_speed.icon(), thief_speed.name(), thief_speed.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_speed"));
            skillsGUI.addItem(10, thief_haste.icon(), thief_haste.name(), thief_haste.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_haste"));
            skillsGUI.addItem(11, thief_exp.icon(), thief_exp.name(), thief_exp.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_stole_exp"));
            skillsGUI.addItem(12, thief_caution.icon(), thief_caution.name(), thief_caution.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_caution"));
            skillsGUI.addItem(13, thief_specialization.icon(), thief_specialization.name(), thief_specialization.description(), plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_specialization"));
        }


        skillsGUI.open(player);

        return true;
    }

}
