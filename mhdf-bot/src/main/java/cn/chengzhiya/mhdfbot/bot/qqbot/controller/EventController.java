package cn.chengzhiya.mhdfbot.bot.qqbot.controller;

import cn.chengzhiya.mhdfbot.Main;
import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfbot.thread.MHDFScheduledThread;
import cn.chengzhiya.mhdfhttpframework.api.enums.RequestTypes;
import cn.chengzhiya.mhdfhttpframework.server.annotation.BodyData;
import cn.chengzhiya.mhdfhttpframework.server.annotation.IgnoreNullParam;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestPath;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestType;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.CopyOnWriteArraySet;

@RequestPath("/")
public final class EventController {
    private static final CopyOnWriteArraySet<JSONObject> handleEvent = new CopyOnWriteArraySet<>();
    private static final MHDFScheduledThread thread = new MHDFScheduledThread("MHDF-Bot QqBot-Remove-Event-Cache Thread") {
    };

    @IgnoreNullParam
    @RequestPath("/default")
    @RequestType(RequestTypes.POST)
    public static void callEvent(HttpServletResponse response,
                                 @BodyData("body") JSONObject body,
                                 @BodyData("t") String event,
                                 @BodyData("d") JSONObject d
    ) {
        if (EventController.handleEvent.contains(body)) return;

        if (Main.getConfigManager().getData().getBoolean("logSettings.groupMessage")) {
            MHDFBot.getLogger().info("收到来自腾讯服务器的事件, 事件: {}, 数据: {}",
                    event,
                    body
            );
        }

        // 因为部分情况下腾讯会请求多次，所以需要将相同事件忽略
        EventController.handleEvent.add(body);
        EventController.thread.schedule(() -> EventController.handleEvent.remove(body), 2500);

        switch (event) {
            case "GROUP_AT_MESSAGE_CREATE" -> MHDFBot.getListenerManager().callEvent(new GroupMessageEvent(d));
        }
    }
}
