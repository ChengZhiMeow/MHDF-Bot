package cn.chengzhiya.mhdfbot.bot;

import cn.chengzhiya.mhdfbot.api.bot.Bot;
import cn.chengzhiya.mhdfbot.api.command.CommandManager;
import cn.chengzhiya.mhdfbot.api.listener.ListenerManager;
import cn.chengzhiya.mhdfbot.api.plugin.PluginManager;
import cn.chengzhiya.mhdfbot.api.scheduler.Scheduler;
import cn.chengzhiya.mhdfbot.command.MHDFCommandManager;
import cn.chengzhiya.mhdfbot.listener.MHDFListenerManager;
import cn.chengzhiya.mhdfbot.plugin.MHDFPluginManager;
import cn.chengzhiya.mhdfbot.scheduler.MHDFScheduler;
import lombok.Getter;

@Getter
public abstract class MHDFAbstractBot implements Bot {
    private final PluginManager pluginManager = new MHDFPluginManager();
    private final CommandManager commandManager = new MHDFCommandManager();
    private final ListenerManager listenerManager = new MHDFListenerManager();
    private final Scheduler scheduler = new MHDFScheduler();
}
