package cn.chengzhiya.mhdfbot.listener;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.Event;
import cn.chengzhiya.mhdfbot.api.listener.EventHandler;
import cn.chengzhiya.mhdfbot.api.listener.Listener;
import cn.chengzhiya.mhdfbot.api.listener.ListenerManager;
import cn.chengzhiya.mhdfbot.api.plugin.PluginInfo;
import cn.chengzhiya.mhdfbot.lang.Languages;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public final class MHDFListenerManager implements ListenerManager {
    private final Map<Class<? extends Event>, List<RegisterListener>> map = new HashMap<>();

    @Override
    public void registerListener(PluginInfo pluginInfo, Listener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (!method.isAnnotationPresent(EventHandler.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> param = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(param)) continue;

            // noinspection unchecked
            Class<? extends Event> event = (Class<? extends Event>) param;
            List<RegisterListener> list = this.map.computeIfAbsent(event, k -> new CopyOnWriteArrayList<>());
            list.add(new RegisterListener(listener, method, pluginInfo));
        }
    }

    @Override
    public void unregisterListener(Listener listener) {
        for (List<RegisterListener> list : this.map.values()) {
            list.removeIf(registerListener -> registerListener.listener().equals(listener));
        }
    }

    @Override
    public void unregisterAllListener(PluginInfo pluginInfo) {
        for (List<RegisterListener> list : this.map.values()) {
            list.removeIf(registerListener -> registerListener.pluginInfo().equals(pluginInfo));
        }
    }

    @Override
    public void callEvent(Event event) {
        List<RegisterListener> list = this.map.get(event.getClass());
        if (list == null || list.isEmpty()) return;

        for (RegisterListener registerListener : list) {
            try {
                registerListener.handler().invoke(registerListener.listener(), event);
            } catch (Exception e) {
                MHDFBot.getLogger().error(Languages.CALL_EVENT_THROW_EXCEPTION,
                        registerListener.handler().getDeclaringClass().getName(), e
                );
            }
        }
    }

    private record RegisterListener(Listener listener, Method handler, PluginInfo pluginInfo) {
    }
}
