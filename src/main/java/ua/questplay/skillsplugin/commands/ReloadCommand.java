package ua.questplay.skillsplugin.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import ua.questplay.skillsplugin.SkillsPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReloadCommand implements BasicCommand {
    private final SkillsPlugin plugin;

    public ReloadCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Entity sender = commandSourceStack.getExecutor();
        if (!(commandSourceStack.getExecutor() instanceof Player player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return;
        }
        switch (args[0]) {
            case "config" -> {
                plugin.reloadPluginConfig();
                player.sendMessage(plugin.formattedFromKey("config.reload"));
            }
            case "messages" -> {
                plugin.reloadMessages();
                player.sendMessage(plugin.formattedFromKey("config.messages"));
            }
            case "all" -> {
                plugin.reloadPluginConfig();
                player.sendMessage(plugin.formattedFromKey("config.reload"));
                plugin.reloadMessages();
                player.sendMessage(plugin.formattedFromKey("config.messages"));
            }
            default -> player.sendMessage(plugin.formattedFromKey("config.wrong"));
        }

    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return suggestions();
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return BasicCommand.super.canUse(sender);
    }

    @Override
    public @Nullable String permission() {
        return "skillsplugin.reload";
    }

    private List<String> suggestions() {
        List<String> suggestions = new ArrayList<>();

        suggestions.add("config");
        suggestions.add("messages");
        suggestions.add("all");

        return suggestions;
    }
}
