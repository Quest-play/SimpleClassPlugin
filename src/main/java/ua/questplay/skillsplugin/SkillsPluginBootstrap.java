package ua.questplay.skillsplugin;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class SkillsPluginBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(@NotNull BootstrapContext context) {

    }

    @Override
    public JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
        return PluginBootstrap.super.createPlugin(context);
    }
}
