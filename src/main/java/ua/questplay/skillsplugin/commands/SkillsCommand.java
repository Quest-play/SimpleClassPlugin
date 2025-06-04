package ua.questplay.skillsplugin.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillData;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.gui.SkillsGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SkillsCommand implements CommandExecutor {
    private final SkillsPlugin plugin;

    public SkillsCommand(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageFromKey("restrict.onlyplayer"));
            return true;
        }
        Player player = (Player) sender;
        SkillsGUI skillsGUI = new SkillsGUI(MiniMessage.miniMessage().deserialize(""),54);

        Component frameName = Component.text("");
        List<Component> framLore = Arrays.asList(Component.text(""));

        for (int i = 0; i < 9; i++) {
            skillsGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, framLore);
        }

        for (int i = 45; i < 54; i++) {
            skillsGUI.addItem(i, Material.GRAY_STAINED_GLASS_PANE, frameName, framLore);
        }

        List<SkillData> skills = getSkillsForPlayer(player);
        int[] skillSlots = {9, 10, 11, 12, 13};
        for (int i = 0; i < Math.min(skills.size(), skillSlots.length); i++) {
            if (plugin.getDbManager().hasSkill(player.getUniqueId(), "skill_thief_speed")) {
                skillsGUI.addItem(i, skills.getFirst().getIcon(), skills.getFirst().getName(), skills.getFirst().getDescription());
            } else {
                skillsGUI.addItem(i, skills.getFirst().getIcon(), skills.getFirst().getName(), skills.getFirst().getDescription());
            }
        }

        skillsGUI.open(player);

        return true;
    }

    public List<SkillData> getSkillsForPlayer(Player player) {
        List<SkillData> skills = new ArrayList<>();

        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_thief")) {
            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.speed")),
                    Material.FEATHER,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.speed_desc"))),
                    "skill_thief_speed",
                    Arrays.asList(
                            new ItemStack(Material.SUGAR, 64), // 3 зелья скорости I
                            new ItemStack(Material.GOLD_INGOT, 16)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.haste")),
                    Material.GOLDEN_SWORD,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.haste_desc"))),
                    "skill_thief_haste",
                    Arrays.asList(
                            new ItemStack(Material.SUGAR, 96),
                            new ItemStack(Material.BLAZE_POWDER, 32),
                            new ItemStack(Material.GOLDEN_APPLE, 1)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.stole_exp")),
                    Material.EXPERIENCE_BOTTLE,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.stole_exp_desc"))),
                    "skill_thief_stole_exp",
                    Arrays.asList(
                            new ItemStack(Material.EXPERIENCE_BOTTLE, 64),
                            new ItemStack(Material.EMERALD, 12),
                            new ItemStack(Material.DIAMOND, 2),
                            new ItemStack(Material.COPPER_INGOT, 64)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.caution")),
                    Material.LEATHER_BOOTS,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.caution_desc"))),
                    "skill_thief_caution",
                    Arrays.asList(
                            new ItemStack(Material.ANCIENT_DEBRIS, 3),
                            new ItemStack(Material.GOLDEN_APPLE, 10)
                    )

            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.specialization")),
                    Material.WOODEN_SWORD,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.thief.specialization_desc"))),
                    "skill_thief_specialization",
                    Arrays.asList(
                            new ItemStack(Material.SUGAR, 256),
                            new ItemStack(Material.BLAZE_POWDER, 128),
                            new ItemStack(Material.NETHERITE_INGOT, 3),
                            new ItemStack(Material.GOLDEN_APPLE, 4)
                    )
            ));
        }

        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_assassin")) {
            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.speed")),
                    Material.BLAZE_POWDER,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.speed_desc"))),
                    "skill_assassin_speed",
                    Arrays.asList(
                            new ItemStack(Material.SUGAR,16),
                            new ItemStack(Material.REDSTONE, 32)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.murder")),
                    Material.REDSTONE,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.murder_desc"))),
                    "skill_assassin_murder",
                    Arrays.asList(
                            new ItemStack(Material.BLAZE_POWDER,48),
                            new ItemStack(Material.DIAMOND,6)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.haste")),
                    Material.NETHERITE_SWORD,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.haste_desc"))),
                    "skill_assassin_haste",
                    Arrays.asList(
                            new ItemStack(Material.DIAMOND, 8),
                            new ItemStack(Material.GOLD_INGOT, 32),
                            new ItemStack(Material.EMERALD, 64)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.vampirism")),
                    Material.TIPPED_ARROW,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.vampirism_desc"))),
                    "skill_assassin_vampirism",
                    Arrays.asList(
                            new ItemStack(Material.ANCIENT_DEBRIS,3),
                            new ItemStack(Material.GOLDEN_APPLE, 16)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.recovery")),
                    Material.NETHER_STAR,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.assassin.recovery_desc"))),
                    "skill_assassin_recovery",
                    Arrays.asList(
                            new ItemStack(Material.NETHER_STAR,1),
                            new ItemStack(Material.NETHERITE_INGOT,3),
                            new ItemStack(Material.GOLDEN_APPLE, 48)
                    )
            ));
        }

        if (plugin.getDbManager().hasPlayerClass(player.getUniqueId(), "class_trader")) {
            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.luck")),
                    Material.EMERALD,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.luck_desc"))),
                    "class_trader_luck",
                    Arrays.asList(
                            new ItemStack(Material.DIAMOND,1),
                            new ItemStack(Material.EMERALD,32)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.exp")),
                    Material.EXPERIENCE_BOTTLE,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.exp_desc"))),
                    "class_trader_exp",
                    Arrays.asList(
                            new ItemStack(Material.EMERALD_BLOCK, 16),
                            new ItemStack(Material.DIAMOND,8),
                            new ItemStack(Material.EXPERIENCE_BOTTLE,32)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.run")),
                    Material.FEATHER,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.run_desc"))),
                    "class_trader_run",
                    Arrays.asList(
                            new ItemStack(Material.SUGAR, 1024),
                            new ItemStack(Material.EMERALD_BLOCK,32),
                            new ItemStack(Material.GOLDEN_APPLE, 8)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.hero")),
                    Material.EMERALD_BLOCK,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.hero_desc"))),
                    "class_trader_hero",
                    Arrays.asList(
                            new ItemStack(Material.ANCIENT_DEBRIS,2),
                            new ItemStack(Material.EMERALD_BLOCK,128)
                    )
            ));

            skills.add(new SkillData(
                    MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.blessing")),
                    Material.TOTEM_OF_UNDYING,
                    Arrays.asList(MiniMessage.miniMessage().deserialize(plugin.getMessageFromKey("skills_gui.skills.trader.blessing_desc"))),
                    "class_trader_blessing",
                    Arrays.asList(
                            new ItemStack(Material.TOTEM_OF_UNDYING,1),
                            new ItemStack(Material.NETHERITE_INGOT, 3),
                            new ItemStack(Material.NETHER_STAR,1)
                    )
            ));
        }
        return skills;
    }
}
