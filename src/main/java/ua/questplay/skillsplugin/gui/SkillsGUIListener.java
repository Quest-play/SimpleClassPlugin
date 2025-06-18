package ua.questplay.skillsplugin.gui;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.skills.SkillType;

import java.util.List;

public class SkillsGUIListener implements Listener {
    private final SkillsPlugin plugin;


    public SkillsGUIListener(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();

        Component frameName = Component.text("");
        List<Component> framLore = List.of(Component.text(""));

        if (!(inventory.getHolder(false) instanceof SkillsGUI gui)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        PriceGUI priceGUI = new PriceGUI(plugin.formattedFromKey("price.title"), 27);
        for (int i = 0; i < 9; i++) {
            priceGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, framLore);
        }

        for (int i = 18; i < 27; i++) {
            priceGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, framLore);
        }
        int slot = event.getSlot();
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_killer")) {
            switch (slot) {
                case 9 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.KILLER_SPEED);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.KILLER_SPEED, "killer_speed");
                    }
                }

                case 10 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.KILLER_MURDER);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.KILLER_MURDER, "killer_murder");
                    }
                }

                case 11 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.KILLER_HASTE);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.KILLER_HASTE, "killer_haste");
                    }
                }

                case 12 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.KILLER_VAMPIRISM);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.KILLER_VAMPIRISM, "killer_vampirism");
                    }
                }

                case 13 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.KILLER_RECOVERY);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.KILLER_RECOVERY, "killer_recovery");
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            switch (slot) {
                case 9 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.THIEF_SPEED);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.THIEF_SPEED, "thief_speed");
                    }
                }

                case 10 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.THIEF_HASTE);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.THIEF_HASTE, "thief_haste");
                    }
                }

                case 11 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.THIEF_EXP);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.THIEF_EXP, "thief_stole_exp");
                    }
                }

                case 12 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.THIEF_CAUTION);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.THIEF_CAUTION, "thief_caution");
                    }
                }

                case 13 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.THIEF_SPECIALIZATION);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.THIEF_SPECIALIZATION, "thief_specialization");
                    }
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------
        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_merchant")) {
            switch (slot) {
                case 9 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.MERCHANT_LUCK);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.MERCHANT_LUCK, "merchant_luck");
                    }
                }

                case 10 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.MERCHANT_EXP);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.MERCHANT_EXP, "merchant_exp");
                    }
                }

                case 11 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.MERCHANT_RUN);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.MERCHANT_RUN, "merchant_run");
                    }
                }

                case 12 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.MERCHANT_HERO);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.MERCHANT_HERO, "merchant_hero");
                    }
                }

                case 13 -> {
                    if (event.isRightClick()) {
                        int pos = 9;
                        SkillData skillData = plugin.getSkillManager().getSkill(SkillType.MERCHANT_BLESSING);
                        for (int i = 0; i < skillData.price().size(); i++) {
                            priceGUI.addItemCount(pos, skillData.price().get(i).getType(), Component.text(""), List.of(Component.text("")), skillData.price().get(i).getAmount());
                            pos++;
                        }
                        priceGUI.open(player);
                    } else {
                        processSkillPurchase(player, SkillType.MERCHANT_BLESSING, "merchant_blessing");
                    }
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

        if (!hasRequiredResources(player, skill.price())) {
            player.sendMessage(plugin.formattedFromKey("skills_gui.no_res"));
            return;
        }

        takeResources(player, skill.price());
        plugin.getDbManager().addSkill(player.getUniqueId(), tag);


        player.sendMessage(plugin.formattedFromKey("skills_gui.purchased"));
        player.closeInventory();
    }

    private boolean hasRequiredResources(Player player, List<ItemStack> requiredItems) {
        for (ItemStack required : requiredItems) {
            if (!player.getInventory().containsAtLeast(required, required.getAmount())) {
                return false;
            }
        }
        return true;
    }

    private void takeResources(Player player, List<ItemStack> price) {
        for (ItemStack cost : price) {
            player.getInventory().removeItem(cost);
        }
    }
}
