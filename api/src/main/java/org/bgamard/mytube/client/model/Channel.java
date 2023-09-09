package org.bgamard.mytube.client.model;

public class Channel {
    public String id;
    public ContentDetails contentDetails;

    public static class ContentDetails {
        public RelatedPlaylists relatedPlaylists;

        @Override
        public String toString() {
            return "ContentDetails{" +
                    "relatedPlaylists=" + relatedPlaylists +
                    '}';
        }
    }

    public static class RelatedPlaylists {
        public String uploads;

        @Override
        public String toString() {
            return "RelatedPlaylists{" +
                    "uploads='" + uploads + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id='" + id + '\'' +
                ", contentDetails=" + contentDetails +
                '}';
    }
}
