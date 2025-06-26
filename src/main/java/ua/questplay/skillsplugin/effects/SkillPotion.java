package ua.questplay.skillsplugin.effects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ua.questplay.skillsplugin.SkillsPlugin;

public class SkillPotion {
    private final SkillsPlugin plugin;

    public SkillPotion(SkillsPlugin plugin) {
        this.plugin = plugin;
        startEffectLoop();
    }

    private void startEffectLoop() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    applyEffects(player);
                }
            }
        }.runTaskTimer(plugin, 0, 5L);
    }

    private void applyEffects(Player player) {
        ItemStack offHand = player.getInventory().getItemInOffHand();

        if (plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_speed")) {
            player.addPotionEffect(createPermanentEffect(PotionEffectType.SPEED, 0));
        }
        if (plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_haste")) {
            player.addPotionEffect(createPermanentEffect(PotionEffectType.HASTE, 0));
        }
        if (plugin.getDbManager().hasSkill(player.getUniqueId(), "killer_speed")) {
            player.addPotionEffect(createPermanentEffect(PotionEffectType.SPEED, 0));
        }
        if (plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_hero")) {
            player.addPotionEffect(createPermanentEffect(PotionEffectType.HERO_OF_THE_VILLAGE, 1));
        }

        if (plugin.getDbManager().hasSkill(player.getUniqueId(), "merchant_blessing") && offHand.getType() == Material.TOTEM_OF_UNDYING) {
            player.addPotionEffect(createPermanentEffect(PotionEffectType.HEALTH_BOOST, 2));
        }

        if (plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_specialization") && offHand.getType() != Material.SHIELD) {
            player.addPotionEffect(createPermanentEffect(PotionEffectType.SPEED, 1));
            player.addPotionEffect(createPermanentEffect(PotionEffectType.HASTE, 1));
        }
    }
    private PotionEffect createPermanentEffect(PotionEffectType type, int amplifier) {
        return new PotionEffect(
                type,
                25, // Длительность 1.25 секунды (обновляется каждый тик)
                amplifier,
                true,
                false
        );
    }
}
