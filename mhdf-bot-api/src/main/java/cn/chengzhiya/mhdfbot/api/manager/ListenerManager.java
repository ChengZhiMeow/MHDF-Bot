package cn.chengzhiya.mhdfbot.api.manager;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.entity.plugin.PluginInfo;
import cn.chengzhiya.mhdfbot.api.event.Event;
import cn.chengzhiya.mhdfbot.api.listener.EventHandler;
import cn.chengzhiya.mhdfbot.api.listener.Listener;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.HashMap;

@Getter
@SuppressWarnings("unused")
public final class ListenerManager {
    private final HashMap<Listener, PluginInfo> listenerHashMap = new HashMap<>();

    /**
     * 注册指定监听器实例
     *
     * @param pluginInfo 插件信息实例
     * @param listener   监听器实例
     */
    public void registerListener(PluginInfo pluginInfo, Listener listener) {
        this.getListenerHashMap().put(listener, pluginInfo);
    }

    /**
     * 响应指定事件实例
     *
     * @param event 事件实例
     */
    public void callEvent(Event event) {
        for (Listener listener : this.getListenerHashMap().keySet()) {
            for (Method method : listener.getClass().getMethods()) {
                if (!method.isAnnotationPresent(EventHandler.class)) {
                    continue;
                }

                if (method.getParameters().length != 1) {
                    continue;
                }

                if (!method.getParameters()[0].getType().equals(event.getClass())) {
                    continue;
                }

                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    MHDFBot.getLogger().error("在处理监听器 {} 的时候遇到了问题:",
                            listener.getClass()
                    );
                    e.printStackTrace();
                }
            }
        }
    }
}
