package ua.questplay.skillsplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import ua.questplay.skillsplugin.commands.ClassCommand;
import ua.questplay.skillsplugin.commands.ReloadCommand;
import ua.questplay.skillsplugin.commands.SkillsCommand;
import ua.questplay.skillsplugin.commands.tabcompleters.ReloadCompleter;
import ua.questplay.skillsplugin.db.DatabaseManager;
import ua.questplay.skillsplugin.gui.ClassGUIListener;
import ua.questplay.skillsplugin.gui.SkillsGUIListener;
import ua.questplay.skillsplugin.skills.SkillData;
import ua.questplay.skillsplugin.skills.SkillManager;
import ua.questplay.skillsplugin.skills.SkillType;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public final class SkillsPlugin extends JavaPlugin {
    private DatabaseManager dbManager;
    private FileConfiguration config;
    private FileConfiguration messages;
    private File messagesFile;
    private final SkillManager skillManager = new SkillManager();

    private FileConfiguration newMessages = null;

    @Override
    public void onEnable() {
        dbManager = new DatabaseManager();
        dbManager.connect();

        saveDefaultConfig();
        setupMessagesConfig();
        config = getConfig();
        registerSkills();

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

    private void registerSkills() {
        skillManager.registerSkill(SkillType.KILLER_SPEED, new SkillData(
                formattedFromKey("skills_gui.skills.killer.speed"),
                Material.BLAZE_POWDER,
                List.of(formattedFromKey("skills_gui.skills.killer.speed_desc")
                ),
                Arrays.asList(
                        new ItemStack(Material.SUGAR,16),
                        new ItemStack(Material.REDSTONE, 32)
                )
        ));

        skillManager.registerSkill(SkillType.KILLER_MURDER, new SkillData(
                formattedFromKey("skills_gui.skills.killer.murder"),
                Material.REDSTONE,
                List.of(formattedFromKey("skills_gui.skills.killer.murder_desc"),
                        formattedFromKey("skills_gui.skills.killer.murder_desc1")
                ),
                Arrays.asList(
                        new ItemStack(Material.BLAZE_POWDER,48),
                        new ItemStack(Material.DIAMOND,6)
                )
        ));

        skillManager.registerSkill(SkillType.KILLER_HASTE, new SkillData(
                formattedFromKey("skills_gui.skills.killer.haste"),
                Material.NETHERITE_SWORD,
                List.of(formattedFromKey("skills_gui.skills.killer.haste_desc"),
                        formattedFromKey("skills_gui.skills.killer.haste_desc1")
                ),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND, 8),
                        new ItemStack(Material.GOLD_INGOT, 32),
                        new ItemStack(Material.EMERALD, 64)
                ))
        );

        skillManager.registerSkill(SkillType.KILLER_VAMPIRISM, new SkillData(
                formattedFromKey("skills_gui.skills.killer.vampirism"),
                Material.TIPPED_ARROW,
                List.of(formattedFromKey("skills_gui.skills.killer.vampirism_desc"),
                        formattedFromKey("skills_gui.skills.killer.vampirism_desc1"),
                        formattedFromKey("skills_gui.skills.killer.vampirism_desc2")
                ),
                Arrays.asList(
                        new ItemStack(Material.ANCIENT_DEBRIS,3),
                        new ItemStack(Material.GOLDEN_APPLE, 16)
                )
        ));

        skillManager.registerSkill(SkillType.KILLER_RECOVERY, new SkillData(
                formattedFromKey("skills_gui.skills.killer.recovery"),
                Material.NETHER_STAR,
                List.of(formattedFromKey("skills_gui.skills.killer.recovery_desc"),
                        formattedFromKey("skills_gui.skills.killer.recovery_desc1"),
                        formattedFromKey("skills_gui.skills.killer.recovery_desc2")
                ),
                Arrays.asList(
                        new ItemStack(Material.NETHER_STAR,1),
                        new ItemStack(Material.NETHERITE_INGOT,3),
                        new ItemStack(Material.GOLDEN_APPLE, 48)
                )
        ));

        skillManager.registerSkill(SkillType.THIEF_SPEED, new SkillData(
                formattedFromKey("skills_gui.skills.thief.speed"),
                Material.FEATHER,
                List.of(formattedFromKey("skills_gui.skills.thief.speed_desc")
                ),
                Arrays.asList(
                        new ItemStack(Material.SUGAR, 64), // 3 зелья скорости I
                        new ItemStack(Material.GOLD_INGOT, 16)
                )
        ));

        skillManager.registerSkill(SkillType.THIEF_HASTE, new SkillData(
                formattedFromKey("skills_gui.skills.thief.haste"),
                Material.GOLDEN_SWORD,
                List.of(formattedFromKey("skills_gui.skills.thief.haste_desc")
                ),
                Arrays.asList(
                        new ItemStack(Material.SUGAR, 96),
                        new ItemStack(Material.BLAZE_POWDER, 32),
                        new ItemStack(Material.GOLDEN_APPLE, 1)
                )
        ));

        skillManager.registerSkill(SkillType.THIEF_EXP, new SkillData(
                formattedFromKey("skills_gui.skills.thief.stole_exp"),
                Material.EXPERIENCE_BOTTLE,
                List.of(formattedFromKey("skills_gui.skills.thief.stole_exp_desc"),
                        formattedFromKey("skills_gui.skills.thief.stole_exp_desc1"),
                        formattedFromKey("skills_gui.skills.thief.stole_exp_desc2")
                ),
                Arrays.asList(
                        new ItemStack(Material.EXPERIENCE_BOTTLE, 64),
                        new ItemStack(Material.EMERALD, 12),
                        new ItemStack(Material.DIAMOND, 2),
                        new ItemStack(Material.COPPER_INGOT, 64)
                )
        ));

        skillManager.registerSkill(SkillType.THIEF_CAUTION, new SkillData(
                formattedFromKey("skills_gui.skills.thief.caution"),
                Material.LEATHER_BOOTS,
                List.of(formattedFromKey("skills_gui.skills.thief.caution_desc"),
                        formattedFromKey("skills_gui.skills.thief.caution_desc1")
                ),
                Arrays.asList(
                        new ItemStack(Material.ANCIENT_DEBRIS, 3),
                        new ItemStack(Material.GOLDEN_APPLE, 10)
                )
        ));

        skillManager.registerSkill(SkillType.THIEF_SPECIALIZATION, new SkillData(
                formattedFromKey("skills_gui.skills.thief.specialization"),
                Material.WOODEN_SWORD,
                List.of(formattedFromKey("skills_gui.skills.thief.specialization_desc"),
                        formattedFromKey("skills_gui.skills.thief.specialization_desc1")
                ),
                Arrays.asList(
                        new ItemStack(Material.SUGAR, 256),
                        new ItemStack(Material.BLAZE_POWDER, 128),
                        new ItemStack(Material.NETHERITE_INGOT, 3),
                        new ItemStack(Material.GOLDEN_APPLE, 4)
                )
        ));

        skillManager.registerSkill(SkillType.MERCHANT_LUCK, new SkillData(
                formattedFromKey("skills_gui.skills.trader.luck"),
                Material.EMERALD,
                List.of(formattedFromKey("skills_gui.skills.trader.luck_desc"),
                        formattedFromKey("skills_gui.skills.trader.luck_desc1")
                ),
                Arrays.asList(
                        new ItemStack(Material.DIAMOND,1),
                        new ItemStack(Material.EMERALD,32)
                )
        ));

        skillManager.registerSkill(SkillType.MERCHANT_EXP, new SkillData(
                formattedFromKey("skills_gui.skills.trader.exp"),
                Material.EXPERIENCE_BOTTLE,
                List.of(formattedFromKey("skills_gui.skills.trader.exp_desc"),
                        formattedFromKey("skills_gui.skills.trader.exp_desc1")
                ),
                Arrays.asList(
                        new ItemStack(Material.EMERALD_BLOCK, 16),
                        new ItemStack(Material.DIAMOND,8),
                        new ItemStack(Material.EXPERIENCE_BOTTLE,32)
                )
        ));

        skillManager.registerSkill(SkillType.MERCHANT_RUN, new SkillData(
                formattedFromKey("skills_gui.skills.trader.run"),
                Material.FEATHER,
                List.of(formattedFromKey("skills_gui.skills.trader.run_desc"),
                        formattedFromKey("skills_gui.skills.trader.run_desc1"),
                        formattedFromKey("skills_gui.skills.trader.run_desc2"),
                        formattedFromKey("skills_gui.skills.trader.run_desc3"),
                        formattedFromKey("skills_gui.skills.trader.run_desc4"),
                        formattedFromKey("skills_gui.skills.trader.run_desc5")
                ),
                Arrays.asList(
                        new ItemStack(Material.SUGAR, 1024),
                        new ItemStack(Material.EMERALD_BLOCK,32),
                        new ItemStack(Material.GOLDEN_APPLE, 8)
                )
        ));

        skillManager.registerSkill(SkillType.MERCHANT_HERO, new SkillData(
                formattedFromKey("skills_gui.skills.trader.hero"),
                Material.EMERALD_BLOCK,
                List.of(formattedFromKey("skills_gui.skills.trader.hero_desc")
                ),
                Arrays.asList(
                        new ItemStack(Material.ANCIENT_DEBRIS,2),
                        new ItemStack(Material.EMERALD_BLOCK,128)
                )
        ));

        skillManager.registerSkill(SkillType.MERCHANT_BLESSING, new SkillData(
                formattedFromKey("skills_gui.skills.trader.blessing"),
                Material.TOTEM_OF_UNDYING,
                List.of(formattedFromKey("skills_gui.skills.trader.blessing_desc")
                        ),
                Arrays.asList(
                        new ItemStack(Material.TOTEM_OF_UNDYING,1),
                        new ItemStack(Material.NETHERITE_INGOT, 3),
                        new ItemStack(Material.NETHER_STAR,1)
                )
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

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public Component formattedFromKey(String key) {
        return MiniMessage.miniMessage().deserialize(getMessageFromKey(key)).decoration(TextDecoration.ITALIC, false);
    }
}
