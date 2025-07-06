package ua.questplay.skillsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault2.economy.Economy;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.commands.ClassCommand;
import ua.questplay.skillsplugin.commands.ReloadCommand;
import ua.questplay.skillsplugin.commands.SkillsCommand;
import ua.questplay.skillsplugin.commands.tabcompleters.ReloadCompleter;
import ua.questplay.skillsplugin.db.DatabaseManager;
import ua.questplay.skillsplugin.effects.KillerEffects;
import ua.questplay.skillsplugin.effects.SkillPotion;
import ua.questplay.skillsplugin.effects.ThiefEffects;
import ua.questplay.skillsplugin.effects.TraderEffects;
import ua.questplay.skillsplugin.gui.ClassGUIListener;
import ua.questplay.skillsplugin.gui.SkillsGUIListener;
import ua.questplay.skillsplugin.listeners.JoinEvent;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.skills.SkillManager;
import ua.questplay.skillsplugin.skills.SkillType;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class SkillsPlugin extends JavaPlugin {
    private DatabaseManager dbManager;
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration prices;
    private File messagesFile;

    private static Economy economyModern = null;
    private static net.milkbowl.vault.economy.Economy economyLegacy = null;


    private final SkillManager skillManager = new SkillManager();

    public static Economy getEconomyModern() {
        return economyModern;
    }

    public static net.milkbowl.vault.economy.Economy getEconomyLegacy() {
        return economyLegacy;
    }

    @Override
    public void onEnable() {
        dbManager = new DatabaseManager(this);
        dbManager.connect();

        saveDefaultConfig();
        setupConfigFile();
        setupMessagesConfig();
        setupPricesConfig();

        registerSkills();
        registerCommands();
        registerEvents();

        if (setupLegacyEconomy()) {
            getLogger().info("Vault connected successfully");
            getLogger().info("Using Legacy Economy.");
        } else if (setupModernEconomy()) {
            getLogger().info("Vault connected successfully");
            getLogger().info("Using Modern Economy.");
        } else {
            getLogger().severe("Economy plugin not found!");
            getServer().getPluginManager().disablePlugin(this);
        }

        SkillPotion effectApplier = new SkillPotion(this);
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
    }

    @Override
    public @NotNull FileConfiguration getConfig() {
        return config;
    }

    private boolean setupLegacyEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault not found.");
            getLogger().severe("Shutdown...");
            return false;
        }
        RegisteredServiceProvider<net.milkbowl.vault.economy.Economy> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (rsp == null) {
            getLogger().severe("Legacy Economy not found.");
            return false;
        }
        economyLegacy = rsp.getProvider();

        return economyLegacy != null;
    }

    private boolean setupModernEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault not found.");
            getLogger().severe("Shutdown...");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().severe("Modern Economy not found.");
            return false;
        }
        economyModern = rsp.getProvider();

        return economyModern != null;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public void reloadMessages() {
        FileConfiguration newMessages = YamlConfiguration.loadConfiguration(messagesFile);

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

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public Component formattedFromKey(String key) {
        return MiniMessage.miniMessage().deserialize(getMessageFromKey(key)).decoration(TextDecoration.ITALIC, false);
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
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new TraderEffects(this), this);
        getServer().getPluginManager().registerEvents(new ThiefEffects(this), this);
        getServer().getPluginManager().registerEvents(new KillerEffects(this), this);
    }

    private void registerSkills() {
        skillManager.registerSkill(SkillType.KILLER_SPEED, new SkillData(
                formattedFromKey("skills_gui.skills.killer.speed"),
                Material.BLAZE_POWDER,
                List.of(formattedFromKey("skills_gui.skills.killer.speed_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.speed"))).build())
                ),
                prices.getInt("killer.speed"),
                "killer_speed"
        ));

        skillManager.registerSkill(SkillType.KILLER_MURDER, new SkillData(
                formattedFromKey("skills_gui.skills.killer.murder"),
                Material.REDSTONE,
                List.of(formattedFromKey("skills_gui.skills.killer.murder_desc"),
                        formattedFromKey("skills_gui.skills.killer.murder_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.murder"))).build())
                ),
                prices.getInt("killer.murder"),
                "killer_murder"
        ));

        skillManager.registerSkill(SkillType.KILLER_HASTE, new SkillData(
                formattedFromKey("skills_gui.skills.killer.haste"),
                Material.NETHERITE_SWORD,
                List.of(formattedFromKey("skills_gui.skills.killer.haste_desc"),
                        formattedFromKey("skills_gui.skills.killer.haste_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.haste"))).build())
                ),
                prices.getInt("killer.haste"),
                "killer_haste"
        ));

        skillManager.registerSkill(SkillType.KILLER_VAMPIRISM, new SkillData(
                formattedFromKey("skills_gui.skills.killer.vampirism"),
                Material.TIPPED_ARROW,
                List.of(formattedFromKey("skills_gui.skills.killer.vampirism_desc"),
                        formattedFromKey("skills_gui.skills.killer.vampirism_desc1"),
                        formattedFromKey("skills_gui.skills.killer.vampirism_desc2"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.vampirism"))).build())
                ),
                prices.getInt("killer.vampirism"),
                "killer_vampirism"
        ));

        skillManager.registerSkill(SkillType.KILLER_RECOVERY, new SkillData(
                formattedFromKey("skills_gui.skills.killer.recovery"),
                Material.NETHER_STAR,
                List.of(formattedFromKey("skills_gui.skills.killer.recovery_desc"),
                        formattedFromKey("skills_gui.skills.killer.recovery_desc1"),
                        formattedFromKey("skills_gui.skills.killer.recovery_desc2"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.recovery"))).build())
                ),
                prices.getInt("killer.recovery"),
                "killer_recovery"
        ));

        skillManager.registerSkill(SkillType.THIEF_SPEED, new SkillData(
                formattedFromKey("skills_gui.skills.thief.speed"),
                Material.FEATHER,
                List.of(formattedFromKey("skills_gui.skills.thief.speed_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.speed"))).build())
                ),
                prices.getInt("thief.speed"),
                "thief_speed"
        ));

        skillManager.registerSkill(SkillType.THIEF_HASTE, new SkillData(
                formattedFromKey("skills_gui.skills.thief.haste"),
                Material.GOLDEN_SWORD,
                List.of(formattedFromKey("skills_gui.skills.thief.haste_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.haste"))).build())
                ),
                prices.getInt("thief.haste"),
                "thief_haste"
        ));

        skillManager.registerSkill(SkillType.THIEF_EXP, new SkillData(
                formattedFromKey("skills_gui.skills.thief.stole_exp"),
                Material.EXPERIENCE_BOTTLE,
                List.of(formattedFromKey("skills_gui.skills.thief.stole_exp_desc"),
                        formattedFromKey("skills_gui.skills.thief.stole_exp_desc1"),
                        formattedFromKey("skills_gui.skills.thief.stole_exp_desc2"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.stole_exp"))).build())
                ),
                prices.getInt("thief.stole_exp"),
                "thief_stole_exp"
        ));

        skillManager.registerSkill(SkillType.THIEF_CAUTION, new SkillData(
                formattedFromKey("skills_gui.skills.thief.caution"),
                Material.LEATHER_BOOTS,
                List.of(formattedFromKey("skills_gui.skills.thief.caution_desc"),
                        formattedFromKey("skills_gui.skills.thief.caution_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.caution"))).build())
                ),
                prices.getInt("thief.caution"),
                "thief_caution"
        ));

        skillManager.registerSkill(SkillType.THIEF_SPECIALIZATION, new SkillData(
                formattedFromKey("skills_gui.skills.thief.specialization"),
                Material.WOODEN_SWORD,
                List.of(formattedFromKey("skills_gui.skills.thief.specialization_desc"),
                        formattedFromKey("skills_gui.skills.thief.specialization_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.specialization"))).build())
                ),
                prices.getInt("thief.specialization"),
                "thief_specialization"
        ));

        skillManager.registerSkill(SkillType.MERCHANT_LUCK, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.luck"),
                Material.EMERALD,
                List.of(formattedFromKey("skills_gui.skills.merchant.luck_desc"),
                        formattedFromKey("skills_gui.skills.merchant.luck_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.luck"))).build())
                ),
                prices.getInt("merchant.luck"),
                "merchant_luck"
        ));

        skillManager.registerSkill(SkillType.MERCHANT_EXP, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.exp"),
                Material.EXPERIENCE_BOTTLE,
                List.of(formattedFromKey("skills_gui.skills.merchant.exp_desc"),
                        formattedFromKey("skills_gui.skills.merchant.exp_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.exp"))).build())
                ),
                prices.getInt("merchant.exp"),
                "merchant_exp"
        ));

        skillManager.registerSkill(SkillType.MERCHANT_RUN, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.run"),
                Material.FEATHER,
                List.of(formattedFromKey("skills_gui.skills.merchant.run_desc"),
                        formattedFromKey("skills_gui.skills.merchant.run_desc1"),
                        formattedFromKey("skills_gui.skills.merchant.run_desc2"),
                        formattedFromKey("skills_gui.skills.merchant.run_desc3"),
                        formattedFromKey("skills_gui.skills.merchant.run_desc4"),
                        formattedFromKey("skills_gui.skills.merchant.run_desc5"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.run"))).build())
                ),
                prices.getInt("merchant.run"),
                "merchant_run"
        ));

        skillManager.registerSkill(SkillType.MERCHANT_HERO, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.hero"),
                Material.EMERALD_BLOCK,
                List.of(formattedFromKey("skills_gui.skills.merchant.hero_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.hero"))).build())
                ),
                prices.getInt("merchant.hero"),
                "merchant_hero"
        ));

        skillManager.registerSkill(SkillType.MERCHANT_BLESSING, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.blessing"),
                Material.TOTEM_OF_UNDYING,
                List.of(formattedFromKey("skills_gui.skills.merchant.blessing_desc"),
                        formattedFromKey("skills_gui.skills.merchant.blessing_desc1"),
                        formattedFromKey("skills_gui.skills.merchant.blessing_desc2"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.blessing"))).build())
                        ),
                prices.getInt("merchant.blessing"),
                "merchant_blessing"
        ));
    }

    //messages.yml
    private void setupMessagesConfig() {
        messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void setupPricesConfig() {
        File pricesFile = new File(getDataFolder(), "prices.yml");

        if (!pricesFile.exists()) {
            saveResource("prices.yml", false);
        }

        prices = YamlConfiguration.loadConfiguration(pricesFile);
    }

    private void setupConfigFile() {
        File configFile = new File(getDataFolder(), "config.yml");

        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public FileConfiguration getPrices() {
        return prices;
    }
}
