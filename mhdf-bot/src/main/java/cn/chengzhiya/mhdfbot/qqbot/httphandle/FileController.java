package cn.chengzhiya.mhdfbot.qqbot.httphandle;

import cn.chengzhiya.mhdfbot.api.MHDFBot;
import cn.chengzhiya.mhdfbot.api.http.HttpUtil;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestParam;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestPath;
import cn.chengzhiya.mhdfbot.api.http.annotation.RequestType;
import cn.chengzhiya.mhdfbot.api.http.entity.JsonHttpData;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.io.File;

@RequestPath("/file")
public final class FileController {
    @SneakyThrows
    @RequestPath("/get")
    @RequestType(RequestType.Type.GET)
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

        HttpUtil.returnFileHttpData(response, file);
    }
}
