package ua.questplay.skillsplugin.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.skills.SkillType;

import java.math.BigDecimal;
import java.util.List;

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
                    processSkillPurchase(player, SkillType.KILLER_SPEED, "killer_speed");
                }

                case 10 -> {
                    processSkillPurchase(player, SkillType.KILLER_MURDER, "killer_murder");
                }

                case 11 -> {
                    processSkillPurchase(player, SkillType.KILLER_HASTE, "killer_haste");
                }

                case 12 -> {
                    processSkillPurchase(player, SkillType.KILLER_VAMPIRISM, "killer_vampirism");
                }

                case 13 -> {
                    processSkillPurchase(player, SkillType.KILLER_RECOVERY, "killer_recovery");
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            switch (slot) {
                case 9 -> {
                    processSkillPurchase(player, SkillType.THIEF_SPEED, "thief_speed");
                }

                case 10 -> {
                    processSkillPurchase(player, SkillType.THIEF_HASTE, "thief_haste");
                }

                case 11 -> {
                    processSkillPurchase(player, SkillType.THIEF_EXP, "thief_stole_exp");
                }

                case 12 -> {
                    processSkillPurchase(player, SkillType.THIEF_CAUTION, "thief_caution");
                }

                case 13 -> {
                    processSkillPurchase(player, SkillType.THIEF_SPECIALIZATION, "thief_specialization");
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            switch (slot) {
                case 9 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_LUCK, "merchant_luck");
                }

                case 10 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_EXP, "merchant_exp");
                }

                case 11 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_RUN, "merchant_run");
                }

                case 12 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_HERO, "merchant_hero");
                }

                case 13 -> {
                    processSkillPurchase(player, SkillType.MERCHANT_BLESSING, "merchant_blessing");
                }
            }
        }
    }

    private void processSkillPurchase(Player player, SkillType skillType, String tag) {
        SkillData skill = plugin.getSkillManager().getSkill(skillType);

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
            if (!SkillsPlugin.getEconomyModern().has(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(price))) {
                return false;
            }
        } else {
            if (!SkillsPlugin.getEconomyLegacy().has(player.getName(), price)) {
                return false;
            }
        }
        return true;
    }

    private void takeMoney(Player player, int price) {
        if (SkillsPlugin.getEconomyLegacy() != null) {
            SkillsPlugin.getEconomyLegacy().withdrawPlayer(player.getName(), price);
        } else {
            SkillsPlugin.getEconomyModern().withdraw(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(price));
        }
    }
}
