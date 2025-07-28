package ua.questplay.skillsplugin.effects;

import net.kyori.adventure.text.TextReplacementConfig;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillerEffects implements Listener {
    private final SkillsPlugin plugin;
    private final ClanManager clanManager = SkillsPlugin.getInstance().getSimpleClans().getClanManager();
    private final Map<UUID, Long> cooldowns = new HashMap<>();


    private static final long COOLDOWN_MS = 3 * 60 * 1000;

    public KillerEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKillerMurder(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), skillTag(SkillType.KILLER_MURDER))) return;

        if (!(event.getEntity() instanceof  Player victim)) return;

        ClanPlayer attackerClan = clanManager.getClanPlayer(attacker);
        ClanPlayer victimClan = clanManager.getClanPlayer(victim);

        if (attackerClan != null && victimClan != null) {
            if (attackerClan.getClan().equals(victimClan.getClan())) return;
        }

        if (Math.random() > plugin.getConfig().getDouble("killer.chances.murder")) return;

        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                plugin.getConfig().getInt("killer.effects.skills.murder") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.murder_strength"),
                true,
                false
        ));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            attacker.sendActionBar(plugin.formattedFromKey("skill_effects.killer.murder"));
        }
        event.getDamager().getWorld().spawnParticle(Particle.ANGRY_VILLAGER,
                event.getDamager().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onKillerVampirism(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;


        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), skillTag(SkillType.KILLER_VAMPIRISM))) return;

        if (!(event.getEntity() instanceof  Player victim)) return;

        ClanPlayer attackerClan = clanManager.getClanPlayer(attacker);
        ClanPlayer victimClan = clanManager.getClanPlayer(victim);

        if (attackerClan != null && victimClan != null) {
            if (attackerClan.getClan().equals(victimClan.getClan())) return;
        }

        if (Math.random() > plugin.getConfig().getDouble("killer.chances.vampirism")) return;

        float damage = 1.0f;

        victim.setHealth(Math.max(0, victim.getHealth() - damage));

        attacker.setHealth(Math.min(20, attacker.getHealth() + damage / 2));

        if (plugin.getConfig().getBoolean("skills_messages")) {
            attacker.sendActionBar(plugin.formattedFromKey("skill_effects.killer.vampirism"));
        }

        event.getDamager().getWorld().spawnParticle(Particle.HEART,
                event.getDamager().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onKillerHaste(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), skillTag(SkillType.KILLER_HASTE))) return;

        if (!(event.getEntity() instanceof  Player victim)) return;

        ClanPlayer attackerClan = clanManager.getClanPlayer(attacker);
        ClanPlayer victimClan = clanManager.getClanPlayer(victim);

        if (attackerClan != null && victimClan != null) {
            if (attackerClan.getClan().equals(victimClan.getClan())) return;
        }


        if (Math.random() > plugin.getConfig().getDouble("killer.chances.haste")) return;

        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                plugin.getConfig().getInt("killer.effects.skills.haste") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.haste_strength"),
                true,
                false
        ));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            attacker.sendActionBar(plugin.formattedFromKey("skill_effects.killer.haste"));
        }
        event.getDamager().getWorld().spawnParticle(Particle.LAVA,
                event.getDamager().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player victim = event.getPlayer();
        if (killer == null) return;

        if (!plugin.getDbManager().hasSkill(killer.getUniqueId(), skillTag(SkillType.KILLER_RECOVERY))) return;

        ClanPlayer attackerClan = clanManager.getClanPlayer(killer);
        ClanPlayer victimClan = clanManager.getClanPlayer(victim);

        if (attackerClan != null && victimClan != null) {
            if (attackerClan.getClan().equals(victimClan.getClan())) return;
        }

        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                plugin.getConfig().getInt("killer.effects.skills.recovery.speed") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.recovery.speed_strength"),
                true,
                false
        ));

        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                plugin.getConfig().getInt("killer.effects.skills.recovery.resistance") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.recovery.resistance_strength"),
                true,
                false
        ));

        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                plugin.getConfig().getInt("killer.effects.skills.recovery.regeneration") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.recovery.regeneration_strength"),
                true,
                false
        ));

        if (plugin.getConfig().getBoolean("skills_messages")) {
            killer.sendActionBar(plugin.formattedFromKey("skill_effects.killer.recovery"));
        }
        killer.playSound(killer.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

        killer.spawnParticle(Particle.TOTEM_OF_UNDYING, killer.getLocation(), 30);
        event.getEntity().getWorld().spawnParticle(Particle.TRIAL_OMEN,
                event.getEntity().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onKillerMoneyFromPlayer(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player victim = event.getPlayer();

        if (killer == null) return;

        UUID killerId = killer.getUniqueId();

        if (!plugin.getDbManager().hasSkill(killer.getUniqueId(), skillTag(SkillType.KILLER_MONEY))) return;

        ClanPlayer attackerClan = clanManager.getClanPlayer(killer);
        ClanPlayer victimClan = clanManager.getClanPlayer(victim);

        if (attackerClan != null && victimClan != null) {
            if (attackerClan.getClan().equals(victimClan.getClan())) return;
        }


        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(killerId, 0L);

        if (currentTime - lastUsed < COOLDOWN_MS) {
            long remainingSeconds = (COOLDOWN_MS - (currentTime - lastUsed)) / 1000;
            killer.sendActionBar(plugin.formattedFromKey("skill_effects.killer.money_cd").replaceText(TextReplacementConfig.builder().matchLiteral("<time>").replacement(String.valueOf(remainingSeconds)).build())
            );
            return;
        }

        Number targetBalance = getMoney(victim);
        if (targetBalance.doubleValue() < 1) {
            return;
        }

        double stolenAmount = targetBalance.doubleValue() * 0.3;

        if (stolenAmount < 1) {
            stolenAmount = 1;
        }

        if (!hasMoney(victim, stolenAmount)) {
            return;
        }
        takeMoney(victim, stolenAmount);
        giveMoney(killer, stolenAmount);

        cooldowns.put(killerId, currentTime);

        if (!plugin.getConfig().getBoolean("skills_messages")) return;

        killer.sendMessage(plugin.formattedFromKey("skill_effects.thief.money_thief").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(stolenAmount)).build()));
        victim.sendMessage(plugin.formattedFromKey("skill_effects.thief.money_target").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(stolenAmount)).build()));
    }

    @EventHandler
    public void onKillerMoneyFromMobs(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null) return;

        if (!plugin.getDbManager().hasSkill(killer.getUniqueId(), skillTag(SkillType.KILLER_MONEY))) return;

        if (Math.random() > plugin.getConfig().getDouble("killer.chances.money")) return;

        int moneyGained = 1 + (int)(Math.random() * 15);

        giveMoney(killer, moneyGained);

        if (!plugin.getConfig().getBoolean("skills_messages")) return;

        killer.sendActionBar(plugin.formattedFromKey("skill_effects.killer.money").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(moneyGained)).build()));

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
