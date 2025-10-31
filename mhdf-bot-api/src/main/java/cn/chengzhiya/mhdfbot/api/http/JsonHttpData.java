package cn.chengzhiya.mhdfbot.api.http;

import com.alibaba.fastjson2.JSONObject;

public record JsonHttpData(int id, String msg, JSONObject data) {
    public static final JsonHttpData serverError = new JsonHttpData(500, "服务器发生内部错误", new JSONObject());
    public static final JsonHttpData noInterface = new JsonHttpData(404, "找不到目标接口", new JSONObject());
    public static final JsonHttpData noData = new JsonHttpData(404, "找不到数据", new JSONObject());
    public static final JsonHttpData noAuth = new JsonHttpData(401, "未登录或登录失效", new JSONObject());
    public static final JsonHttpData noCookie = new JsonHttpData(400, "找不到cookie", new JSONObject());
    public static final JsonHttpData noParam = new JsonHttpData(400, "传参错误", new JSONObject());

    public JsonHttpData() {
        this(200, "", new JSONObject());
    }

    public JsonHttpData(int id, String msg) {
        this(id, msg, new JSONObject());
    }

    public JsonHttpData(String msg, JSONObject data) {
        this(200, msg, data);
    }

    public JsonHttpData(JSONObject data) {
        this(200, "", data);
    }

    public JsonHttpData(String msg) {
        this(200, msg, new JSONObject());
    }

    public JsonHttpData(int id) {
        this(id, "", new JSONObject());
    }
}