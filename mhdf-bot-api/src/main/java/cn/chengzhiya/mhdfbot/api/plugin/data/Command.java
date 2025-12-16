package cn.chengzhiya.mhdfbot.api.plugin.data;

import cn.chengzhiya.mhdfbot.api.command.CommandExecutor;
import cn.chengzhiya.mhdfbot.api.command.TabCompleter;
import cn.chengzhiya.mhdfbot.api.plugin.PluginInfo;

@SuppressWarnings("unused")
public final class Command {
    private final String command;
    private PluginInfo pluginInfo;
    private String description;
    private String usage;
    private CommandExecutor executor;
    private TabCompleter completer;

    public Command(String command) {
        this.command = command;
    }

    public String command() {
        return this.command;
    }

    public PluginInfo pluginInfo() {
        return this.pluginInfo;
    }

    public String description() {
        return this.description;
    }

    public String usage() {
        return this.usage;
    }

    public CommandExecutor executor() {
        return this.executor;
    }

    public TabCompleter completer() {
        return this.completer;
    }

    /**
     * 设置命令实例来源
     *
     * @param pluginInfo 插件实例
     */
    public Command plugin(PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
        return this;
    }

    /**
     * 设置命令实例描述
     *
     * @param description 命令描述
     */
    public Command description(String description) {
        this.description = description;
        return this;
    }

    /**
     * 设置命令实例用法
     *
     * @param usage 命令用法
     */
    public Command usage(String usage) {
        this.usage = usage;
        return this;
    }

    /**
     * 设置命令实例命令执行实例
     *
     * @param executor 执行实例
     */
    public Command executor(CommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * 设置命令实例命令补全实例
     *
     * @param tabCompleter 补全实例
     */
    public Command completer(TabCompleter tabCompleter) {
        this.completer = tabCompleter;
        return this;
    }
}