package cn.chengzhiya.mhdfbot.api.message.data;

import com.alibaba.fastjson2.JSONObject;

public record RecordInfo(String file, String url, String fileSize, String fileName, String base64) {
    public static RecordInfo fromJson(JSONObject data) {
        return new RecordInfo(
                data.getString("file"),
                data.getString("url"),
                data.getString("file_size"),
                data.getString("file_name"),
                data.getString("base64")
        );
    }
}