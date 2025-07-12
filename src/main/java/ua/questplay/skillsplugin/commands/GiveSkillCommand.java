package ua.questplay.skillsplugin.commands;

import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiveSkillCommand implements CommandExecutor {
    private final SkillsPlugin plugin;

    public GiveSkillCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return true;
        }

        if (args.length != 2) {
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(plugin.formattedFromKey("restrict.notfound"));
            return true;
        }

        if (!plugin.getDbManager().inTable(targetPlayer.getUniqueId())) {
            sender.sendMessage(plugin.formattedFromKey("restrict.no_class"));
        }

        String skill = args[1];
        List<String> skill_names = Arrays.asList(plugin.getDbManager().getAllSkillNames());

        if (args[1].isEmpty() || !skill_names.contains(args[1])) {
            sender.sendMessage("");
            return false;
        }


        plugin.getDbManager().addSkill(targetPlayer.getUniqueId(), skill.toLowerCase());
        sender.sendMessage(plugin.formattedFromKey("success").replaceText(TextReplacementConfig.builder().matchLiteral("<player>").replacement(targetPlayer.getName()).build()));

        return true;
    }
}
