package cn.chengzhiya.mhdfbot.bot.qqbot.controller;

import cn.chengzhiya.mhdfbot.api.http.HttpUtil;
import cn.chengzhiya.mhdfbot.api.http.JsonHttpData;
import cn.chengzhiya.mhdfhttpframework.api.enums.RequestTypes;
import cn.chengzhiya.mhdfhttpframework.server.annotation.Priority;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestPath;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestType;

import javax.servlet.http.HttpServletResponse;

@RequestPath("/default")
public final class NoFoundController {
    @Priority(-999)
    @RequestPath("/default")
    @RequestType(RequestTypes.ALL)
    public static void noFound(
            HttpServletResponse response
    ) {
        HttpUtil.returnJsonHttpData(response, JsonHttpData.noInterface);
    }
}
