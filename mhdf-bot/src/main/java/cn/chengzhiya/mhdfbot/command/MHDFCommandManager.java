package cn.chengzhiya.mhdfbot.command;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.command.CommandManager;
import cn.chengzhiya.mhdfbot.api.plugin.data.Command;
import cn.chengzhiya.mhdfbot.api.plugin.data.PluginInfo;
import cn.chengzhiya.mhdfbot.lang.Languages;

import java.util.*;

public final class MHDFCommandManager implements CommandManager {
    private final Map<String, Command> map = new HashMap<>();

    @Override
    public void registerCommand(Command command) {
        this.map.put(command.command(), command);
    }

    @Override
    public void unregisterCommand(String command) {
        this.map.remove(command);
    }

    @Override
    public void unregisterAllCommand(PluginInfo pluginInfo) {
        for (Map.Entry<String, Command> entry : this.map.entrySet()) {
            if (!entry.getValue().pluginInfo().equals(pluginInfo)) continue;
            this.map.remove(entry.getKey());
        }
    }

    @Override
    public Command getCommand(String command) {
        return this.map.get(command);
    }

    @Override
    public Collection<Command> getCommandList() {
        return this.map.values();
    }

    @Override
    public void executeCommand(String commandString) {
        String[] parts = commandString.split(" ");

        String command = parts[0];
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        if (this.getCommand(parts[0]) == null) {
            MHDFBot.getLogger().error(Languages.NOT_FOUND_COMMAND);
            return;
        }

        this.getCommand(parts[0]).executor().onCommand(command, args);
    }

    @Override
    public List<String> tabComplete(String input) {
        long length = input.chars().filter(c -> c == ' ').count();
        if (length == 0) return this.map.keySet().stream().toList();

        String[] parts = input.split(" ");

        Command command = this.getCommand(parts[0]);
        if (command == null) return new ArrayList<>();
        if (command.completer() == null) return new ArrayList<>();

        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);

        return command.completer().onTabComplete(parts[0], args);
    }
}
