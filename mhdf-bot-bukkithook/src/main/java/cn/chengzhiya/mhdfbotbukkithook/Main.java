package cn.chengzhiya.mhdfbotbukkithook;

import cn.chengzhiya.mhdfbotbukkithook.minecraft.WebSocketClient;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public static Main instance;
    @Getter
    private static WebSocketClient webSocketClient;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Main.instance = this;

        super.saveDefaultConfig();
        super.reloadConfig();

        Main.webSocketClient = new WebSocketClient();
        Main.getWebSocketClient().connectServer();

        super.getLogger().info("梦之机器人框架服务端Hook已启动!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Main.instance = null;

        super.getLogger().info("梦之机器人框架服务端Hook已卸载!");
    }
}
