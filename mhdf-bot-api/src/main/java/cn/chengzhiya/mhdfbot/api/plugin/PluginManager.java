package cn.chengzhiya.mhdfbot.api.plugin;

import cn.chengzhiya.mhdfbot.api.plugin.data.PluginInfo;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface PluginManager {
    /**
     * 获取指定插件名称的插件实例
     *
     * @param pluginName 插件名称
     * @return 插件实例
     */
    PluginInfo getPlugin(String pluginName);

    /**
     * 获取插件实例列表
     *
     * @return 插件实例列表
     */
    Collection<PluginInfo> getPluginList();

    /**
     * 加载插件目录下所有插件
     */
    void loadPlugins();

    /**
     * 加载指定插件文件
     *
     * @param pluginPath 插件文件路径
     */
    void loadPlugin(File pluginPath);

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
