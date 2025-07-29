package ua.questplay.skillsplugin;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault2.economy.Economy;
import net.sacredlabyrinth.phaed.simpleclans.SimpleClans;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import ua.questplay.skillsplugin.commands.*;
import ua.questplay.skillsplugin.db.DatabaseManager;
import ua.questplay.skillsplugin.effects.*;
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

public final class SkillsPlugin extends JavaPlugin implements Listener {
    private static SkillsPlugin instance;
    private DatabaseManager dbManager;
    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration prices;
    private File messagesFile;

    private static Economy economyModern = null;
    private static net.milkbowl.vault.economy.Economy economyLegacy = null;
    private SimpleClans simpleClans;


    private final SkillManager skillManager = new SkillManager();

    @Override
    public void onEnable() {
        instance = this;
        dbManager = new DatabaseManager(this);
        simpleClans = (SimpleClans) getServer().getPluginManager().getPlugin("SimpleClans");

        saveDefaultConfig();
        dbManager.connect();
        setupConfigFile();
        setupMessagesConfig();
        setupPricesConfig();

        registerCommands();
        registerSkills();
        registerEvents();

        saveSkillsToDb();

        setupEconomy();

        SkillPotion effectApplier = new SkillPotion(this);
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
        dbManager.disconnect();
        getLogger().info("Plugin is deactivated.");
    }

    //Public
    public static Economy getEconomyModern() {
        return economyModern;
    }

    public static net.milkbowl.vault.economy.Economy getEconomyLegacy() {
        return economyLegacy;
    }

    public static SkillsPlugin getInstance() {
        return instance;
    }

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

    @EventHandler
    public void onServiceRegister(ServiceRegisterEvent event) {
        if (event.getProvider().getService() == Economy.class) {
            economyModern = Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
            getLogger().info("Successfully hooked into Vault for ModernEconomy!");
        }

        if (event.getProvider().getService() == net.milkbowl.vault.economy.Economy.class) {
            economyLegacy = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class).getProvider();
            getLogger().info("Successfully hooked into Vault for LegacyEconomy!");
        }
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

