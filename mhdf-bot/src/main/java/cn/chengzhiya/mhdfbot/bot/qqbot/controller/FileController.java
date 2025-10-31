package cn.chengzhiya.mhdfbot.bot.qqbot.controller;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.http.HttpUtil;
import cn.chengzhiya.mhdfbot.api.http.JsonHttpData;
import cn.chengzhiya.mhdfhttpframework.api.enums.RequestTypes;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestParam;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestPath;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestType;
import cn.chengzhiya.mhdfhttpframework.server.util.HttpServerUtil;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RequestPath("/file")
public final class FileController {
    @SneakyThrows
    @RequestPath("/get")
    @RequestType(RequestTypes.GET)
    public static void getImage(HttpServletResponse response,
                                @RequestParam("path") String path
    ) {
        File fileFolder = new File("files");
        File file = new File(fileFolder, path);
        if (!file.getCanonicalPath().startsWith(fileFolder.getAbsolutePath())) {
            HttpUtil.returnJsonHttpData(response, new JsonHttpData(401, "接口保护,你可能不是猫娘,无法使用此接口!"));
            MHDFBot.getLogger().error("网页端危险操作,访问路径越权,已拦截!");
            return;
        }

        HttpServerUtil.returnFileData(response, file);
    }
}
