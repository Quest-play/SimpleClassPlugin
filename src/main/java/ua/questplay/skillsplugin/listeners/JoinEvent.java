package ua.questplay.skillsplugin.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ua.questplay.skillsplugin.SkillsPlugin;

public class JoinEvent implements Listener {
    private final SkillsPlugin plugin;

    public JoinEvent(SkillsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!plugin.getDbManager().hasClass(player.getUniqueId())) {
            player.sendMessage(plugin.formattedFromKey("class_command.choose"));
        }
    }
}
