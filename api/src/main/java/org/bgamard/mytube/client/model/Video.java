package org.bgamard.mytube.client.model;

import java.time.Duration;
import java.time.LocalDateTime;

public class Video {
    public String id;
    public Snippet snippet;
    public ContentDetails contentDetails;
    public Statistics statistics;

    public static class Snippet {
        public LocalDateTime publishedAt;
        public String title;
        public String description;
        public String channelId;
        public String channelTitle;
        public Thumbnails thumbnails;
        public String liveBroadcastContent;

        @Override
        public String toString() {
            return "Snippet{" +
                    "publishedAt=" + publishedAt +
                    ", title='" + title + '\'' +
                    ", channelId='" + channelId + '\'' +
                    ", channelTitle='" + channelTitle + '\'' +
                    ", thumbnails=" + thumbnails +
                    ", liveBroadcastContent='" + liveBroadcastContent + '\'' +
                    '}';
        }
    }

    public static class ContentDetails {
        public Duration duration;

        @Override
        public String toString() {
            return "ContentDetails{" +
                    "duration=" + duration +
                    '}';
        }
    }

    public static class Statistics {
        public Long viewCount;
        public Long likeCount;

        @Override
        public String toString() {
            return "Statistics{" +
                    "viewCount=" + viewCount +
                    ", likeCount=" + likeCount +
                    '}';
        }
    }

    public static class Thumbnails {
        public Thumbnail high;

        @Override
        public String toString() {
            return "Thumbnails{" +
                    "high=" + high +
                    '}';
        }
    }

    public static class Thumbnail {
        public String url;

        @Override
        public String toString() {
            return "Thumbnail{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                ", contentDetails=" + contentDetails +
                ", statistics=" + statistics +
                '}';
    }
}
