package cn.chengzhiya.mhdfbot.api.user.data;

import cn.chengzhiya.mhdfbot.api.user.type.SexType;
import com.alibaba.fastjson2.JSONObject;

public class Stranger {
    public static Stranger fromJson(JSONObject data) {
        return new Stranger(
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
                data.getInteger("level")
        );
    }

    private final String qid;
    private final long id;
    private final String nickName;
    private final String card;
    private final Birthday birthday;
    private final SexType sex;
    private final String country;
    private final String province;
    private final String city;
    private final Long regTime;
    private final String[] labels;
    private final Integer level;

    public Stranger(
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
            Integer level
    ) {
        this.qid = qid;
        this.id = id;
        this.nickName = nickName;
        this.card = card;
        this.birthday = birthday;
        this.sex = sex;
        this.country = country;
        this.province = province;
        this.city = city;
        this.regTime = regTime;
        this.labels = labels;
        this.level = level;
    }

    public String qid() {
        return this.qid;
    }

    public long id() {
        return this.id;
    }

    public String nickName() {
        return this.nickName;
    }

    public String card() {
        return this.card;
    }

    public Birthday birthday() {
        return this.birthday;
    }

    public SexType sex() {
        return this.sex;
    }

    public String country() {
        return this.country;
    }

    public String province() {
        return this.province;
    }

    public String city() {
        return this.city;
    }

    public Long regTime() {
        return this.regTime;
    }

    public String[] labels() {
        return this.labels;
    }

    public Integer level() {
        return this.level;
    }
}