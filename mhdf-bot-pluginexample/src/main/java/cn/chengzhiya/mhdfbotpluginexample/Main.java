package cn.chengzhiya.mhdfbotpluginexample;

import cn.chengzhiya.mhdfbot.api.plugin.JavaPlugin;

public final class Main extends JavaPlugin {
    @Override
    public void onEnable() {
        super.getLogger().info("hello World!");
    }

    @Override
    public void onDisable() {
        super.getLogger().info("goodbye World!");
    }
}
