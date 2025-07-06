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
import ua.questplay.skillsplugin.skills.SkillType;

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
                thief.sendMessage(plugin.formattedFromKey("skill_effects.thief.stole_exp").replaceText(TextReplacementConfig.builder().matchLiteral("<stolen_exp>").replacement(String.valueOf(stolenExp)).build()));
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
            player.sendMessage(plugin.formattedFromKey("skill_effects.thief.caution"));
        }
        event.getEntity().getWorld().spawnParticle(Particle.EFFECT,
                event.getEntity().getLocation().add(0, 2, 0),
                12, 0.5, 0.5, 0.5);
    }

    private String skillTag(SkillType skillType) {
        return plugin.getSkillManager().getSkill(skillType).tag();
    }
}
