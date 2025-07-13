package ua.questplay.skillsplugin.effects;

import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillType;

import java.math.BigDecimal;

public class ThiefEffects implements Listener {
    private final SkillsPlugin plugin;

    public ThiefEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onStealExp(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player thief)) return;

        if (!(event.getEntity() instanceof Player target)) return;

        if (!plugin.getDbManager().hasSkill(thief.getUniqueId(), skillTag(SkillType.THIEF_EXP))) return;

        if (Math.random() > plugin.getConfig().getDouble("thief.chances.stole_exp")) return;

        int stolenExp = (int) (Math.random() * 6) + 5;
        if (target.getExp() > 0) {
            target.giveExp(-stolenExp);
            thief.giveExp(stolenExp);
            if (plugin.getConfig().getBoolean("skills_messages")) {
                thief.sendActionBar(plugin.formattedFromKey("skill_effects.thief.stole_exp").replaceText(TextReplacementConfig.builder().matchLiteral("<stolen_exp>").replacement(String.valueOf(stolenExp)).build()));
            }
            thief.spawnParticle(Particle.ASH,
                    thief.getLocation().add(0, 2, 0),
                    12, 0.5, 0.5, 0.5);
        }

    }

    @EventHandler
    public void onThiefCaution(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!plugin.getDbManager().hasSkill(player.getUniqueId(), skillTag(SkillType.THIEF_CAUTION))) return;
        if (Math.random() > plugin.getConfig().getDouble("thief.chances.caution")) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                plugin.getConfig().getInt("thief.effects.duration.resistance") * 20,
                plugin.getConfig().getInt("thief.effects.strength.resistance")
        ));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            player.sendActionBar(plugin.formattedFromKey("skill_effects.thief.caution"));
        }
        event.getEntity().getWorld().spawnParticle(Particle.EFFECT,
                event.getEntity().getLocation().add(0, 2, 0),
                12, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onThiefMoney(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player thief)) return;

        if (!(event.getEntity() instanceof Player target)) return;

        if (thief.equals(target)) return;

        if (!plugin.getDbManager().hasSkill(thief.getUniqueId(), skillTag(SkillType.THIEF_MONEY))) return;

        if (Math.random() > plugin.getConfig().getDouble("thief.chances.money")) return;

        double targetBalance = getMoney(target).doubleValue();
        if (targetBalance < 1) {
            return;
        }

        double stolenAmount = targetBalance * 0.05;

        if (stolenAmount < 1) {
            stolenAmount = 1;
        }

        if (!hasMoney(target, stolenAmount)) {
            return;
        }
        takeMoney(target, stolenAmount);
        giveMoney(thief, stolenAmount);

        if (!plugin.getConfig().getBoolean("skills_messages")) return;
        thief.sendActionBar(plugin.formattedFromKey("skill_effects.thief.money_thief").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(stolenAmount)).build()));
        target.sendMessage(plugin.formattedFromKey("skill_effects.thief.money_target").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(stolenAmount)).build()));
    }

    private String skillTag(SkillType skillType) {
        return plugin.getSkillManager().getSkill(skillType).tag();
    }

    private Number getMoney(Player player) {
        if (SkillsPlugin.getEconomyLegacy() != null) {
            return SkillsPlugin.getEconomyLegacy().getBalance(player.getName());
        } else {
            return SkillsPlugin.getEconomyModern().balance(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId());
        }
    }

    private boolean hasMoney(@NotNull Player player, double price) {
        if (SkillsPlugin.getEconomyModern() != null) {
            return SkillsPlugin.getEconomyModern().has(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(price));
        } else {
            return SkillsPlugin.getEconomyLegacy().has(player.getName(), price);
        }
    }

    private void takeMoney(Player player, double price) {
        if (SkillsPlugin.getEconomyLegacy() != null) {
            SkillsPlugin.getEconomyLegacy().withdrawPlayer(player.getName(), price);
        } else {
            SkillsPlugin.getEconomyModern().withdraw(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(price));
        }
    }

    private void giveMoney(Player player, double money) {
        if (SkillsPlugin.getEconomyLegacy() != null) {
            SkillsPlugin.getEconomyLegacy().depositPlayer(player.getName(), money);
        } else {
            SkillsPlugin.getEconomyModern().deposit(SkillsPlugin.getEconomyModern().getName(), player.getUniqueId(), BigDecimal.valueOf(money));
        }
    }
}
