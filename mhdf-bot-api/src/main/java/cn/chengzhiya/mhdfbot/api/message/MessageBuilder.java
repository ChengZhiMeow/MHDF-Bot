package cn.chengzhiya.mhdfbot.api.message;

import cn.chengzhiya.mhdfbot.api.file.MediaBuilder;
import cn.chengzhiya.mhdfbot.api.message.data.MusicInfo;
import cn.chengzhiya.mhdfbot.api.util.MessageEscapeUtil;

@SuppressWarnings("unused")
public final class MessageBuilder {
    public static Builder builder() {
        return new Builder();
    }

    private MessageBuilder() {
    }

    public static class Builder {
        private final StringBuilder builder = new StringBuilder();

        private Builder() {
        }

        public Builder text(String text) {
            this.builder.append(text);
            return this;
        }

        public Builder face(int id) {
            this.builder.append("[CQ:face,id=")
                    .append(id)
                    .append("]");
            return this;
        }

        public Builder at(long qq) {
            this.builder.append("[CQ:at,qq=")
                    .append(qq)
                    .append("]");
            return this;
        }

        public Builder atAll() {
            this.builder.append("[CQ:at,qq=all]");
            return this;
        }

        public Builder image(String image) {
            this.builder.append("[CQ:image,")
                    .append(MediaBuilder.builder().file(image).build())
                    .append("]");
            return this;
        }

        public Builder flashImage(String image) {
            this.builder.append("[CQ:image,type=flash,")
                    .append(MediaBuilder.builder().file(image).build())
                    .append("]");
            return this;
        }

        public Builder video(String video, String cover) {
            this.builder.append("[CQ:video,")
                    .append(MediaBuilder.builder().file(video).cover(cover).build())
                    .append("]");
            return this;
        }

        public Builder audio(String audio) {
            this.builder.append("[CQ:record,")
                    .append(MediaBuilder.builder().file(audio).build())
                    .append("]");
            return this;
        }

        public Builder tts(String message) {
            this.builder.append("[CQ:tts,text=")
                    .append(message)
                    .append("]");
            return this;
        }

        public Builder reply(int messageId) {
            this.builder.append("[CQ:reply,id=")
                    .append(messageId)
                    .append("]");
            return this;
        }

        public Builder music(String type, long musicId) {
            this.builder.append("[CQ:music,type=")
                    .append(type)
                    .append(",id=")
                    .append(musicId)
                    .append("]");
            return this;
        }

        public Builder customMusic(MusicInfo music) {
            this.builder.append("[CQ:music,type=custom,url=")
                    .append(MessageEscapeUtil.escape(music.url()))
                    .append(",title=")
                    .append(MessageEscapeUtil.escape(music.title()))
                    .append(",content=")
                    .append(MessageEscapeUtil.escape(music.content()))
                    .append(",image=")
                    .append(MessageEscapeUtil.escape(music.image()))
                    .append(",audio=")
                    .append(MessageEscapeUtil.escape(music.audio()))
                    .append("]");
            return this;
        }

        public Builder forward(String forwardId) {
            this.builder.append("[CQ:forward,id=")
                    .append(forwardId)
                    .append("]");
            return this;
        }

        public String build() {
            return this.builder.toString();
        }
    }
}
