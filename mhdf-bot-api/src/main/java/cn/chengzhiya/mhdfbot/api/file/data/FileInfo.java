package cn.chengzhiya.mhdfbot.api.file.data;

import com.alibaba.fastjson2.JSONObject;

public record FileInfo(String id, String name, long size, long busid) {
    public static FileInfo fromJson(JSONObject data) {
        if (data == null) return new FileInfo("", "", 0, 0);

        String name = data.getString("name");
        if (name == null) name = data.getString("nickName");

        return new FileInfo(
                data.getString("id"),
                name == null ? "" : name,
                data.getLongValue("size"),
                data.getLongValue("busid")
        );
    }
}
