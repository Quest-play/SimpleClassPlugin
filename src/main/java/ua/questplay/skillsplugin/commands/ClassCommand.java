package ua.questplay.skillsplugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.gui.ClassGUI;

import java.util.Arrays;
import java.util.List;

public class ClassCommand implements CommandExecutor {

    private final SkillsPlugin plugin;

    public ClassCommand(SkillsPlugin  plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageFromKey("restrict.onlyplayer"));
            return true;
        }

        Player player = (Player) sender;

        //GUI Components
        Component frameName = Component.text("");
        List<Component> framLore = Arrays.asList(Component.text(""));

        Component killerName = MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("class_command.class_gui.killer")).decoration(TextDecoration.ITALIC, false);
        Component thiefName = Component.text(plugin.getMessageFromKey("class_command.class_gui.thief")).color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false);
        Component traderName = Component.text(plugin.getMessageFromKey("class_command.class_gui.trader")).color(NamedTextColor.GOLD).decoration(TextDecoration.ITALIC, false);

        List<Component> killerLore = Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("class_command.class_gui.lore.killer")));
        List<Component> thiefLore = Arrays.asList(Component.text(plugin.getMessageFromKey("class_command.class_gui.lore.thief")).color(NamedTextColor.DARK_PURPLE));
        List<Component> traderLore = Arrays.asList(Component.text(plugin.getMessageFromKey("class_command.class_gui.lore.trader")).color(NamedTextColor.DARK_PURPLE));

        // Damn. You already have a game class.
        if (plugin.getDbManager().hasClass(player.getUniqueId())) {
            player.sendMessage(plugin.getMessageFromKey("class_command.already_have_class"));
            return true;
        }


        // Открываем GUI (пока просто текст)
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
        return true;
    }
}
