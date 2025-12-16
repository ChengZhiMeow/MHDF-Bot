package cn.chengzhiya.mhdfbot.api.plugin.data;

import cn.chengzhiya.mhdfbot.api.plugin.PluginStatus;

import java.io.File;
import java.util.List;
import java.util.Objects;

public final class PluginInfo {
    private final String name;
    private final String version;
    private final String main;
    private final List<String> authors;
    private File jarFile;
    private PluginStatus pluginStatus;

    public PluginInfo(String name, String version, String main, List<String> authors) {
        this.name = name;
        this.version = version;
        this.main = main;
        this.authors = authors;
    }

    public String name() {
        return this.name;
    }

    public String version() {
        return this.version;
    }

    public String main() {
        return this.main;
    }

    public List<String> authors() {
        return this.authors;
    }

    public File jarFile() {
        return this.jarFile;
    }

    public PluginStatus pluginStatus() {
        return this.pluginStatus;
    }

    public void jarFile(File jarFile) {
        this.jarFile = jarFile;
    }

    public void pluginStatus(PluginStatus pluginStatus) {
        this.pluginStatus = pluginStatus;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PluginInfo pluginInfo)) return false;
        if (pluginInfo == this) return true;
        return pluginInfo.name.equals(this.name) &&
                pluginInfo.version.equals(this.version) &&
                pluginInfo.main.equals(this.main) &&
                pluginInfo.authors.equals(this.authors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.version, this.main, this.authors);
    }

    @Override
    public String toString() {
        return "PluginInfo[name=" + this.name + ", version=" + this.version + ", main=" + this.main + ", authoendrs=" + this.authors + "]";
    }
}