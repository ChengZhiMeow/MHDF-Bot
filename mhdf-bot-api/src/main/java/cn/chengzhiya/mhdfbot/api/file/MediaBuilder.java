package cn.chengzhiya.mhdfbot.api.file;

import cn.chengzhiya.mhdfbot.api.util.MessageEscapeUtil;
import lombok.Builder;

@Builder
@SuppressWarnings("unused")
public final class MediaBuilder {
    private String file;
    private Boolean cache;
    private Boolean proxy;
    private Integer timeout;
    private String cover;
    private String magic;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("file=").append(MessageEscapeUtil.escape(this.file));
        if (this.cache != null) builder.append(",cache=").append(this.cache);
        if (this.proxy != null) builder.append(",proxy=").append(this.proxy);
        if (this.timeout != null) builder.append(",timeout=").append(this.timeout);
        if (this.magic != null) builder.append(",magic=").append(this.magic);
        return builder.toString();
    }
}
