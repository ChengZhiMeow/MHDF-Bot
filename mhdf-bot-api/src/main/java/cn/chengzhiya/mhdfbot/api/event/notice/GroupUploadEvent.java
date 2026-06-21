package cn.chengzhiya.mhdfbot.api.event.notice;

import cn.chengzhiya.mhdfbot.api.file.data.FileInfo;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;

@Getter
public final class GroupUploadEvent extends AbstractNoticeEvent {
    private final long groupId;
    private final FileInfo fileInfo;

    public GroupUploadEvent(JSONObject data) {
        super(data);
        this.groupId = data.getLongValue("group_id");
        JSONObject fileData = data.getJSONObject("file");
        if (fileData == null) fileData = data.getJSONObject("fileInfo");
        this.fileInfo = FileInfo.fromJson(fileData);
    }
}
