package ua.questplay.skillsplugin.effects;

import io.papermc.paper.event.player.PlayerTradeEvent;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillType;

import java.util.HashMap;
import java.util.Map;

public class TraderEffects implements Listener {
    private final SkillsPlugin plugin;
    private final Map<Player, Integer> hitCounter = new HashMap<>();

    public TraderEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (!plugin.getDbManager().hasSkill(player.getUniqueId(), skillTag(SkillType.MERCHANT_LUCK))) return;

        if (Math.random() > plugin.getConfig().getDouble("merchant.chances.luck")) return;

        Material dropType = getDropForOre(block.getType());
        if (dropType == null) return;

        int bonus = (int) (Math.random() * 3) + 2; // 2-4
        ItemStack drop = new ItemStack(dropType, bonus);

        block.getWorld().dropItemNaturally(block.getLocation(), drop);
        block.getWorld().spawnParticle(Particle.CRIT,
                block.getLocation().add(0, 0, 0),
                10, 0.5, 0.5, 0.5);
        if (plugin.getConfig().getBoolean("skills_messages")) {
            player.sendMessage(plugin.formattedFromKey("skill_effects.merchant.luck").replaceText(TextReplacementConfig.builder().matchLiteral("<bonus>").replacement(String.valueOf(bonus)).build()));
        }
    }

    private Material getDropForOre(Material oreType) {
        return switch (oreType) {
            case IRON_ORE, DEEPSLATE_IRON_ORE -> Material.RAW_IRON;
            case COPPER_ORE, DEEPSLATE_COPPER_ORE -> Material.RAW_COPPER;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE -> Material.RAW_GOLD;
            case COAL_ORE, DEEPSLATE_COAL_ORE -> Material.COAL;
            case DIAMOND_ORE, DEEPSLATE_DIAMOND_ORE -> Material.DIAMOND;
            case EMERALD_ORE, DEEPSLATE_EMERALD_ORE -> Material.EMERALD;
            case LAPIS_ORE, DEEPSLATE_LAPIS_ORE -> Material.LAPIS_LAZULI;
            case REDSTONE_ORE, DEEPSLATE_REDSTONE_ORE -> Material.REDSTONE;
            case NETHER_QUARTZ_ORE -> Material.QUARTZ;
            case NETHER_GOLD_ORE -> Material.GOLD_NUGGET;
            case ANCIENT_DEBRIS -> Material.ANCIENT_DEBRIS;
            default -> null;
        };
    }

    @EventHandler
    public void onMerchantRun(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player trader)) return;

        if (!plugin.getDbManager().hasSkill(event.getEntity().getUniqueId(), skillTag(SkillType.MERCHANT_RUN))) return;

        int hits = hitCounter.getOrDefault(trader, 0) + 1;
        hitCounter.put(trader, hits);

        if (hits == 1) {
            trader.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, plugin.getConfig().getInt("merchant.effects.run.duration.effect1") * 20,
                    plugin.getConfig().getInt("merchant.effects.run.strength.effect1")));
            trader.spawnParticle(Particle.DRIPPING_LAVA,
                    trader.getLocation().add(0, 2, 0),
                    5, 0.5, 0.5, 0.5);
        } else if (hits == 4) {
            trader.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, plugin.getConfig().getInt("merchant.effects.run.duration.effect2") * 20,
                    plugin.getConfig().getInt("merchant.effects.run.strength.effect2")));
            trader.spawnParticle(Particle.LAVA,
                    trader.getLocation().add(0, 2, 0),
                    8, 0.5, 0.5, 0.5);
        } else if (hits >= 8) {
            trader.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, plugin.getConfig().getInt("merchant.effects.run.duration.effect3_speed") * 20,
                    plugin.getConfig().getInt("merchant.effects.run.strength.effect3_speed")));
            trader.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, plugin.getConfig().getInt("merchant.effects.run.duration.effect3_resistance") * 20,
                    plugin.getConfig().getInt("merchant.effects.run.strength.effect3_speed")));
            trader.spawnParticle(Particle.ANGRY_VILLAGER,
                    trader.getLocation().add(0, 2, 0),
                    12, 0.5, 0.5, 0.5);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (hitCounter.getOrDefault(trader, 0) == hits) {
                hitCounter.remove(trader);
            }
        }, 5 * 20);
    }

    @EventHandler
    public void onMerchantTrade(PlayerTradeEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getDbManager().hasSkill(player.getUniqueId(), skillTag(SkillType.MERCHANT_EXP))) return;


        if (Math.random() > plugin.getConfig().getDouble("merchant.chances.exp")) return;

        int expGained = 5 + (int)(Math.random() * 21);
        player.giveExp(expGained);

        if (plugin.getConfig().getBoolean("skills_messages")) {
            player.sendMessage(plugin.formattedFromKey("skill_effects.merchant.exp").replaceText(TextReplacementConfig.builder().matchLiteral("<exp>").replacement(String.valueOf(expGained)).build()));
        }
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);

        player.spawnParticle(Particle.HAPPY_VILLAGER,
                player.getLocation().add(0, 1, 0),
                10, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onMerchantBlessing(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!plugin.getDbManager().hasSkill(player.getUniqueId(), skillTag(SkillType.MERCHANT_BLESSING))) return;

        if (Math.random() > plugin.getConfig().getDouble("merchant.chances.blessing")) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.INSTANT_HEALTH, 1, 1));

        if (plugin.getConfig().getBoolean("skills_messages")) {
            player.sendMessage(plugin.formattedFromKey("skill_effects.merchant.blessing").replaceText(TextReplacementConfig.builder().matchLiteral("<health>").replacement(String.valueOf(4)).build()));
        }
    }

    private String skillTag(SkillType skillType) {
        return plugin.getSkillManager().getSkill(skillType).tag();
    }
}
