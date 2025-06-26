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

public class KillerEffects implements Listener {
    private final SkillsPlugin plugin;

    public KillerEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onKillerMurder(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), "killer_murder")) return;

        boolean isPlayerVictim = event.getEntity() instanceof Player;

        if (!isPlayerVictim) return;

        if (Math.random() > 0.5) return;

        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                15 * 20,
                0,
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

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), "killer_vampirism")) return;

        if (!isPlayerVictim) return;

        if (Math.random() > 0.15) return;

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

        if (!plugin.getDbManager().hasSkill(attacker.getUniqueId(), "killer_haste")) return;

        if (!isPlayerVictim) return;

        if (Math.random() > 0.15) return;

        attacker.addPotionEffect(new PotionEffect(
                PotionEffectType.HASTE,
                10 * 20,
                0,
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

        if (!plugin.getDbManager().hasSkill(killer.getUniqueId(), "killer_recovery")) return;

        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                6 * 20,
                2,
                true,
                false
        ));

        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                5 * 20,
                1,
                true,
                false
        ));

        killer.addPotionEffect(new PotionEffect(
                PotionEffectType.REGENERATION,
                30 * 20,
                0,
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
}
