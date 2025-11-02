package cn.chengzhiya.mhdfbot;

import cn.chengzhimeow.ccyaml.CCYaml;
import cn.chengzhimeow.ccyaml.configuration.ConfigurationSection;
import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.bot.type.BotType;
import cn.chengzhiya.mhdfbot.api.plugin.data.Command;
import cn.chengzhiya.mhdfbot.api.plugin.data.PluginInfo;
import cn.chengzhiya.mhdfbot.bot.onebot.MHDFOneBot;
import cn.chengzhiya.mhdfbot.bot.qqbot.MHDFQqBot;
import cn.chengzhiya.mhdfbot.console.MHDFCommandCompleter;
import cn.chengzhiya.mhdfbot.feature.command.Help;
import cn.chengzhiya.mhdfbot.feature.command.Plugins;
import cn.chengzhiya.mhdfbot.feature.listener.MessageListener;
import cn.chengzhiya.mhdfbot.lang.Languages;
import cn.chengzhiya.mhdfbot.manager.MHDFConfigManager;
import cn.chengzhiya.mhdfbot.minecraft.MHDFMinecraftWebSocketServer;
import cn.chengzhiya.mhdfbot.thread.MHDFBotServiceThread;
import cn.chengzhiya.mhdfbot.thread.MHDFMinecraftWsServerThread;
import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;

public class Main {
    @Getter
    private static PluginInfo frameworkInfo;
    @Getter
    private static CCYaml yamlManager;
    @Getter
    private static MHDFConfigManager configManager;
    @Getter
    private static MHDFMinecraftWebSocketServer minecraftWebSocketServer;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // 读取机器人信息
        URL url = Main.class.getClassLoader().getResource("mhdfbot_info.yml");
        if (url == null) throw new RuntimeException(Languages.FILE_CORRUPTED);
        try (InputStream in = url.openStream()) {
            YamlConfiguration frameworkInfoConfig = YamlConfiguration.loadConfiguration(in);
            Main.frameworkInfo = new PluginInfo(
                    frameworkInfoConfig.getString("name"),
                    frameworkInfoConfig.getString("version"),
                    Main.class.getName(),
                    frameworkInfoConfig.getStringList("authoendrs")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 读取配置
        Main.yamlManager = new CCYaml(
                Main.class.getClassLoader(),
                new File("./"),
                Main.frameworkInfo.version()
        );
        Main.configManager = new MHDFConfigManager();

        // 启动程序
        MHDFBot.getLogger().info("===========================================");
        MHDFBot.getLogger().info("MHDF-Bot | 版本: {}", Main.frameworkInfo.version());
        MHDFBot.getLogger().info("MHDF-Bot | 作者: {}", Main.frameworkInfo.authoendrs());
        MHDFBot.getLogger().info("");
        MHDFBot.getLogger().info("Ciallo～ (∠·ω< )⌒★");
        MHDFBot.getLogger().info("\"在意的话，会让眼前的幸福逃走的，傻子才会在意。\"");
        MHDFBot.getLogger().info("===========================================");

        Main.getConfigManager().saveDefaultFile();
        Main.getConfigManager().reload();

        Main.initBot();

        Main.registerCommand();
        Main.registerListener();

        MHDFBot.getPluginManager().loadPlugins();

        Main.minecraftWebSocketServer = new MHDFMinecraftWebSocketServer();
        MHDFMinecraftWsServerThread.getInstance().execute(() -> Main.getMinecraftWebSocketServer().startServer());

        MHDFBotServiceThread.getInstance().execute(MHDFBot::init);

        MHDFBot.getLogger().info(Languages.START_DONE, System.currentTimeMillis() - startTime);

        // 关闭程序
        Runtime.getRuntime().addShutdownHook(new Thread(() -> MHDFBot.getPluginManager().unloadPlugins()));

        // 控制台
        try {
            LineReader lineReader = LineReaderBuilder.builder()
                    .completer(new MHDFCommandCompleter())
                    .build();

            String line;
            while ((line = lineReader.readLine()) != null) {
                MHDFBot.getCommandManager().executeCommand(line);
            }
        } catch (UserInterruptException e) {
            System.exit(0);
        }
    }

    /**
     * 初始化机器人
     */
    private static void initBot() {
        ConfigurationSection config = Main.getConfigManager().getData().getConfigurationSection("bot_settings");
        if (config == null) throw new NullPointerException(Languages.NOT_FOUND_BOT_CONFIG);

        MHDFBot.setBotType(BotType.valueOf(Objects.requireNonNull(config.getString("type")).toUpperCase(Locale.ROOT)));
        switch (MHDFBot.getBotType()) {
            case ONEBOT -> MHDFBot.setBot(new MHDFOneBot());
            case QQBOT -> MHDFBot.setBot(new MHDFQqBot());
            default -> throw new RuntimeException(Languages.NOT_SUPPORT_BOT_TYPE);
        }
    }

    /**
     * 注册框架自带命令
     */
    private static void registerCommand() {
        MHDFBot.getCommandManager().registerCommand(
                new Command("help").plugin(Main.frameworkInfo).executor(new Help()).description(Languages.COMMAND_HELP_DESCRIPTION).usage(Languages.COMMAND_HELP_USAGE)
        );

        MHDFBot.getCommandManager().registerCommand(
                new Command("plugins").plugin(Main.frameworkInfo).executor(new Plugins()).description(Languages.COMMAND_PLUGINS_DESCRIPTION)
        );
    }

    /**
     * 注册框架自带事件
     */
    private static void registerListener() {
        MHDFBot.getListenerManager().registerListener(Main.getFrameworkInfo(), new MessageListener());
    }
}
