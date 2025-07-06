package ua.questplay.skillsplugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;

public class ReloadCommand implements CommandExecutor {
    private final SkillsPlugin plugin;

    public ReloadCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {

        switch (args[0]) {
            case "config" -> {
                plugin.reloadPluginConfig();
                sender.sendMessage(plugin.formattedFromKey("config.reload"));
            }
            case "messages" -> {
                plugin.reloadMessages();
                sender.sendMessage(plugin.formattedFromKey("config.messages"));
            }
            case "all" -> {
                plugin.reloadPluginConfig();
                sender.sendMessage(plugin.formattedFromKey("config.reload"));
                plugin.reloadMessages();
                sender.sendMessage(plugin.formattedFromKey("config.messages"));
            }
            default -> sender.sendMessage(plugin.formattedFromKey("config.wrong"));
        }
        return true;
    }
}
