package org.bgamard.mytube.client.model;

import java.util.List;

public class SubscriptionList {
    public String nextPageToken;
    public List<Subscription> items;

    @Override
    public String toString() {
        return "SubscriptionList{" +
                "nextPageToken='" + nextPageToken + '\'' +
                ", items=" + items +
                '}';
    }
}
