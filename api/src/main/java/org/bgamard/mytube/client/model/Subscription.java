package org.bgamard.mytube.client.model;

import java.time.LocalDateTime;

public class Subscription {
    public String id;
    public Snippet snippet;

    public static class Snippet {
        public String title;
        public String description;
        public ResourceId resourceId;

        @Override
        public String toString() {
            return "Snippet{" +
                    "title='" + title + '\'' +
                    ", description='" + description + '\'' +
                    ", resourceId=" + resourceId +
                    '}';
        }
    }

    public static class ResourceId {
        public String channelId;

        @Override
        public String toString() {
            return "ResourceId{" +
                    "channelId='" + channelId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Video{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                '}';
    }
}
