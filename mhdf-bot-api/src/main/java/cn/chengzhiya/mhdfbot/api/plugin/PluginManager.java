package cn.chengzhiya.mhdfbot.api.plugin;

import java.io.IOException;
import java.util.Collection;

public interface PluginManager {
    /**
     * 获取指定插件名称的插件实例
     *
     * @param pluginName 插件名称
     * @return 插件实例
     */
    JavaPlugin getPlugin(String pluginName);

    /**
     * 获取插件实例列表
     *
     * @return 插件实例列表
     */
    Collection<JavaPlugin> getPluginList();

    /**
     * 加载插件目录下所有插件
     */
    void loadPlugins();

    /**
     * 加载指定插件
     *
     * @param pluginInfo 插件信息
     */
    void loadPlugin(PluginInfo pluginInfo);

    /**
     * 启用插件目录下所有插件
     */
    void enablePlugins();

    /**
     * 启用指定名称的插件
     *
     * @param pluginName 插件名称
     */
    void enablePlugin(String pluginName);

    /**
     * 卸载所有插件
     */
    void unloadPlugins();

    /**
     * 卸载指定名称的插件
     *
     * @param pluginName 插件名称
     */
    void unloadPlugin(String pluginName) throws IOException;

    /**
     * 重载指定名称的插件
     *
     * @param pluginName 插件名称
     */
    void reloadPlugin(String pluginName) throws IOException;
}
