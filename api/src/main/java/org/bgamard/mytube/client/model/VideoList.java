package org.bgamard.mytube.client.model;

import java.util.List;

public class VideoList {
    public List<Video> items;

    @Override
    public String toString() {
        return "VideoList{" +
                "items=" + items +
                '}';
    }
}
