package cn.chengzhiya.mhdfbot.feature.listener;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfbot.api.event.message.PrivateMessageEvent;
import cn.chengzhiya.mhdfbot.api.listener.EventHandler;
import cn.chengzhiya.mhdfbot.api.listener.Listener;
import cn.chengzhiya.mhdfbot.lang.Languages;

public final class MessageListener implements Listener {
    /**
     * 私聊消息提示
     */
    @EventHandler
    public void onPrivateMessage(PrivateMessageEvent event) {
        if (!Main.getConfigManager().getData().getBoolean("logSettings.privateMessage")) return;

        MHDFBot.getLogger().info(
                Languages.MESSAGE_LOG_PRIVATE,
                event.getSender().nickName(),
                event.getSender().userId(),
                event.getMessage()
        );
    }

    /**
     * 群聊消息提示
     */
    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        if (!Main.getConfigManager().getData().getBoolean("logSettings.groupMessage")) return;

        MHDFBot.getLogger().info(
                Languages.MESSAGE_LOG_GROUP,
                event.getGroupId(),
                event.getSender().nickName(),
                event.getSender().userId(),
                event.getMessage()
        );
    }
}
