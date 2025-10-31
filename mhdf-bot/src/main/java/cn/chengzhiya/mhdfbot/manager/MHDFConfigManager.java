package cn.chengzhiya.mhdfbot.manager;

import cn.chengzhimeow.ccyaml.manager.AbstractYamlManager;
import cn.chengzhiya.mhdfbot.Main;
import lombok.Getter;

@Getter
public final class MHDFConfigManager extends AbstractYamlManager {
    public MHDFConfigManager() {
        super(Main.getYamlManager());
    }

    @Override
    public String originFilePath() {
        return "mhdfbot_config.yml";
    }

    @Override
    public String filePath() {
        return "config.yml";
    }
}
