package ua.questplay.skillsplugin.commands.tabcompleters;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ua.questplay.skillsplugin.SkillsPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GiveSkillCompleter implements TabCompleter {
    private final SkillsPlugin plugin;
    private final List<String> player_names = new ArrayList<>();

    public GiveSkillCompleter(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Collection<? extends Player> players = plugin.getServer().getOnlinePlayers();
        for (Player player : players) {
            player_names.add(player.getName());
        }

        switch (args.length) {
            case  1 -> {
                return player_names;
            }
            case 2 -> {
                return List.of(plugin.getDbManager().getAllSkillNames());
            }
        }

        player_names.clear();
        return List.of("");
    }
}
