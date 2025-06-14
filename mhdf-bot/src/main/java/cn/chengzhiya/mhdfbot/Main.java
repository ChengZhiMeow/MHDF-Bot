package cn.chengzhiya.mhdfbot;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.entity.plugin.Command;
import cn.chengzhiya.mhdfbot.api.entity.plugin.PluginInfo;
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

public class Main {
    @Getter
    private static final ConfigManager configManager = new ConfigManager();
    @Getter
    private static final PluginInfo frameworkInfo =
            new PluginInfo("MHDF-Bot", "2.1.1", null, Collections.singletonList("ChengZhiYa"));

    public static void main(String[] args) throws Exception {
        Long startTime = System.currentTimeMillis();

        getConfigManager().saveDefaultConfig();
        getConfigManager().reloadConfig();

        new MinecraftWebSocketServer.HeartBeat().runTaskAsynchronouslyTimer(0L, 1L);

        registerCommand();
        registerListener();

        MHDFBot.getPluginManager().loadPlugins();

        MHDFBot.getScheduler().runTaskAsynchronously(MHDFBot::init);
        MHDFBot.getScheduler().runTaskAsynchronously(() -> MHDFBot.getMinecraftWebSocketServer().startServer());

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
