package org.bgamard.mytube.client.model;

public class PlaylistItem {
    public String id;
    public Snippet snippet;

    public static class Snippet {
        public String playlistId;
        public ResourceId resourceId;

        @Override
        public String toString() {
            return "Snippet{" +
                    "playlistId='" + playlistId + '\'' +
                    ", resourceId=" + resourceId +
                    '}';
        }
    }

    public static class ResourceId {
        public String videoId;

        @Override
        public String toString() {
            return "ResourceId{" +
                    "videoId='" + videoId + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "PlaylistItem{" +
                "id='" + id + '\'' +
                ", snippet=" + snippet +
                '}';
    }
}
