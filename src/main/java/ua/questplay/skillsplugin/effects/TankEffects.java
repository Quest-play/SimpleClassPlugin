package ua.questplay.skillsplugin.effects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.sacredlabyrinth.phaed.simpleclans.ClanPlayer;
import net.sacredlabyrinth.phaed.simpleclans.managers.ClanManager;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.SkillsPlugin;
import ua.questplay.skillsplugin.skills.SkillType;

import java.math.BigDecimal;

public class TankEffects implements Listener {
    private final SkillsPlugin plugin;
    private final ClanManager clanManager = SkillsPlugin.getInstance().getSimpleClans().getClanManager();
    int tankMoneyBase = 3;
    float damage_reduction = 0.7f;
    float player_damage = 0.75f;
    float mob_damage = 0.875f;

    public TankEffects(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTankMoney(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player tank)) return;
        if (!(event.getDamager() instanceof  Player attacker)) return;
        if (!plugin.getDbManager().hasSkill(tank.getUniqueId(), skillTag(SkillType.TANK_MONEY))) return;
        if (Math.random() > plugin.getConfig().getDouble("tank.chances.money")) return;
        ClanPlayer attackerClan = clanManager.getClanPlayer(attacker);
        ClanPlayer tankClan = clanManager.getClanPlayer(tank);
        if (tankClan == null) {
            giveMoney(tank, tankMoneyBase);
            tank.sendActionBar(plugin.formattedFromKey("skill_effects.tank.money").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(tankMoneyBase)).build()));
            return;
        }

        if (attackerClan.getClan().equals(tankClan.getClan())) return;
        int count = tankMoneyBase;
        for (Player player: tank.getLocation().getNearbyPlayers(10.0)) {
            if (tank.equals(player)) continue;
            ClanPlayer clanPlayer = clanManager.getClanPlayer(player);
            if (clanPlayer == null) continue;
            if (clanPlayer.getClan().equals(tankClan.getClan())) {
                count++;
            }
        }

        tank.sendPlainMessage(String.valueOf(count));
        giveMoney(tank, count);
        tank.sendActionBar(plugin.formattedFromKey("skill_effects.tank.money").replaceText(TextReplacementConfig.builder().matchLiteral("<money>").replacement(String.valueOf(count)).build()));

    }

    @EventHandler
    public void onBlock(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player tank)) return;

        if (!plugin.getDbManager().hasSkill(tank.getUniqueId(), skillTag(SkillType.TANK_BLOCK))) return;

        if (Math.random() > plugin.getConfig().getDouble("tank.chances.block")) return;

        event.setCancelled(true);
        tank.playSound(tank.getLocation(), Sound.ITEM_SHIELD_BLOCK, 0.7f, 1.8f);
        tank.sendActionBar(plugin.formattedFromKey("skill_effects.tank.block"));
    }

    @EventHandler
    public void onParry(EntityDamageByEntityEvent event) {
        if (!((event.getEntity()) instanceof Player tank)) return;

        if (!plugin.getDbManager().hasSkill(tank.getUniqueId(), skillTag(SkillType.TANK_PARRY))) return;
        if (Math.random() > plugin.getConfig().getDouble("tank.chances.parry")) return;

        Player attacker = getAttacker(event.getDamager());
        if (attacker == null) return;
        event.setDamage(event.getDamage() * 0.5);

        attacker.damage(event.getDamage());

        tank.sendActionBar(plugin.formattedPlaceholderFromKey("skill_effects.tank.parry", "<damage>", String.valueOf(event.getDamage())));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBalanceSelf(EntityDamageEvent event) {
        if (!((event.getEntity()) instanceof Player tank)) return;

        if (!plugin.getDbManager().hasSkill(tank.getUniqueId(), skillTag(SkillType.TANK_BALANCE))) return;

        event.setDamage(event.getDamage() * damage_reduction);
    }

    @EventHandler
    public void onBalanceDamage(EntityDamageByEntityEvent event) {
        if (!((event.getDamager()) instanceof  Player tank)) return;
        if (!plugin.getDbManager().hasSkill(tank.getUniqueId(), skillTag(SkillType.TANK_BALANCE))) return;

        if (event.getEntity() instanceof Player) {
            event.setDamage(event.getDamage() * player_damage);
            return;
        }

        event.setDamage(event.getDamage() * mob_damage);
    }

    @EventHandler
    public void onBlockAura(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (event.isCancelled()) return;

        ClanPlayer damagedClanPlayer = clanManager.getClanPlayer(damaged);
        if (damagedClanPlayer == null) return;

        for (Player nearby : damaged.getLocation().getNearbyPlayers(12.0)) {
            if (nearby.equals(damaged)) continue;

            if (!plugin.getDbManager().hasSkill(nearby.getUniqueId(), skillTag(SkillType.TANK_BLOCK))) continue;

            ClanPlayer nearbyClanPlayer = clanManager.getClanPlayer(nearby);
            if (nearbyClanPlayer == null) continue;
            if (!nearbyClanPlayer.getClan().equals(damagedClanPlayer.getClan())) continue;


            if (Math.random() > plugin.getConfig().getDouble("tank.chances.block_aura")) return;
            event.setCancelled(true);

            damaged.sendActionBar(plugin.formattedFromKey("skill_effects.tank.block"));
            break;
        }
    }

    @EventHandler
    public void onRedirectAura(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player damaged)) return;
        if (event.isCancelled()) return;

        ClanPlayer damagedClanPlayer = clanManager.getClanPlayer(damaged);
        if (damagedClanPlayer == null) return;

        for (Player nearby : damaged.getLocation().getNearbyPlayers(6.0)) {
            if (nearby.equals(damaged)) continue;

            if (!plugin.getDbManager().hasSkill(nearby.getUniqueId(), skillTag(SkillType.TANK_BLOCK))) continue;

            ClanPlayer nearbyClanPlayer = clanManager.getClanPlayer(nearby);
            if (nearbyClanPlayer == null) continue;
            if (!nearbyClanPlayer.getClan().equals(damagedClanPlayer.getClan())) continue;


            if (Math.random() > plugin.getConfig().getDouble("tank.chances.redirect_aura")) return;
            nearby.damage(event.getDamage());
            event.setCancelled(true);

            damaged.sendActionBar(plugin.formattedFromKey("skill_effects.tank.redirect_ally"));
            nearby.sendActionBar(plugin.formattedFromKey("skill_effects.tank.redirect_tank"));
            break;
        }
    }


    private String skillTag(SkillType skillType) {
        return plugin.getSkillManager().getSkill(skillType).tag();
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

    private Player getAttacker(Entity damager) {
        if (damager instanceof Player) {
            return (Player) damager;
        }
        if (damager instanceof Projectile && ((Projectile)damager).getShooter() instanceof Player) {
            return (Player) ((Projectile)damager).getShooter();
        }
        return null;
    }
}
