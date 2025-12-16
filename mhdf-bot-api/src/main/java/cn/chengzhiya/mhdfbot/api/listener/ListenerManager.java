package cn.chengzhiya.mhdfbot.api.listener;

import cn.chengzhiya.mhdfbot.api.event.Event;
import cn.chengzhiya.mhdfbot.api.plugin.PluginInfo;

public interface ListenerManager {
    /**
     * 注册指定监听器实例
     *
     * @param pluginInfo 插件信息实例
     * @param listener   监听器实例
     */
    void registerListener(PluginInfo pluginInfo, Listener listener);

    /**
     * 取消注册指定监听器实例
     *
     * @param listener 监听器实例
     */
    void unregisterListener(Listener listener);

    /**
     * 取消注册指定插件信息实例的所有监听器
     *
     * @param pluginInfo 插件信息实例
     */
    void unregisterAllListener(PluginInfo pluginInfo);

    /**
     * 响应指定事件实例
     *
     * @param event 事件实例
     */
    void callEvent(Event event);
}
