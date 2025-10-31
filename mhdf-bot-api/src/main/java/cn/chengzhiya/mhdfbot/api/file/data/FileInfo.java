package cn.chengzhiya.mhdfbot.api.file.data;

import com.alibaba.fastjson2.JSONObject;

public record FileInfo(String id, String name, long size, long busid) {
    public static FileInfo fromJson(JSONObject data) {
        return new FileInfo(
                data.getString("id"),
                data.getString("nickName"),
                data.getLong("size"),
                data.getLong("busid")
        );
    }
}