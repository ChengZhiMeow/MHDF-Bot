package cn.chengzhiya.mhdfbot.bot.qqbot.controller;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfhttpframework.api.enums.RequestTypes;
import cn.chengzhiya.mhdfhttpframework.server.annotation.BodyData;
import cn.chengzhiya.mhdfhttpframework.server.annotation.IgnoreNullParam;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestPath;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestType;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CopyOnWriteArraySet;

@RequestPath("/")
public final class EventController {
    @Getter
    private static final CopyOnWriteArraySet<JSONObject> handleEvent = new CopyOnWriteArraySet<>();

    @IgnoreNullParam
    @RequestPath("/default")
    @RequestType(RequestTypes.POST)
    public static void callEvent(HttpServletResponse response,
                                 @BodyData("body") JSONObject body,
                                 @BodyData("t") String event,
                                 @BodyData("d") JSONObject d
    ) {
        if (EventController.getHandleEvent().contains(body)) {
            return;
        }

        EventController.getHandleEvent().add(body);
        MHDFBot.getLogger().info("收到来自腾讯服务器的事件, 事件: {}, 数据: {}",
                event,
                body
        );

        MHDFBot.getScheduler().runTaskLater(() -> EventController.getHandleEvent().remove(body), 10);

        switch (event) {
            case "GROUP_AT_MESSAGE_CREATE" -> MHDFBot.getListenerManager().callEvent(new GroupMessageEvent(d));
        }
    }
}
