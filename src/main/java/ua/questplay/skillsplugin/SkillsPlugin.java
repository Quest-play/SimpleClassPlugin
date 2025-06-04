package ua.questplay.skillsplugin;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ua.questplay.skillsplugin.commands.ClassCommand;
import ua.questplay.skillsplugin.commands.ReloadCommand;
import ua.questplay.skillsplugin.commands.SkillsCommand;
import ua.questplay.skillsplugin.commands.tabcompleters.ReloadCompleter;
import ua.questplay.skillsplugin.db.DatabaseManager;
import ua.questplay.skillsplugin.gui.ClassGUIListener;
import ua.questplay.skillsplugin.gui.SkillsGUIListener;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class SkillsPlugin extends JavaPlugin {
    private DatabaseManager dbManager;
    private FileConfiguration config;
    private FileConfiguration messages;
    private File messagesFile;

    private FileConfiguration newMessages = null;

    @Override
    public void onEnable() {
        dbManager = new DatabaseManager();
        dbManager.connect();

        saveDefaultConfig();
        setupMessagesConfig();
        config = getConfig();

        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dbManager.disconnect();
        getLogger().info("Plugin is deactivated.");
    }

    //Public
    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    //Private

    //Register
    private void registerCommands() {
        getCommand("class").setExecutor(new ClassCommand(this));
        getCommand("reload").setExecutor(new ReloadCommand(this));
        getCommand("skills").setExecutor(new SkillsCommand(this));

        getCommand("reload").setTabCompleter(new ReloadCompleter());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new ClassGUIListener(this),this);
        getServer().getPluginManager().registerEvents(new SkillsGUIListener(this), this);
        /*
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ThiefSkillsListener(this), this);
        getServer().getPluginManager().registerEvents(new AssassinSkillsListener(this), this);
        getServer().getPluginManager().registerEvents(new TraderSkillsListener(this), this);
        getServer().getPluginManager().registerEvents(new TraderBlessListener(this), this);
         */
    }

    //messages.yml
    private void setupMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reloadMessages() {
        newMessages = YamlConfiguration.loadConfiguration(messagesFile);

        final InputStream defConfigStream = getResource("messages.yml");
        if (defConfigStream == null) {
            return;
        }

        newMessages.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8)));


        messages = newMessages;
    }

    public String getMessageFromKey(String key){
        return messages.getString(key);
    }
}
