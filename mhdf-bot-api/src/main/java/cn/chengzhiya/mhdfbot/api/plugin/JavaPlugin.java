package cn.chengzhiya.mhdfbot.api.plugin;

import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.listener.Listener;
import cn.chengzhiya.mhdfbot.api.plugin.data.Command;
import lombok.Getter;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Getter
@SuppressWarnings("unused")
public abstract class JavaPlugin implements Plugin {
    private PluginInfo pluginInfo;
    private YamlConfiguration config;
    private Logger logger;

    /**
     * 注册命令实例
     *
     * @param command 命令实例
     */
    public void registerCommand(Command command) {
        MHDFBot.getCommandManager().registerCommand(command);
    }

    /**
     * 注册指定监听器实例
     *
     * @param listener 监听器实例
     */
    public void registerListener(Listener listener) {
        MHDFBot.getListenerManager().registerListener(this.pluginInfo, listener);
    }

    /**
     * 保存默认配置文件
     */
    public void saveDefaultConfig() {
        if (!this.getDataFolder().exists()) // noinspection ResultOfMethodCallIgnored
            this.getDataFolder().mkdirs();
        this.saveResource("config.yml", "config.yml", false);
    }

    /**
     * 重载配置文件
     */
    public void reloadConfig() {
        try {
            this.config = YamlConfiguration.loadConfiguration(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 保存资源
     *
     * @param filePath     保存目录
     * @param resourcePath 资源目录
     * @param replace      替换文件
     */
    public void saveResource(String filePath, String resourcePath, boolean replace) {
        File file = new File(this.getDataFolder(), filePath);
        if (file.exists() && !replace) {
            return;
        }

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL url = classLoader.getResource(resourcePath);
        if (url == null) throw new RuntimeException("找不到资源: " + resourcePath);

        try (InputStream in = classLoader.getResourceAsStream(resourcePath)) {
            try (FileOutputStream out = new FileOutputStream(file)) {
                if (in == null) {
                    throw new RuntimeException("读取资源 " + resourcePath + " 的时候发生了错误");
                }

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取插件数据目录实例
     *
     * @return 数据目录实例
     */
    public File getDataFolder() {
        return new File("./plugins/" + this.pluginInfo.name());
    }
}
