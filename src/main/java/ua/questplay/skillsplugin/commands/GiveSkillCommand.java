package ua.questplay.skillsplugin.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import ua.questplay.skillsplugin.SkillsPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class GiveSkillCommand implements BasicCommand {
    private final SkillsPlugin plugin;
    private final List<String> player_names = new ArrayList<>();

    public GiveSkillCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return;
        }

        if (args.length != 2) {
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);

        if (targetPlayer == null) {
            sender.sendMessage(plugin.formattedFromKey("restrict.notfound"));
            return;
        }

        if (!plugin.getDbManager().inTable(targetPlayer.getUniqueId())) {
            sender.sendMessage(plugin.formattedFromKey("restrict.no_class"));
        }

        String skill = args[1];
        List<String> skill_names = Arrays.asList(plugin.getDbManager().getAllSkillNames());

        if (args[1].isEmpty() || !skill_names.contains(args[1])) {
            sender.sendMessage("");
            return;
        }


        plugin.getDbManager().addSkill(targetPlayer.getUniqueId(), skill.toLowerCase());
        sender.sendMessage(plugin.formattedFromKey("success").replaceText(TextReplacementConfig.builder().matchLiteral("<player>").replacement(targetPlayer.getName()).build()));
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            player_names.add(player.getName());
        }

        switch (args.length) {
            case  0 -> {
                return player_names;
            }
            case 1 -> {
                return List.of(plugin.getDbManager().getAllSkillNames());
            }
        }

        player_names.clear();
        return List.of("");
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return BasicCommand.super.canUse(sender);
    }

    @Override
    public @Nullable String permission() {
        return "skillsplugin.give_skill";
    }
}
