package ua.questplay.skillsplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ua.questplay.skillsplugin.SkillsPlugin;

public class ClassGUIListener implements Listener {
    private final SkillsPlugin plugin;

    public ClassGUIListener(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder(false) instanceof ClassGUI gui)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        switch (slot) {
            case 9:
                plugin.getDbManager().savePlayerClass(player.getUniqueId(), "class_killer");
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.assassin_given"));
                inventory.close();
                break;
            case 10:
                plugin.getDbManager().savePlayerClass(player.getUniqueId(),"class_thief");
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.thief_given"));
                inventory.close();
                break;
            case 11:
                plugin.getDbManager().savePlayerClass(player.getUniqueId(),"class_merchant");
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.trader_given"));
                inventory.close();
                break;
            case 12:
                player.sendMessage(gui.getClass().descriptorString());
                break;
        }
    }
}