package ua.questplay.skillsplugin.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.gui.ClassGUI;

import java.util.Collection;
import java.util.List;

public class ClassCommand implements BasicCommand {
    private final SkillsPlugin plugin;

    public ClassCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        Entity sender = commandSourceStack.getExecutor();
        if (!(commandSourceStack.getExecutor() instanceof Player player)) {
            sender.sendMessage(plugin.formattedFromKey("restrict.onlyplayer"));
            return;
        }

        //GUI Components
        Component frameName = Component.text("");
        List<Component> framLore = List.of(Component.text(""));

        Component killerName = plugin.formattedFromKey("class_command.class_gui.killer");
        Component thiefName = plugin.formattedFromKey("class_command.class_gui.thief");
        Component traderName = plugin.formattedFromKey("class_command.class_gui.trader");

        List<Component> killerLore = List.of(plugin.formattedFromKey("class_command.class_gui.lore.killer"));
        List<Component> thiefLore = List.of(plugin.formattedFromKey("class_command.class_gui.lore.thief"));
        List<Component> traderLore = List.of(plugin.formattedFromKey("class_command.class_gui.lore.trader"));

        // Damn. You already have a game class.
        if (plugin.getDbManager().hasClass(player.getUniqueId())) {
            player.sendMessage(plugin.formattedFromKey("class_command.already_have_class"));
            return;
        }


        ClassGUI classgui = new ClassGUI(plugin.getMessageFromKey("class_command.class_gui.title"), 54);
        for (int i = 0; i < 9; i++) {
            classgui.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, framLore);
        }

        for (int i = 45; i < 54; i++) {
            classgui.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, framLore);
        }
        classgui.addItem(9, Material.NETHERITE_SWORD, killerName, killerLore);
        classgui.addItem(10, Material.GOLDEN_BOOTS, thiefName, thiefLore);
        classgui.addItem(11, Material.TOTEM_OF_UNDYING, traderName, traderLore);

        classgui.open(player);
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
        return BasicCommand.super.permission();
    }
}
