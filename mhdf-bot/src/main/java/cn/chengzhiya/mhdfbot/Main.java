package cn.chengzhiya.mhdfbot;

import cn.chengzhimeow.ccyaml.CCYaml;
import cn.chengzhimeow.ccyaml.configuration.ConfigurationSection;
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
import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class Main {
    @Getter
    private static PluginInfo frameworkInfo;
    @Getter
    private static CCYaml yamlManager;
    @Getter
    private static MHDFConfigManager configManager;
    @Getter
    private static MHDFMinecraftWebSocketServer minecraftWebSocketServer;

    public static void main(String[] args) throws Exception {
        Main.frameworkInfo = new PluginInfo("MHDF-Bot", "2.1.4", Main.class.getName(), List.of("ChengZhiMeow"));
        Main.yamlManager = new CCYaml(
                Main.class.getClassLoader(),
                new File("./"),
                Main.frameworkInfo.version()
        );
        Main.configManager = new MHDFConfigManager();

        long startTime = System.currentTimeMillis();
        {
            Main.getConfigManager().saveDefaultFile();
            Main.getConfigManager().reload();

            Main.initBot();

            Main.registerCommand();
            Main.registerListener();

            MHDFBot.getPluginManager().loadPlugins();

            MHDFBot.getScheduler().runTask(MHDFBot::init);

            Main.minecraftWebSocketServer = new MHDFMinecraftWebSocketServer();
            MHDFBot.getScheduler().runTask(() -> Main.getMinecraftWebSocketServer().startServer());
        }
        MHDFBot.getLogger().info(Languages.START_DONE, System.currentTimeMillis() - startTime);

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
        ConfigurationSection botConfig = Main.getConfigManager().getData().getConfigurationSection("botSettings");
        if (botConfig == null) throw new RuntimeException("机器人配置错误!");

        MHDFBot.setBotType(BotType.valueOf(botConfig.getString("type").toUpperCase(Locale.ROOT)));
        switch (MHDFBot.getBotType()) {
            case ONEBOT -> MHDFBot.setBot(new MHDFOneBot());
            case QQBOT -> MHDFBot.setBot(new MHDFQqBot());
            default -> throw new RuntimeException("不支持的机器人类型!");
        }
    }

    /**
     * 注册框架自带命令
     */
    private static void registerCommand() {
        MHDFBot.getCommandManager().registerCommand(
                new Command("help").plugin(Main.frameworkInfo).executor(new Help()).description("查看命令帮助").usage("help <页数>")
        );

        MHDFBot.getCommandManager().registerCommand(
                new Command("plugins").plugin(Main.frameworkInfo).executor(new Plugins()).description("查看插件列表")
        );
    }

    /**
     * 注册框架自带事件
     */
    private static void registerListener() {
        MHDFBot.getListenerManager().registerListener(Main.getFrameworkInfo(), new MessageListener());
    }
}
