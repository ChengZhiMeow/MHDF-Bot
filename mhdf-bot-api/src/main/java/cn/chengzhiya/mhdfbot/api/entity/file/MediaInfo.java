package cn.chengzhiya.mhdfbot.api.entity.file;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
public final class MediaInfo {
    private final String fileUuid;
    private final String fileInfo;
    private final int ttl;
    private final String id;

    public MediaInfo(JSONObject data) {
        this.fileUuid = data.getString("file_uuid");
        this.fileInfo = data.getString("file_info");
        this.ttl = data.getInteger("ttl");
        this.id = data.getString("id");
    }

    public JSONObject toJsonObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("file_uuid", fileUuid);
        jsonObject.put("file_info", fileInfo);
        jsonObject.put("ttl", ttl);
        jsonObject.put("id", id);

        return jsonObject;
    }
}
