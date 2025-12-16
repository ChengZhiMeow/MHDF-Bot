package cn.chengzhiya.mhdfbot.plugin;

import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.bot.type.BotType;
import cn.chengzhiya.mhdfbot.api.log.LoggerManager;
import cn.chengzhiya.mhdfbot.api.plugin.JavaPlugin;
import cn.chengzhiya.mhdfbot.api.plugin.PluginInfo;
import cn.chengzhiya.mhdfbot.api.plugin.PluginManager;
import cn.chengzhiya.mhdfbot.api.plugin.PluginStatus;
import cn.chengzhiya.mhdfbot.lang.Languages;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"CallToPrintStackTrace", "unused", "LoggingSimilarMessage"})
public final class MHDFPluginManager implements PluginManager {
    private final Map<String, JavaPlugin> pluginMap = new ConcurrentHashMap<>();
    private final Map<String, PluginInfo> toLoad = new HashMap<>();

    public MHDFPluginManager() {
        File pluginFolder = new File("./plugins");
        // 创建插件目录
        if (!pluginFolder.exists())
            // noinspection ResultOfMethodCallIgnored
            pluginFolder.mkdirs();

        // 获取要加载的插件列表
        for (File file : Objects.requireNonNull(pluginFolder.listFiles())) {
            if (!file.getName().endsWith(".jar")) continue;

            try (JarFile jarFile = new JarFile(file)) {
                // 读取 plugin.yml
                JarEntry pluginInfoFile = jarFile.getJarEntry("plugin.yml");
                if (pluginInfoFile == null) {
                    MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_INVALID, file.getName());
                    continue;
                }

                YamlConfiguration pluginInfoData = YamlConfiguration.loadConfiguration(jarFile.getInputStream(pluginInfoFile));
                PluginInfo pluginInfo = PluginInfo.by(file, pluginInfoData);

                // 检查框架类型是否匹配
                List<BotType> supportFrameworks = pluginInfo.supportFrameworks();
                if (!supportFrameworks.isEmpty()) {
                    if (!supportFrameworks.contains(MHDFBot.getBotType())) {
                        MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_NOT_SUPPORT_FRAMEWORK, pluginInfo.name(), supportFrameworks.toString());
                        continue;
                    }
                }

                // 检查框架版本是否匹配
                String mhdfbotVersion = pluginInfo.mhdfbotVersion();
                if (mhdfbotVersion != null) {
                    String frameworkVersion = Main.getFrameworkInfo().version();
                    int currentVersion = Integer.parseInt(frameworkVersion.replace(".", ""));
                    int targetVersion = Integer.parseInt(mhdfbotVersion.replace(".", ""));
                    if (currentVersion < targetVersion) {
                        MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_NOT_SUPPORT_MHDFBOT_VERSION, pluginInfo.name(), mhdfbotVersion, frameworkVersion);
                        continue;
                    }
                }

