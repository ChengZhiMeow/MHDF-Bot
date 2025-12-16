package cn.chengzhiya.mhdfbot.api.plugin;

import cn.chengzhimeow.ccyaml.configuration.yaml.YamlConfiguration;
import cn.chengzhiya.mhdfbot.api.bot.type.BotType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class PluginInfo {
    public static PluginInfo by(File file, YamlConfiguration config) {
        String name = config.getString("name");
        String version = config.getString("version");
        String main = config.getString("main");
        List<String> authors = config.getStringList("authors");
        List<BotType> supportFramework = new ArrayList<>();
        for (String type : config.getStringList("support_framework")) {
            supportFramework.add(BotType.valueOf(type.toUpperCase(Locale.ROOT)));
        }
        String mhdfbotVersion = config.getString("mhdfbot_version");
        List<String> depend = config.getStringList("depend");
        List<String> softdepend = config.getStringList("softdepend");

        return new PluginInfo(
                file,
                Objects.requireNonNull(name),
                Objects.requireNonNull(version),
                Objects.requireNonNull(main),
                authors,
                supportFramework,
                mhdfbotVersion,
                depend,
                softdepend
        );
    }

    private final File file;
    private final String name;
    private final String version;
    private final String main;
    private final List<String> authors;
    private final List<BotType> supportFrameworks;
    private final String mhdfbotVersion;
    private final List<String> depend;
    private final List<String> softdepend;
    private PluginStatus status = PluginStatus.NO_LOAD;

    public PluginInfo(@Nullable File file, @NotNull String name, @NotNull String version, @NotNull String main, @NotNull List<String> authors, @NotNull List<BotType> supportFrameworks, @Nullable String mhdfbotVersion, @NotNull List<String> depend, @NotNull List<String> softdepend) {
        this.file = file;
        this.name = name;
        this.version = version;
        this.main = main;
        this.authors = authors;
        this.supportFrameworks = supportFrameworks;
        this.mhdfbotVersion = mhdfbotVersion;
        this.depend = depend;
        this.softdepend = softdepend;
    }

    public @Nullable File file() {
        return this.file;
    }

    public @NotNull String name() {
        return this.name;
    }

    public @NotNull String version() {
        return this.version;
    }

    @SuppressWarnings("ConfusingMainMethod")
    public @NotNull String main() {
        return this.main;
    }

    public @NotNull List<String> authors() {
        return this.authors;
    }

    public @NotNull List<BotType> supportFrameworks() {
        return this.supportFrameworks;
    }

    public @Nullable String mhdfbotVersion() {
        return this.mhdfbotVersion;
    }

    public @NotNull List<String> depend() {
        return this.depend;
    }

    public @NotNull List<String> softdepend() {
        return this.softdepend;
    }

    public @NotNull PluginStatus status() {
        return this.status;
    }

    public void status(@NotNull PluginStatus pluginStatus) {
        this.status = pluginStatus;
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