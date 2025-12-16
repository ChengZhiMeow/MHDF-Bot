package cn.chengzhiya.mhdfbot.api.command;

import cn.chengzhiya.mhdfbot.api.plugin.PluginInfo;
import cn.chengzhiya.mhdfbot.api.plugin.data.Command;

import java.util.Collection;
import java.util.List;

public interface CommandManager {
    /**
     * 注册命令实例
     *
     * @param command 命令实例
     */
    void registerCommand(Command command);

    /**
     * 取消注册指定命令
     *
     * @param command 命令
     */
    void unregisterCommand(String command);

    /**
     * 取消注册指定插件信息实例的所有命令
     *
     * @param pluginInfo 插件信息实例
     */
    void unregisterAllCommand(PluginInfo pluginInfo);

    /**
     * 获取指定命令的命令实例
     *
     * @param command 命令
     * @return 命令实例
     */
    Command getCommand(String command);

    /**
     * 获取命令实例列表
     *
     * @return 命令实例列表
     */
    Collection<Command> getCommandList();

    /**
     * 执行指定命令
     *
     * @param commandString 完整命令
     */
    void executeCommand(String commandString);

    /**
     * 补全指定命令
     *
     * @param input 当前输入内容
     */
    List<String> tabComplete(String input);
}
