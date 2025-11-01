package cn.chengzhiya.mhdfbot.bot.qqbot.controller;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.http.HttpUtil;
import cn.chengzhiya.mhdfbot.api.http.JsonHttpData;
import cn.chengzhiya.mhdfbot.lang.Languages;
import cn.chengzhiya.mhdfhttpframework.api.enums.RequestTypes;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestParam;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestPath;
import cn.chengzhiya.mhdfhttpframework.server.annotation.RequestType;
import cn.chengzhiya.mhdfhttpframework.server.util.HttpServerUtil;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RequestPath("/file")
public final class FileController {
    @SneakyThrows
    @RequestPath("/get")
    @RequestType(RequestTypes.GET)
    public static void getImage(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("path") String path
    ) {
        File fileFolder = new File("files");
        File file = new File(fileFolder, path);
        if (!file.getCanonicalPath().startsWith(fileFolder.getAbsolutePath())) {
            HttpUtil.returnJsonHttpData(response, new JsonHttpData(401, Languages.WEBHOOK_GET_FILE_INVALID_PATH_MESSAGE));
            MHDFBot.getLogger().warn(Languages.WEBHOOK_GET_FILE_INVALID_PATH_LOG,
                    request.getRemoteAddr(),
                    file.getCanonicalPath()
            );
            return;
        }

        HttpServerUtil.returnFileData(response, file);
    }
}
