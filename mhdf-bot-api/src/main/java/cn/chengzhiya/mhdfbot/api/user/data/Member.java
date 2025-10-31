package cn.chengzhiya.mhdfbot.api.user.data;

import cn.chengzhiya.mhdfbot.api.user.type.RoleType;
import cn.chengzhiya.mhdfbot.api.user.type.SexType;
import com.alibaba.fastjson2.JSONObject;

public final class Member extends Stranger {
    public static Member fromJson(JSONObject data) {
        return new Member(
                data.getString("qid"),
                data.getLong("user_id"),
                data.getString("nickname"),
                data.getString("longNick"),
                new Birthday(
                        data.getIntValue("birthday_year"),
                        data.getIntValue("birthday_month"),
                        data.getIntValue("birthday_day")
                ),
                SexType.get(data.getString("sex")),
                data.getString("country"),
                data.getString("province"),
                data.getString("city"),
                data.getLong("regTime"),
                data.get("labels") != null ? data.getJSONArray("labels").toArray(String.class) : new String[0],
                data.getInteger("level"),
                data.getLong("group_id"),
                data.getString("card"),
                RoleType.get(data.getString("role")),
                data.getString("title"),
                data.getInteger("qq_level")
        );
    }

    private final long groupId;
    private final String groupCard;
    private final RoleType role;
    private final String title;
    private final Integer qqLevel;

    public Member(
            String qid,
            long id,
            String nickName,
            String card,
            Birthday birthday,
            SexType sex,
            String country,
            String province,
            String city,
            Long regTime,
            String[] labels,
            Integer level,
            long groupId,
            String groupCard,
            RoleType role,
            String title,
            Integer qqLevel
    ) {
        super(qid, id, nickName, card, birthday, sex, country, province, city, regTime, labels, level);
        this.groupId = groupId;
        this.groupCard = groupCard;
        this.role = role;
        this.title = title;
        this.qqLevel = qqLevel;
    }

    public long groupId() {
        return this.groupId;
    }

    public String groupCard() {
        return this.groupCard;
    }

    public RoleType role() {
        return this.role;
    }

    public String title() {
        return this.title;
    }

    public int qqLevel() {
        return this.qqLevel;
    }
}