                this.toLoad.put(pluginInfo.name(), pluginInfo);
            } catch (Throwable e) {
                // noinspection LoggingSimilarMessage
                MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_THROW_EXCEPTION, file.getName());
                e.printStackTrace();
            }
        }
    }

    @Override
    public JavaPlugin getPlugin(String pluginName) {
        return this.pluginMap.get(pluginName);
    }

    @Override
    public Collection<JavaPlugin> getPluginList() {
        return this.pluginMap.values();
    }

    @Override
    public void loadPlugins() {
        Map<String, PluginInfo> plugins = new HashMap<>(this.toLoad);
        List<PluginInfo> pluginList = new ArrayList<>(plugins.values());

        // 过滤硬依赖未安装的插件
        Queue<String> noLoadQueue = new LinkedList<>();
        for (PluginInfo pluginInfo : pluginList) {
            for (String depend : pluginInfo.depend()) {
                if (plugins.containsKey(depend)) continue;

                plugins.remove(pluginInfo.name());
                noLoadQueue.add(pluginInfo.name());
                MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_NOT_INSTALL_DEPEND, pluginInfo.name(), depend);
                break;
            }
        }

        // 递归移除硬依赖缺失插件
        while (!noLoadQueue.isEmpty()) {
            String name = noLoadQueue.poll();
            PluginInfo pluginInfo = plugins.get(name);
            for (String depend : pluginInfo.depend()) {
                plugins.remove(depend);
                noLoadQueue.add(depend);
                MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_NOT_INSTALL_DEPEND, name, depend);
            }
        }

        // 统计依赖数量
        Map<String, Integer> pluginDependCount = new HashMap<>();
        Map<String, List<PluginInfo>> reverseDependencies = new HashMap<>();
        for (PluginInfo pluginInfo : plugins.values()) {
            // noinspection MappingBeforeCount
            int degree = (int) Stream.concat(pluginInfo.depend().stream(), pluginInfo.softdepend().stream())
                    .filter(plugins::containsKey)
                    .peek(depend -> reverseDependencies.computeIfAbsent(depend, k -> new ArrayList<>()).add(pluginInfo))
                    .count();
            pluginDependCount.put(pluginInfo.name(), degree);
        }

        // 排序插件列表
        List<PluginInfo> sortedPlugins = new ArrayList<>();
        Queue<PluginInfo> queue = plugins.values().stream()
                .filter(pluginInfo -> pluginDependCount.getOrDefault(pluginInfo.name(), 0) == 0) // 优先加载无依赖插件
                .collect(Collectors.toCollection(LinkedList::new));
        while (!queue.isEmpty()) {
            PluginInfo current = queue.poll();
            sortedPlugins.add(current);
            reverseDependencies.getOrDefault(current.name(), Collections.emptyList())
                    .forEach(dependent -> {
                        // noinspection DataFlowIssue
                        if (pluginDependCount.compute(dependent.name(), (k, v) -> v - 1) != 0) return;
                        queue.add(dependent);
                    });
        }

        // 检查循环依赖
        if (sortedPlugins.size() < plugins.size()) {
            MHDFBot.getLogger().error(Languages.PLUGIN_LOAD_ERROR_CANNOT_SORT_PLUGINS);
            Set<PluginInfo> sortedSet = new HashSet<>(sortedPlugins);
            plugins.values().stream()
                    .filter(plugin -> !sortedSet.contains(plugin))
                    .forEach(plugin -> MHDFBot.getLogger().error("- {}", plugin.name()));
        }

        for (PluginInfo pluginInfo : sortedPlugins) {
            this.loadPlugin(pluginInfo);
        }
    }

    @Override
    public void loadPlugin(PluginInfo pluginInfo) {
        File pluginFile = pluginInfo.file();
        if (pluginFile == null) return;

        try (JarFile jarFile = new JarFile(pluginFile)) {
            MHDFBot.getLogger().info(Languages.PLUGIN_LOADING, pluginInfo.name(), pluginInfo.version());
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                // 改用插件类加载器
                URL[] urls = {pluginFile.toURI().toURL()};
                MHDFPluginClassLoader classLoader = new MHDFPluginClassLoader(urls, MHDFBot.class.getClassLoader(), this);
                Thread.currentThread().setContextClassLoader(classLoader);

                Class<?> clazz = classLoader.loadClass(pluginInfo.main());

                // 初始化插件主类
                JavaPlugin plugin = (JavaPlugin) clazz.getDeclaredConstructor().newInstance();

                // 写入插件信息数据
                Field field = plugin.getClass().getSuperclass().getDeclaredField("pluginInfo");
                field.setAccessible(true);
                field.set(plugin, pluginInfo);

                pluginInfo.status(PluginStatus.LOAD_DONE);
                this.pluginMap.put(pluginInfo.name(), plugin);
            } catch (Throwable e) {
                pluginInfo.status(PluginStatus.LOAD_ERROR);
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
    public void enablePlugins() {
        for (String name : this.pluginMap.keySet()) {
            this.enablePlugin(name);
        }
    }

    @Override
    public void enablePlugin(String pluginName) {
        JavaPlugin plugin = this.getPlugin(pluginName);
        PluginInfo pluginInfo = plugin.getPluginInfo();
        if (pluginInfo.status() != PluginStatus.LOAD_DONE) return;

        try {
            MHDFBot.getLogger().info(Languages.PLUGIN_ENABLING, pluginInfo.name(), pluginInfo.version());

            Field field = plugin.getClass().getSuperclass().getDeclaredField("logger");
            field.setAccessible(true);
            field.set(plugin, LoggerManager.getInstance().getLogger(pluginInfo.name()));

            plugin.onEnable();
            pluginInfo.status(PluginStatus.ENABLE_DONE);
        } catch (Throwable e) {
            pluginInfo.status(PluginStatus.ENABLE_ERROR);
            MHDFBot.getLogger().error(Languages.PLUGIN_ENABLE_ERROR_THROW_EXCEPTION, pluginInfo.name());
            e.printStackTrace();
        }
    }

    @Override
    public void unloadPlugins() {
        this.pluginMap.keySet().forEach(k -> {
            try {
                this.unloadPlugin(k);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void unloadPlugin(String pluginName) throws IOException {
        JavaPlugin plugin = this.getPlugin(pluginName);
        PluginInfo pluginInfo = plugin.getPluginInfo();

        // 卸载插件
        MHDFBot.getLogger().info(Languages.PLUGIN_UNLOADING, pluginInfo.name(), pluginInfo.version());
        ClassLoader pluginClassLoader = plugin.getClass().getClassLoader();

        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            plugin.onDisable();

            // 卸载插件的监听器和命令
            MHDFBot.getListenerManager().unregisterAllListener(pluginInfo);
            MHDFBot.getCommandManager().unregisterAllCommand(pluginInfo);

            // 删除插件
            this.pluginMap.remove(pluginName);
        } catch (Throwable e) {
            MHDFBot.getLogger().error(Languages.PLUGIN_UNLOADING_ERROR_THROW_EXCEPTION, pluginName, e);
        }
    }

    @Override
    public void reloadPlugin(String pluginName) throws IOException {
        JavaPlugin plugin = this.getPlugin(pluginName);
        PluginInfo pluginInfo = plugin.getPluginInfo();

        this.unloadPlugin(pluginName);
        this.loadPlugin(pluginInfo);
        this.enablePlugin(pluginInfo.name());
    }
}
