package cn.chengzhiya.mhdfbot;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.entity.config.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.entity.plugin.Command;
import cn.chengzhiya.mhdfbot.api.entity.plugin.PluginInfo;
import cn.chengzhiya.mhdfbot.api.enums.bot.BotType;
import cn.chengzhiya.mhdfbot.bot.OneBotImpl;
import cn.chengzhiya.mhdfbot.bot.QqBotImpl;
import cn.chengzhiya.mhdfbot.command.Help;
import cn.chengzhiya.mhdfbot.command.Plugins;
import cn.chengzhiya.mhdfbot.console.CommandCompleter;
import cn.chengzhiya.mhdfbot.listener.MessageListener;
import cn.chengzhiya.mhdfbot.manager.ConfigManager;
import cn.chengzhiya.mhdfbot.minecraft.MinecraftWebSocketServer;
import lombok.Getter;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;

import java.util.Collections;
import java.util.Locale;

public class Main {
    @Getter
    private static final ConfigManager configManager = new ConfigManager();

    @Getter
    private static MinecraftWebSocketServer minecraftWebSocketServer;

    @Getter
    private static final PluginInfo frameworkInfo =
            new PluginInfo("MHDF-Bot", "2.1.2", null, Collections.singletonList("ChengZhiYa"));

    public static void main(String[] args) throws Exception {
        Long startTime = System.currentTimeMillis();

        getConfigManager().saveDefaultConfig();
        getConfigManager().reloadConfig();

        initBot();

        registerCommand();
        registerListener();

        MHDFBot.getPluginManager().loadPlugins();

        MHDFBot.getScheduler().runTaskAsynchronously(MHDFBot::init);

        minecraftWebSocketServer = new MinecraftWebSocketServer();
        MHDFBot.getScheduler().runTaskAsynchronously(() -> getMinecraftWebSocketServer().startServer());

        Long endTime = System.currentTimeMillis();
        MHDFBot.getLogger().info("启动成功,本次启动时长: {}ms", endTime - startTime);

        try {
            LineReader lineReader = LineReaderBuilder.builder()
                    .completer(new CommandCompleter())
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
        YamlConfiguration botConfig = Main.getConfigManager().getConfig().getConfigurationSection("botSettings");
        if (botConfig == null) {
            throw new RuntimeException("机器人配置错误!");
        }

        MHDFBot.setBotType(BotType.valueOf(botConfig.getString("type").toUpperCase(Locale.ROOT)));
        switch (MHDFBot.getBotType()) {
            case ONEBOT -> MHDFBot.setBot(new OneBotImpl());
            case QQBOT -> MHDFBot.setBot(new QqBotImpl());
            default -> throw new RuntimeException("不支持的机器人类型!");
        }
    }

    /**
     * 注册框架自带命令
     */
    private static void registerCommand() {
        MHDFBot.getCommandManager().registerCommand(
                new Command("help").plugin(frameworkInfo).executor(new Help()).description("查看命令帮助").usage("help <页数>")
        );

        MHDFBot.getCommandManager().registerCommand(
                new Command("plugins").plugin(frameworkInfo).executor(new Plugins()).description("查看插件列表")
        );
    }

    /**
     * 注册框架自带事件
     */
    private static void registerListener() {
        MHDFBot.getListenerManager().registerListener(Main.getFrameworkInfo(), new MessageListener());
    }
}
