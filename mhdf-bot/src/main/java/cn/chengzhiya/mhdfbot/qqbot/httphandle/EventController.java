package cn.chengzhiya.mhdfbot.qqbot.httphandle;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.event.message.GroupMessageEvent;
import cn.chengzhiya.mhdfbot.api.http.annotation.BodyData;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestPath;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestType;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletResponse;

@RequestPath("")
public final class EventController {
    @RequestPath("*")
    @RequestType(RequestType.Type.POST)
    public static void callEvent(HttpServletResponse response,
                                 @BodyData("body") JSONObject body,
                                 @BodyData("t") String event,
                                 @BodyData("d") JSONObject d
    ) {
        MHDFBot.getLogger().info("收到来自腾讯服务器的事件, 事件: {}, 数据: {}",
                event,
                body
        );

        switch (event) {
            case "GROUP_AT_MESSAGE_CREATE" -> MHDFBot.getListenerManager().callEvent(new GroupMessageEvent(d));
        }
    }
}
