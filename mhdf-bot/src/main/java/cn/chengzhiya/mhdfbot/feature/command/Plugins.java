package cn.chengzhiya.mhdfbot.feature.command;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.command.CommandExecutor;
import cn.chengzhiya.mhdfbot.api.plugin.JavaPlugin;
import cn.chengzhiya.mhdfbot.api.plugin.PluginStatus;
import cn.chengzhiya.mhdfbot.api.plugin.data.PluginInfo;

import java.util.Collection;
import java.util.List;

public final class Plugins implements CommandExecutor {
    @Override
    public void onCommand(String command, String[] args) {
        Collection<JavaPlugin> pluginInfoList = MHDFBot.getPluginManager().getPluginList();

        // 输出已加载插件实例列表
        this.printPluginList(pluginInfoList, "已加载插件", PluginStatus.LOAD_DONE);

        // 输出未加载插件实例列表
        this.printPluginList(pluginInfoList, "未加载插件", PluginStatus.LOAD_ERROR);
    }

    /**
     * 输出插件实例列表
     *
     * @param pluginInfoList 插件实例列表
     * @param prefix         前缀
     * @param pluginStatus   插件状态
     */
    private void printPluginList(Collection<JavaPlugin> pluginInfoList, String prefix, PluginStatus pluginStatus) {
        List<PluginInfo> filterPluginInfoList = pluginInfoList.stream()
                .map(JavaPlugin::getPluginInfo)
                .filter(pluginInfo -> pluginInfo.pluginStatus() == pluginStatus)
                .toList();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix).append("(").append(filterPluginInfoList.size()).append(")").append(": ");
        filterPluginInfoList.forEach(pluginInfo -> stringBuilder.append(pluginInfo.name()).append(", "));

        MHDFBot.getLogger().info(stringBuilder.toString());
    }
}
