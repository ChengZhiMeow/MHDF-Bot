package cn.chengzhiya.mhdfbot.api.http.entity;

import cn.chengzhiya.mhdfbot.api.entity.config.YamlConfiguration;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class SSLConfig {
    private boolean enable;
    private String alias;
    private String file;
    private String key;

    public SSLConfig(YamlConfiguration config) {
        if (config == null) {
            return;
        }

        this.enable = config.getBoolean("enable");
        this.alias = config.getString("alias");
        this.file = config.getString("file");
        this.key = config.getString("key");
    }
}
