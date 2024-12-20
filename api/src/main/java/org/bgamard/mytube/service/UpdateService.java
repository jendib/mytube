package org.bgamard.mytube.service;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.bgamard.mytube.client.YoutubeClientService;
import org.bgamard.mytube.client.model.*;
import org.bgamard.mytube.entity.VideoEntity;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UpdateService {
    @Inject
    @RestClient
    YoutubeClientService youtubeClientService;

    @Scheduled(cron = "${mytube.update.cron:off}", identity = "update-job")
    @Transactional
    @TransactionConfiguration(timeout = 600)
    public void update() {
        Log.info("Starting update");
        List<Subscription> subscriptionList = new ArrayList<>();
        String nextToken = "";

        do {
            SubscriptionList subscriptionResult = youtubeClientService.subscriptions("snippet", "alphabetical", true, 50, nextToken);
            subscriptionList.addAll(subscriptionResult.items);
            nextToken = subscriptionResult.nextPageToken;
        } while (nextToken != null);


        // Grab all latest videos
        List<Video> latestVideoList = getLatestVideos(subscriptionList);

        // Output latest videos
        saveVideos(latestVideoList);

        Log.info("Update done");
    }

    private List<Video> getLatestVideos(List<Subscription> subscriptionList) {
        Log.info("Subscriptions = " + subscriptionList.size());
        List<Video> latestVideoList = new ArrayList<>();

        for (Subscription subscription : subscriptionList) {
            Log.info("Grabbing videos for = " + subscription.snippet.title
                    + " (" + (subscriptionList.indexOf(subscription) + 1) + "/" + subscriptionList.size() + ")");

            // Grab uploads playlist from this channel
            ChannelList channelResult = youtubeClientService.channels(subscription.snippet.resourceId.channelId, "contentDetails", 1);
            List<Channel> channelList = channelResult.items;
            if (channelList == null || channelList.isEmpty()) {
                Log.info("No channel, skipping");
                continue;
            }
            String uploadPlaylistId = channelList.get(0).contentDetails.relatedPlaylists.uploads;

            // Get 50 latest uploads from this channel
            PlaylistItemList playlistResult = youtubeClientService.playlistItems(uploadPlaylistId, "snippet", 50);

            // Get details on those videos
            List<String> idList = new ArrayList<>();
            for (PlaylistItem playlistItem : playlistResult.items) {
                idList.add(playlistItem.snippet.resourceId.videoId);
            }

            String ids = String.join(",", idList);
            VideoList videosResult = youtubeClientService.videos(ids, "snippet,contentDetails,statistics", 50);
            latestVideoList.addAll(videosResult.items);
        }

        return latestVideoList;
    }

    private void saveVideos(List<Video> latestVideoList) {
        Log.info("Saving videos: " + latestVideoList.size());
        for (Video video : latestVideoList) {
            VideoEntity videoEntity = VideoEntity.findByYoutubeId(video.id)
                    .orElse(new VideoEntity());

            videoEntity.youtubeId = video.id;
            videoEntity.title = video.snippet.title;
            videoEntity.description = video.snippet.description;
            videoEntity.channelId = video.snippet.channelId;
            videoEntity.channelTitle = video.snippet.channelTitle;
            videoEntity.publishedDate = video.snippet.publishedAt;
            if (video.statistics != null) {
                if (video.statistics.viewCount != null) {
                    videoEntity.viewCount = video.statistics.viewCount;
                }
                if (video.statistics.likeCount != null) {
                    videoEntity.likeCount = video.statistics.likeCount;
                }
            }
            videoEntity.duration = video.contentDetails.duration == null ? Duration.ZERO : video.contentDetails.duration;
            videoEntity.thumbnailUrl = video.snippet.thumbnails.medium.url;
            videoEntity.persist();
        }
    }
}
