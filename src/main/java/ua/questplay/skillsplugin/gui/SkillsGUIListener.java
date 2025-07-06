package ua.questplay.skillsplugin.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.skills.SkillType;

import java.math.BigDecimal;

public class SkillsGUIListener implements Listener {
    private final SkillsPlugin plugin;


    public SkillsGUIListener(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof SkillsGUI gui)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_killer")) {
            switch (slot) {
                case 9 -> {
                    processSkillPurchase(player, SkillType.KILLER_SPEED);
                }

                case 10 -> {
                    processSkillPurchase(player, SkillType.KILLER_MURDER);
                }

                case 11 -> {
                    processSkillPurchase(player, SkillType.KILLER_HASTE);
                }

                case 12 -> {
                    processSkillPurchase(player, SkillType.KILLER_VAMPIRISM);
                }

                case 13 -> {
                    processSkillPurchase(player, SkillType.KILLER_RECOVERY);
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            switch (slot) {
                case 9 -> {
                    processSkillPurchase(player, SkillType.THIEF_SPEED);
                }

                case 10 -> {
                    processSkillPurchase(player, SkillType.THIEF_HASTE);
                }

                case 11 -> {
                    processSkillPurchase(player, SkillType.THIEF_EXP);
                }

                case 12 -> {
                    processSkillPurchase(player, SkillType.THIEF_CAUTION);
                }

                case 13 -> {
                    processSkillPurchase(player, SkillType.THIEF_SPECIALIZATION);
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            switch (slot) {
                case 9 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_LUCK);
                }

                case 10 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_EXP);
                }

                case 11 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_RUN);
                }

                case 12 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_HERO);
                }

                case 13 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_BLESSING);
                }
            }
        }
    }

    private void processSkillPurchase(Player player, SkillType skillType) {
        SkillData skill = plugin.getSkillManager().getSkill(skillType);
        String tag = plugin.getSkillManager().getSkill(skillType).tag();

        if (plugin.getDbManager().hasSkill(player.getUniqueId(), tag)) {
            player.sendMessage(plugin.formattedFromKey("skills_gui.already_have"));
            return;
        }


        if (!hasMoney(player, skill.price())) {
            player.sendMessage(plugin.formattedFromKey("skills_gui.no_res"));
            return;
        }


        takeMoney(player, skill.price());
        plugin.getDbManager().addSkill(player.getUniqueId(), tag);


        player.sendMessage(plugin.formattedFromKey("skills_gui.purchased"));
        player.closeInventory();
    }

    private boolean hasMoney(@NotNull Player player, int price) {
        if (SkillsPlugin.getEconomyModern() != null) {
            return SkillsPlugin.getEconomyModern().has(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(price));
        } else {
            return SkillsPlugin.getEconomyLegacy().has(player.getName(), price);
        }
    }

    private void takeMoney(Player player, int price) {
        if (SkillsPlugin.getEconomyLegacy() != null) {
            SkillsPlugin.getEconomyLegacy().withdrawPlayer(player.getName(), price);
        } else {
            SkillsPlugin.getEconomyModern().withdraw(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(price));
        }
    }
}
