package cn.chengzhiya.mhdfbot.api.http;

import cn.chengzhiya.mhdfbot.api.http.entity.JsonHttpData;
import com.alibaba.fastjson2.JSONObject;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

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

    /**
     * 返回文件数据
     *
     * @param response 请求实例
     * @param file     文件实例
     */
    public static void returnFileHttpData(ServletResponse response, File file) {
        if (file == null || !file.exists()) {
            return;
        }

        try {
            try (InputStream in = Files.newInputStream(file.toPath())) {
                try (OutputStream out = response.getOutputStream()) {
                    int len;
                    byte[] byteData = new byte[1042];
                    while ((len = in.read(byteData)) != -1) {
                        out.write(byteData, 0, len);
                    }
                    out.flush();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