    private void setupEconomy() {
        if (setupLegacyEconomy()) {
            getLogger().info("Vault connected successfully");
            getLogger().info("Using Legacy Economy.");
        } else if (setupModernEconomy()) {
            getLogger().info("Vault connected successfully");
            getLogger().info("Using Modern Economy.");
        } else {
            getLogger().severe("Economy plugin not found!");
            //getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void saveSkillsToDb() {
        for (SkillType skill : SkillType.values()) {
            dbManager.addSkillName(skill.name());
        }
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

    public Component formattedPlaceholderFromKey(String key, String match, String replacement) {
        return MiniMessage.miniMessage().deserialize(getMessageFromKey(key))
                .decoration(TextDecoration.ITALIC, false)
                .replaceText(TextReplacementConfig.builder().matchLiteral(match).replacement(replacement).build())
                ;
    }

    //Register
    private void registerCommands() {
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register("class", new ClassCommand(this));
            commands.registrar().register("skills_reload", new ReloadCommand(this));
            commands.registrar().register("skills", new SkillsCommand(this));
            commands.registrar().register("give_skill", new GiveSkillCommand(this));
        });
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new ClassGUIListener(this),this);
        getServer().getPluginManager().registerEvents(new SkillsGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(this), this);
        getServer().getPluginManager().registerEvents(new MerchantEffects(this), this);
        getServer().getPluginManager().registerEvents(new ThiefEffects(this), this);
        getServer().getPluginManager().registerEvents(new KillerEffects(this), this);
        getServer().getPluginManager().registerEvents(new TankEffects(this), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    private void registerSkills() {
        skillManager.registerSkill(SkillType.KILLER_MONEY, new SkillData(
                formattedFromKey("skills_gui.skills.killer.money"),
                Material.GOLD_NUGGET,
                List.of(formattedFromKey("skills_gui.skills.killer.money_desc"),
                        formattedFromKey("skills_gui.skills.killer.money_desc1"),
                        formattedFromKey("skills_gui.skills.killer.money_desc2"),
                        formattedFromKey("skills_gui.skills.killer.money_desc3"),
                        formattedFromKey("skills_gui.skills.killer.money_desc4")


                ),
                prices.getInt("killer.money"),
                SkillType.KILLER_MONEY.name()
        ));

        skillManager.registerSkill(SkillType.KILLER_SPEED, new SkillData(
                formattedFromKey("skills_gui.skills.killer.speed"),
                Material.BLAZE_POWDER,
                List.of(formattedFromKey("skills_gui.skills.killer.speed_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.speed"))).build())
                ),
                prices.getInt("killer.speed"),
                SkillType.KILLER_SPEED.name()
        ));

        skillManager.registerSkill(SkillType.KILLER_MURDER, new SkillData(
                formattedFromKey("skills_gui.skills.killer.murder"),
                Material.REDSTONE,
                List.of(formattedFromKey("skills_gui.skills.killer.murder_desc"),
                        formattedFromKey("skills_gui.skills.killer.murder_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.murder"))).build())
                ),
                prices.getInt("killer.murder"),
                SkillType.KILLER_MURDER.name()
        ));

        skillManager.registerSkill(SkillType.KILLER_HASTE, new SkillData(
                formattedFromKey("skills_gui.skills.killer.haste"),
                Material.NETHERITE_SWORD,
                List.of(formattedFromKey("skills_gui.skills.killer.haste_desc"),
                        formattedFromKey("skills_gui.skills.killer.haste_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("killer.haste"))).build())
                ),
                prices.getInt("killer.haste"),
                SkillType.KILLER_HASTE.name()
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
                SkillType.KILLER_VAMPIRISM.name()
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
                SkillType.KILLER_RECOVERY.name()
        ));

        skillManager.registerSkill(SkillType.THIEF_MONEY, new SkillData(
                formattedFromKey("skills_gui.skills.thief.money"),
                Material.GOLD_NUGGET,
                List.of(formattedFromKey("skills_gui.skills.thief.money_desc"),
                        formattedFromKey("skills_gui.skills.thief.money_desc1"),
                        formattedFromKey("skills_gui.skills.thief.money_desc2")
                ),
                prices.getInt("thief.money"),
                SkillType.THIEF_MONEY.name()
        ));

        skillManager.registerSkill(SkillType.THIEF_SPEED, new SkillData(
                formattedFromKey("skills_gui.skills.thief.speed"),
                Material.FEATHER,
                List.of(formattedFromKey("skills_gui.skills.thief.speed_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.speed"))).build())
                ),
                prices.getInt("thief.speed"),
                SkillType.THIEF_SPEED.name()
        ));

        skillManager.registerSkill(SkillType.THIEF_HASTE, new SkillData(
                formattedFromKey("skills_gui.skills.thief.haste"),
                Material.GOLDEN_SWORD,
                List.of(formattedFromKey("skills_gui.skills.thief.haste_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.haste"))).build())
                ),
                prices.getInt("thief.haste"),
                SkillType.THIEF_HASTE.name()
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
                SkillType.THIEF_EXP.name()
        ));

        skillManager.registerSkill(SkillType.THIEF_CAUTION, new SkillData(
                formattedFromKey("skills_gui.skills.thief.caution"),
                Material.LEATHER_BOOTS,
                List.of(formattedFromKey("skills_gui.skills.thief.caution_desc"),
                        formattedFromKey("skills_gui.skills.thief.caution_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.caution"))).build())
                ),
                prices.getInt("thief.caution"),
                SkillType.THIEF_CAUTION.name()
        ));

        skillManager.registerSkill(SkillType.THIEF_SPECIALIZATION, new SkillData(
                formattedFromKey("skills_gui.skills.thief.specialization"),
                Material.WOODEN_SWORD,
                List.of(formattedFromKey("skills_gui.skills.thief.specialization_desc"),
                        formattedFromKey("skills_gui.skills.thief.specialization_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("thief.specialization"))).build())
                ),
                prices.getInt("thief.specialization"),
                SkillType.THIEF_SPECIALIZATION.name()
        ));

        skillManager.registerSkill(SkillType.MERCHANT_MONEY, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.money"),
                Material.GOLD_NUGGET,
                List.of(formattedFromKey("skills_gui.skills.merchant.money_desc"),
                        formattedFromKey("skills_gui.skills.merchant.money_desc1")
                ),
                prices.getInt("merchant.money"),
                SkillType.MERCHANT_MONEY.name()
        ));

        skillManager.registerSkill(SkillType.MERCHANT_LUCK, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.luck"),
                Material.EMERALD,
                List.of(formattedFromKey("skills_gui.skills.merchant.luck_desc"),
                        formattedFromKey("skills_gui.skills.merchant.luck_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.luck"))).build())
                ),
                prices.getInt("merchant.luck"),
                SkillType.MERCHANT_LUCK.name()
        ));

        skillManager.registerSkill(SkillType.MERCHANT_EXP, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.exp"),
                Material.EXPERIENCE_BOTTLE,
                List.of(formattedFromKey("skills_gui.skills.merchant.exp_desc"),
                        formattedFromKey("skills_gui.skills.merchant.exp_desc1"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.exp"))).build())
                ),
                prices.getInt("merchant.exp"),
                SkillType.MERCHANT_EXP.name()
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
                SkillType.MERCHANT_RUN.name()
        ));

        skillManager.registerSkill(SkillType.MERCHANT_HERO, new SkillData(
                formattedFromKey("skills_gui.skills.merchant.hero"),
                Material.EMERALD_BLOCK,
                List.of(formattedFromKey("skills_gui.skills.merchant.hero_desc"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("merchant.hero"))).build())
                ),
                prices.getInt("merchant.hero"),
                SkillType.MERCHANT_HERO.name()
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
                SkillType.MERCHANT_BLESSING.name()
        ));

        skillManager.registerSkill(SkillType.TANK_MONEY, new SkillData(
                formattedFromKey("skills_gui.skills.tank.money"),
                Material.GOLD_NUGGET,
                List.of(formattedFromKey("skills_gui.skills.tank.money_desc"),
                        formattedFromKey("skills_gui.skills.tank.money_desc2"),
                        formattedFromKey("skills_gui.skills.tank.money_desc3"),
                        formattedFromKey("skills_gui.skills.tank.money_desc4")),
                prices.getInt("tank.money"),
                SkillType.TANK_MONEY.name()
        ));

        skillManager.registerSkill(SkillType.TANK_BLOCK, new SkillData(
                formattedFromKey("skills_gui.skills.tank.block"),
                Material.SHIELD,
                List.of(formattedFromKey("skills_gui.skills.tank.block_desc"),
                        formattedFromKey("skills_gui.skills.tank.block_desc2"),
                        formattedFromKey("skills_gui.skills.tank.block_desc3"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("tank.block"))).build())

                ),
                prices.getInt("tank.block"),
                SkillType.TANK_BLOCK.name()
        ));

        skillManager.registerSkill(SkillType.TANK_PARRY, new SkillData(
                formattedFromKey("skills_gui.skills.tank.parry"),
                Material.STONE_SWORD,
                List.of(formattedFromKey("skills_gui.skills.tank.parry_desc"),
                        formattedFromKey("skills_gui.skills.tank.parry_desc2"),
                        formattedFromKey("skills_gui.price").replaceText(TextReplacementConfig.builder().matchLiteral("<price>").replacement(String.valueOf(prices.getInt("tank.parry"))).build())

                ),
                prices.getInt("tank.parry"),
                SkillType.TANK_PARRY.name()
        ));

        skillManager.registerSkill(SkillType.TANK_BALANCE, new SkillData(
                formattedFromKey("skills_gui.skills.tank.balance"),
                Material.FIREWORK_STAR,
                List.of(formattedFromKey("skills_gui.skills.tank.balance_desc"),
                        formattedFromKey("skills_gui.skills.tank.balance_desc2"),
                        formattedFromKey("skills_gui.skills.tank.balance_desc3"),
                        formattedPlaceholderFromKey("skills_gui.price", "<price>", String.valueOf(prices.getInt("tank.balance")))
                ),
                prices.getInt("tank.balance"),
                SkillType.TANK_BALANCE.name()
        ));

        skillManager.registerSkill(SkillType.TANK_BLOCK_AURA, new SkillData(
                formattedFromKey("skills_gui.skills.tank.block_aura"),
                Material.TOTEM_OF_UNDYING,
                List.of(formattedFromKey("skills_gui.skills.tank.block_aura_desc"),
                        formattedFromKey("skills_gui.skills.tank.block_aura_desc2"),
                        formattedFromKey("skills_gui.skills.tank.block_aura_desc3"),
                        formattedPlaceholderFromKey("skills_gui.price", "<price>", String.valueOf(prices.getInt("tank.block_aura")))

                ),
                prices.getInt("tank.block_aura"),
                SkillType.TANK_BLOCK_AURA.name()
        ));

        skillManager.registerSkill(SkillType.TANK_REDIRECT_AURA, new SkillData(
                formattedFromKey("skills_gui.skills.tank.redirect_aura"),
                Material.GHAST_TEAR,
                List.of(formattedFromKey("skills_gui.skills.tank.redirect_aura_desc"),
                        formattedFromKey("skills_gui.skills.tank.redirect_aura_desc2"),
                        formattedFromKey("skills_gui.skills.tank.redirect_aura_desc3"),
                        formattedPlaceholderFromKey("skills_gui.price", "<price>", String.valueOf(prices.getInt("tank.redirect_aura")))

                ),
                prices.getInt("tank.redirect_aura"),
                SkillType.TANK_REDIRECT_AURA.name()
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

    public SimpleClans getSimpleClans() {
        return simpleClans;
    }
}
