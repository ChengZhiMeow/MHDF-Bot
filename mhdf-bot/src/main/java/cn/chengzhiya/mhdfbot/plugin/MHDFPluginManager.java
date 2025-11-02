package cn.chengzhiya.mhdfbot.plugin;

import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.plugin.JavaPlugin;
import cn.chengzhiya.mhdfbot.api.plugin.PluginManager;
import cn.chengzhiya.mhdfbot.api.plugin.PluginStatus;
import cn.chengzhiya.mhdfbot.api.plugin.data.PluginInfo;
import cn.chengzhiya.mhdfbot.lang.Languages;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Getter
@SuppressWarnings({"CallToPrintStackTrace", "unused"})
public final class MHDFPluginManager implements PluginManager {
    private final File pluginFolder = new File("./plugins");
    private final Map<String, PluginInfo> pluginHashMap = new ConcurrentHashMap<>();

    @Override
    public PluginInfo getPlugin(String pluginName) {
        return this.pluginHashMap.get(pluginName);
    }

    @Override
    public Collection<PluginInfo> getPluginList() {
        return this.pluginHashMap.values();
    }

    @Override
    public void loadPlugins() {
        if (!this.pluginFolder.exists()) // noinspection ResultOfMethodCallIgnored
            this.pluginFolder.mkdirs();
        for (File file : Objects.requireNonNull(this.pluginFolder.listFiles())) {
            if (!file.getName().endsWith(".jar")) continue;
            this.loadPlugin(file);
        }
    }

    @Override
    public void loadPlugin(File pluginFile) {
        try (JarFile jarFile = new JarFile(pluginFile)) {
            // 读取 plugin.yml
            PluginInfo pluginInfo;
            {
                JarEntry pluginInfoFile = jarFile.getJarEntry("plugin.yml");
                if (pluginInfoFile == null) {
                    MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_INVALID, pluginFile.getName());
                    return;
                }

                YamlConfiguration pluginInfoData = YamlConfiguration.loadConfiguration(jarFile.getInputStream(pluginInfoFile));
                pluginInfo = new PluginInfo(
                        pluginInfoData.getString("name"),
                        pluginInfoData.getString("version"),
                        pluginInfoData.getString("main"),
                        pluginInfoData.getStringList("authors")
                );
            }

            MHDFBot.getLogger().info(Languages.PLUGIN_LOADING, pluginInfo.name(), pluginInfo.version());
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                // 改用插件类加载器
                URL[] urls = {pluginFile.toURI().toURL()};
                MHDFPluginClassLoader classLoader = new MHDFPluginClassLoader(urls, MHDFBot.class.getClassLoader(), this);
                Thread.currentThread().setContextClassLoader(classLoader);

                Class<?> clazz = classLoader.loadClass(pluginInfo.main());

                // 初始化插件主类
                JavaPlugin javaPlugin = (JavaPlugin) clazz.getDeclaredConstructor().newInstance();
                pluginInfo.plugin(javaPlugin);
                pluginInfo.jarFile(pluginFile);

                Field field = javaPlugin.getClass().getSuperclass().getDeclaredField("pluginInfo");
                field.setAccessible(true);
                field.set(javaPlugin, pluginInfo);

                javaPlugin.onEnable();
                pluginInfo.pluginStatus(PluginStatus.LOAD_DONE);
                this.getPluginHashMap().put(pluginInfo.name(), pluginInfo);
            } catch (Throwable e) {
                pluginInfo.pluginStatus(PluginStatus.LOAD_ERROR);
                MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_THROW_EXCEPTION, pluginInfo.name());
                e.printStackTrace();
            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        } catch (Throwable e) {
            // noinspection LoggingSimilarMessage
            MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_THROW_EXCEPTION, pluginFile.getName());
            e.printStackTrace();
        }
    }

    @Override
    public void unloadPlugins() {
        this.pluginHashMap.keySet().forEach(k -> {
            try {
                this.unloadPlugin(k);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unloadPlugin(String pluginName) throws IOException {
        PluginInfo pluginInfo = this.getPlugin(pluginName);
        if (pluginInfo == null) return;

        // 卸载插件
        MHDFBot.getLogger().info(Languages.PLUGIN_UNLOADING, pluginInfo.name(), pluginInfo.version());
        JavaPlugin plugin = pluginInfo.plugin();
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            plugin.onDisable();

            // 卸载插件的监听器和命令
            MHDFBot.getListenerManager().unregisterAllListener(pluginInfo);
            MHDFBot.getCommandManager().unregisterAllCommand(pluginInfo);

            // 删除插件
            this.pluginHashMap.remove(pluginName);
        } catch (Throwable e) {
            MHDFBot.getLogger().error(Languages.PLUGIN_UNLOADING_ERROR_THROW_EXCEPTION, pluginName, e);
        }
    }

    @Override
    public void reloadPlugin(String pluginName) throws IOException {
        PluginInfo pluginInfo = this.getPlugin(pluginName);
        if (pluginInfo == null) return;

        File pluginFile = pluginInfo.jarFile();
        this.unloadPlugin(pluginName);
        this.loadPlugin(pluginFile);
    }
}
