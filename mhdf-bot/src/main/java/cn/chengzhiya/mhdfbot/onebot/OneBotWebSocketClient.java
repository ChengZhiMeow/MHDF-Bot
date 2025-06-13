package cn.chengzhiya.mhdfbot.onebot;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.enums.notice.NoticeType;
import cn.chengzhiya.mhdfbot.api.enums.notice.NotifySubType;
import cn.chengzhiya.mhdfbot.api.event.bot.HeartbeatEvent;
import cn.chengzhiya.mhdfbot.api.event.bot.LifecycleEvent;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfbot.api.event.message.PrivateMessageEvent;
import cn.chengzhiya.mhdfbot.api.event.notice.*;
import cn.chengzhiya.mhdfbot.api.event.request.FriendRequestEvent;
import cn.chengzhiya.mhdfbot.api.event.request.GroupRequestEvent;
import cn.chengzhiya.mhdfbot.api.websocket.AbstractWebSocketClient;
import com.alibaba.fastjson2.JSONObject;

public final class OneBotWebSocketClient extends AbstractWebSocketClient {
    public OneBotWebSocketClient() {
        super(
                MHDFBot.getBot().getBotConfig().getString("websocketHost"),
                true
        );

        setAccessToken("Bearer " + MHDFBot.getBot().getBotConfig().getString("accessToken"));
    }

    @Override
    public void handleMessage(String message) {
        JSONObject data = JSONObject.parseObject(message);
        if (data.getString("post_type") != null) {
            switch (data.getString("post_type")) {
                case "message" -> {
                    switch (data.getString("message_type")) {
                        case "group" -> MHDFBot.getListenerManager().callEvent(new GroupMessageEvent(data));
                        case "private" -> MHDFBot.getListenerManager().callEvent(new PrivateMessageEvent(data));
                        default -> MHDFBot.getLogger().info(message);
                    }
                }
                case "meta_event" -> {
                    switch (data.getString("meta_event_type")) {
                        case "lifecycle" -> MHDFBot.getListenerManager().callEvent(new LifecycleEvent(data));
                        case "heartbeat" -> MHDFBot.getListenerManager().callEvent(new HeartbeatEvent(data));
                        default -> MHDFBot.getLogger().info(message);
                    }
                }
                case "notice" -> {
                    switch (NoticeType.get(data.getString("notice_type"))) {
                        case GROUP_UPLOAD -> MHDFBot.getListenerManager().callEvent(new GroupUploadEvent(data));
                        case GROUP_ADMIN -> MHDFBot.getListenerManager().callEvent(new GroupAdminEvent(data));
                        case GROUP_BAN -> MHDFBot.getListenerManager().callEvent(new GroupBanEvent(data));
                        case GROUP_DECREASE -> MHDFBot.getListenerManager().callEvent(new GroupDecreaseEvent(data));
                        case GROUP_INCREASE -> MHDFBot.getListenerManager().callEvent(new GroupIncreaseEvent(data));
                        case GROUP_RECALL -> MHDFBot.getListenerManager().callEvent(new GroupRecallEvent(data));
                        case GROUP_CARD -> MHDFBot.getListenerManager().callEvent(new GroupCardEvent(data));
                        case FRIEND_RECALL -> MHDFBot.getListenerManager().callEvent(new FriendRecallEvent(data));
                        case NOTIFY -> {
                            switch (NotifySubType.get(data.getString("sub_type"))) {
                                case POKE -> MHDFBot.getListenerManager().callEvent(new PokeEvent(data));
                                case LUCKY_KING -> MHDFBot.getListenerManager().callEvent(new LuckyKingEvent(data));
                                case HONOR -> MHDFBot.getListenerManager().callEvent(new HonorEvent(data));
                                case INPUT_STATUS -> MHDFBot.getListenerManager().callEvent(new InputStatusEvent(data));
                                default -> MHDFBot.getLogger().info(message);
                            }
                        }
                        default -> MHDFBot.getLogger().info(message);
                    }
                }
                case "request" -> {
                    switch (data.getString("request_type")) {
                        case "friend" -> MHDFBot.getListenerManager().callEvent(new FriendRequestEvent(data));
                        case "group" -> MHDFBot.getListenerManager().callEvent(new GroupRequestEvent(data));
                        default -> MHDFBot.getLogger().info(message);
                    }
                }
                default -> MHDFBot.getLogger().info(message);
            }
        }
    }
}
