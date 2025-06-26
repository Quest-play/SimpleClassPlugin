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
import ua.questplay.skillsplugin.SkillsPlugin;

public class ThiefEffects implements Listener {
    private final SkillsPlugin plugin;

    public ThiefEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onStealExp(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player thief)) return;

        if (!(event.getEntity() instanceof Player target)) return;

        if (!plugin.getDbManager().hasSkill(thief.getUniqueId(), "thief_stole_exp")) return;

        if (Math.random() > 0.95) return;
        thief.spawnParticle(Particle.ASH,
                thief.getLocation().add(0, 2, 0),
                12, 0.5, 0.5, 0.5);

        int stolenExp = (int) (Math.random() * 6) + 5;
        if (target.getExp() > 0) {
            target.giveExp(-stolenExp);
            thief.giveExp(stolenExp);
            if (plugin.getConfig().getBoolean("skills_messages")) {
                thief.sendMessage(plugin.formattedFromKey("skill_effects.thief.stole_exp").replaceText(TextReplacementConfig.builder().matchLiteral("<stolen_exp>").replacement(String.valueOf(stolenExp)).build()));
            }
        }

    }

    @EventHandler
    public void onThiefCaution(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (!plugin.getDbManager().hasSkill(player.getUniqueId(), "thief_caution")) return;
        if (Math.random() > 0.08) return;

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.RESISTANCE,
                8 * 20,
                0
        ));
        if (plugin.getConfig().getBoolean("skills_messages")) {
            player.sendMessage(plugin.formattedFromKey("skill_effects.thief.caution"));
        }
        event.getEntity().getWorld().spawnParticle(Particle.EFFECT,
                event.getEntity().getLocation().add(0, 2, 0),
                12, 0.5, 0.5, 0.5);
    }
}
