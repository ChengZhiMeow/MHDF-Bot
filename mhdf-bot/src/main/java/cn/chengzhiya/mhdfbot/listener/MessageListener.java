package cn.chengzhiya.mhdfbot.listener;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfbot.api.event.message.PrivateMessageEvent;
import cn.chengzhiya.mhdfbot.api.listener.EventHandler;
import cn.chengzhiya.mhdfbot.api.listener.Listener;

public final class MessageListener implements Listener {
    /**
     * 群聊消息提示
     */
    @EventHandler
    public void onGroupMessage(GroupMessageEvent event) {
        if (!Main.getConfigManager().getData().getBoolean("logSettings.groupMessage")) {
            return;
        }

        MHDFBot.getLogger().info(
                "在群聊{}收到了一条消息: {}({}): {}",
                event.getGroupId(),
                event.getSender().getNickName(),
                event.getSender().getUserId(),
                event.getMessage()
        );
    }

    /**
     * 私聊消息提示
     */
    @EventHandler
    public void onPrivateMessage(PrivateMessageEvent event) {
        if (!Main.getConfigManager().getData().getBoolean("logSettings.privateMessage")) {
            return;
        }

        MHDFBot.getLogger().info(
                "在私聊收到了一条消息: {}({}): {}",
                event.getSender().getNickName(),
                event.getSender().getUserId(),
                event.getMessage()
        );
    }
}
