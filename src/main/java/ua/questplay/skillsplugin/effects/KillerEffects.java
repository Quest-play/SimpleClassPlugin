package ua.questplay.skillsplugin.effects;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillType;

public class KillerEffects implements Listener {
    private final SkillsPlugin plugin;

    public KillerEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKillerMurder(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), skillTag(SkillType.KILLER_MURDER))) return;

        boolean isPlayerVictim = event.getEntity() instanceof Player;

        if (!isPlayerVictim) return;

        if (Math.random() > plugin.getConfig().getDouble("killer.chances.murder")) return;

        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                plugin.getConfig().getInt("killer.effects.skills.murder") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.murder_strength"),
                true,
                false
        ));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            attacker.sendMessage(plugin.formattedFromKey("skill_effects.killer.murder"));
        }
        event.getDamager().getWorld().spawnParticle(Particle.ANGRY_VILLAGER,
                event.getDamager().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onKillerVampirism(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        boolean isPlayerVictim = event.getEntity() instanceof Player;

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), skillTag(SkillType.KILLER_VAMPIRISM))) return;

        if (!isPlayerVictim) return;

        if (Math.random() > plugin.getConfig().getDouble("killer.chances.vampirism")) return;

        double damage = 1.0;
        Player target = (Player) event.getEntity();
        target.setHealth(Math.max(0, target.getHealth() - damage));
        attacker.setHealth(Math.min(20, attacker.getHealth() + damage / 2));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            attacker.sendMessage(plugin.formattedFromKey("skill_effects.killer.vampirism"));
        }

        event.getDamager().getWorld().spawnParticle(Particle.HEART,
                event.getDamager().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onKillerHaste(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        boolean isPlayerVictim = event.getEntity() instanceof Player;

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), skillTag(SkillType.KILLER_HASTE))) return;

        if (!isPlayerVictim) return;

        if (Math.random() > plugin.getConfig().getDouble("killer.chances.haste")) return;

        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                plugin.getConfig().getInt("killer.effects.skills.haste") * 20,
                plugin.getConfig().getInt("killer.effects.effect_strength.haste_strength"),
                true,
                false
        ));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            attacker.sendMessage(plugin.formattedFromKey("skill_effects.killer.haste"));
        }
        event.getDamager().getWorld().spawnParticle(Particle.LAVA,
                event.getDamager().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        if (!plugin.getDbManager().hasSkill(killer.getUniqueId(), skillTag(SkillType.KILLER_RECOVERY))) return;

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
            killer.sendMessage(plugin.formattedFromKey("skill_effects.killer.recovery"));
        }
        killer.playSound(killer.getLocation(), Sound.ITEM_TOTEM_USE, 1.0f, 1.0f);

        killer.spawnParticle(Particle.TOTEM_OF_UNDYING, killer.getLocation(), 30);
        event.getEntity().getWorld().spawnParticle(Particle.TRIAL_OMEN,
                event.getEntity().getLocation().add(0, 2, 0),
                6, 0.5, 0.5, 0.5);
    }

    private String skillTag(SkillType skillType) {
        return plugin.getSkillManager().getSkill(skillType).tag();
    }
}
