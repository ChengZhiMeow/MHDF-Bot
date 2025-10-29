package cn.chengzhiya.mhdfbot.manager;

import cn.chengzhimeow.ccyaml.manager.AbstractYamlManager;
import cn.chengzhiya.mhdfbot.Main;
import lombok.Getter;

@Getter
public final class ConfigManager extends AbstractYamlManager {
    public ConfigManager() {
        super(Main.getYamlManager());
    }

    @Override
    public String originFilePath() {
        return "";
    }

    @Override
    public String filePath() {
        return "";
    }
}
