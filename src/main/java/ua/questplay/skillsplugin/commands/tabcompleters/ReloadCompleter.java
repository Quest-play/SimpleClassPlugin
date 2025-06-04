package ua.questplay.skillsplugin.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ReloadCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> suggestions = new ArrayList<>();

        if (command.getName().equalsIgnoreCase("reload")) {
            if (args.length == 1) { // First argument should be hand or gui
                suggestions.add("config");
                suggestions.add("messages");
                suggestions.add("all");
            }
        }

        return suggestions;
    }
}
