package cn.chengzhiya.mhdfbot.plugin;

import cn.chengzhiya.mhdfbot.api.plugin.data.PluginInfo;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MHDFPluginClassLoader extends URLClassLoader {
    private final MHDFPluginManager pluginManager;
    private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

    public MHDFPluginClassLoader(URL[] urls, ClassLoader parent, MHDFPluginManager pluginManager) {
        super(urls, parent);
        this.pluginManager = pluginManager;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cachedClass = this.classCache.get(name);
        if (cachedClass != null) {
            return cachedClass;
        }
        try {
            Class<?> clazz = super.findClass(name);
            this.classCache.put(name, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            for (PluginInfo pluginInfo : this.pluginManager.getPluginList()) {
                ClassLoader classLoader = pluginInfo.plugin().getClass().getClassLoader();
                if (classLoader instanceof MHDFPluginClassLoader && classLoader != this) {
                    try {
                        return ((MHDFPluginClassLoader) classLoader).findClassLocal(name);
                    } catch (ClassNotFoundException ignored) {
                    }
                }
            }

            throw e;
        }
    }

    public Class<?> findClassLocal(String name) throws ClassNotFoundException {
        Class<?> cachedClass = this.classCache.get(name);
        if (cachedClass != null) return cachedClass;

        Class<?> clazz = super.findClass(name);
        this.classCache.put(name, clazz);
        return clazz;
    }
}
