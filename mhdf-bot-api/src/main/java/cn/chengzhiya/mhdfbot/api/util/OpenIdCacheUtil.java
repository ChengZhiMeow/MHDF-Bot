package cn.chengzhiya.mhdfbot.api.util;

import cn.chengzhiya.mhdfbot.api.enums.bot.OpenIdType;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class OpenIdCacheUtil {
    @Getter
    private static final Map<Integer, String> messageIdHashMap = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, String> userIdHashMap = new ConcurrentHashMap<>();
    @Getter
    private static final Map<Integer, String> groupIdHashMap = new ConcurrentHashMap<>();

    /**
     * 获取指定ID类型的数据表
     *
     * @param openIdType ID类型
     * @return 数据表实例
     */
    private static Map<Integer, String> getMap(OpenIdType openIdType) {
        return switch (openIdType) {
            case MESSAGE -> OpenIdCacheUtil.getMessageIdHashMap();
            case USER -> OpenIdCacheUtil.getUserIdHashMap();
            case GROUP -> OpenIdCacheUtil.getGroupIdHashMap();
        };
    }

    /**
     * 增加数据
     *
     * @param openIdType ID类型
     * @param openId     数据ID
     * @return 数据ID编号
     */
    public static int addData(OpenIdType openIdType, String openId) {
        Map<Integer, String> map = OpenIdCacheUtil.getMap(openIdType);

        Integer id = OpenIdCacheUtil.getId(openIdType, openId);
        if (id == null) {
            map.put(map.size(), openId);
            return map.size() - 1;
        }

        return id;
    }

    /**
     * 获取数据ID编号
     *
     * @param openIdType ID类型
     * @param openId     数据ID
     * @return 数据ID编号
     */
    public static Integer getId(OpenIdType openIdType, String openId) {
        Map<Integer, String> map = OpenIdCacheUtil.getMap(openIdType);
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (!entry.getValue().equals(openId)) {
                continue;
            }

            return entry.getKey();
        }

        return null;
    }

    /**
     * 获取数据ID
     *
     * @param openIdType ID类型
     * @param id         数据ID编号
     * @return 数据ID
     */
    public static String getData(OpenIdType openIdType, int id) {
        Map<Integer, String> map = OpenIdCacheUtil.getMap(openIdType);
        return map.get(id);
    }
}
