package ua.questplay.skillsplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillType;

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
                plugin.getDbManager().addSkill(player.getUniqueId(), plugin.getSkillManager().getSkill(SkillType.KILLER_MONEY).tag());
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.assassin_given"));
                inventory.close();
                break;
            case 10:
                plugin.getDbManager().savePlayerClass(player.getUniqueId(),"class_thief");
                plugin.getDbManager().addSkill(player.getUniqueId(), plugin.getSkillManager().getSkill(SkillType.THIEF_MONEY).tag());
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.thief_given"));
                inventory.close();
                break;
            case 11:
                plugin.getDbManager().savePlayerClass(player.getUniqueId(),"class_merchant");
                plugin.getDbManager().addSkill(player.getUniqueId(), plugin.getSkillManager().getSkill(SkillType.MERCHANT_MONEY).tag());
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.trader_given"));
                inventory.close();
                break;
            case 12:
                plugin.getDbManager().savePlayerClass(player.getUniqueId(), "class_tank");
                plugin.getDbManager().addSkill(player.getUniqueId(), plugin.getSkillManager().getSkill(SkillType.TANK_MONEY).tag());
                player.sendMessage(plugin.formattedFromKey("class_command.class_list.tank_given"));
                inventory.close();
                break;
        }
    }
}