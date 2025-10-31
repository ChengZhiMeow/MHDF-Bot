package cn.chengzhiya.mhdfbot.api.http;

import com.alibaba.fastjson2.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class HttpUtil {
    /**
     * 返回JSON数据实例
     *
     * @param response 请求实例
     * @param data     JSON数据实例
     */
    public static void returnJsonHttpData(HttpServletResponse response, JSONObject data) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(data.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 返回JSON数据实例
     *
     * @param response 请求实例
     * @param data     JSON数据实例
     */
    public static void returnJsonHttpData(HttpServletResponse response, JsonHttpData data) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(data.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
