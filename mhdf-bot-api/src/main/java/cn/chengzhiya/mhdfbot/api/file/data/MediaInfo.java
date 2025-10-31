package cn.chengzhiya.mhdfbot.api.file.data;

import com.alibaba.fastjson2.JSONObject;

public record MediaInfo(String fileUuid, String fileInfo, int ttl, String id) {
    public static MediaInfo fromJson(JSONObject data) {
        return new MediaInfo(
                data.getString("file_uuid"),
                data.getString("file_info"),
                data.getInteger("ttl"),
                data.getString("id")
        );
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("file_uuid", this.fileUuid);
        jsonObject.put("file_info", this.fileInfo);
        jsonObject.put("ttl", this.ttl);
        jsonObject.put("id", this.id);
        return jsonObject;
    }
}
