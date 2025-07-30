package ua.questplay.skillsplugin.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import ua.questplay.skillsplugin.SkillsPlugin;

import java.util.Collection;

public class ResetClassCommand implements BasicCommand {
    private final SkillsPlugin plugin;

    public ResetClassCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Entity sender = commandSourceStack.getExecutor();
        if (!(commandSourceStack.getExecutor() instanceof Player player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return;
        }

        final Component message = plugin.formattedFromKey("reset.message");

        final Component yesButton = Component.text()
                .append(plugin.formattedFromKey("reset.yes_"))
                .hoverEvent(HoverEvent.showText(plugin.formattedFromKey("reset.yes_message")))
                .clickEvent(ClickEvent.callback(callback -> {
                    resetClass(player);
                    player.sendMessage(plugin.formattedFromKey("reset.success_delete"));
                }))
                .build();

        final Component noButton = Component.text()
                .append(plugin.formattedFromKey("reset.no_"))
                .hoverEvent(HoverEvent.showText(plugin.formattedFromKey("reset.no_message")))
                .clickEvent(ClickEvent.callback(audience -> {
                    audience.sendMessage(plugin.formattedFromKey("reset.canceled"));
                }))
                .build();

        Component fullMessage = Component.text()
                .append(message)
                .append(Component.newline())
                .append(yesButton)
                .append(Component.text("  /  "))
                .append(noButton)
                .build();

        player.sendMessage(fullMessage);
    }

    @Override
    public Collection<String> suggest(CommandSourceStack commandSourceStack, String[] args) {
        return BasicCommand.super.suggest(commandSourceStack, args);
    }

    @Override
    public boolean canUse(CommandSender sender) {
        return BasicCommand.super.canUse(sender);
    }

    @Override
    public @Nullable String permission() {
        return "skillsplugin.reset_command";
    }

    private void removeMoney(Player player) {
        if (SkillsPlugin.getEconomyLegacy() != null) {
            SkillsPlugin.getEconomyLegacy().withdrawPlayer(player, SkillsPlugin.getEconomyLegacy().getBalance(player));
        } else {
            SkillsPlugin.getEconomyModern().withdraw(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), SkillsPlugin.getEconomyModern().balance(player.getName(), player.getUniqueId()));
        }
    }

    private void resetClass(Player player) {
        plugin.getDbManager().deletePlayerData(player.getUniqueId());
        removeMoney(player);
    }
}
