package org.bgamard.mytube.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.google.common.base.Joiner;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.bgamard.mytube.entity.VideoEntity;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class UpdateService {
    @ConfigProperty(name = "mytube.google.auth")
    String googleAuth;

    @Scheduled(every = "${mytube.update.cron:off}", identity = "update-job")
    @Transactional
    public void update() throws Exception {
        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = List.of("https://www.googleapis.com/auth/youtube.readonly");

        // Authorize the request.
        Credential credential = Auth.authorize(scopes, "mytube", googleAuth);

        // This object is used to make YouTube Data API requests.
        YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName("jendib-mytube").build();

        List<Subscription> subscriptionList = new ArrayList<>();

        String nextToken = "";

        // Call the API one or more times to retrieve all items in the
        // list. As long as the API response returns a nextPageToken,
        // there are still more items to retrieve.
        do {
            YouTube.Subscriptions.List subscriptionRequest = youtube.subscriptions().list("snippet");
            subscriptionRequest.setMine(true);
            subscriptionRequest.setPageToken(nextToken);
            subscriptionRequest.setMaxResults(1000L);
            subscriptionRequest.setOrder("alphabetical");
            SubscriptionListResponse subscriptionResult = ClientRequestHelper.executeRetry(subscriptionRequest);

            subscriptionList.addAll(subscriptionResult.getItems());

            nextToken = subscriptionResult.getNextPageToken();
        } while (nextToken != null);

        // Grab all latest videos
        List<Video> latestVideoList = getLatestVideos(youtube, subscriptionList);

        // Output latest videos
        saveVideos(latestVideoList);
    }

    private List<Video> getLatestVideos(YouTube youtube, List<Subscription> subscriptionList) throws Exception {
        Log.info("Subscriptions = " + subscriptionList.size());
        List<Video> latestVideoList = new ArrayList<>();

        for (Subscription subscription : subscriptionList) {
            Log.info("Grabbing videos for = " + subscription.getSnippet().getTitle()
                    + " (" + (subscriptionList.indexOf(subscription) + 1) + "/" + subscriptionList.size() + ")");

            // Grab uploads playlist from this channel
            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setMaxResults(1L);
            channelRequest.setId(subscription.getSnippet().getResourceId().getChannelId());
            ChannelListResponse channelResult = ClientRequestHelper.executeRetry(channelRequest);
            List<Channel> channelList = channelResult.getItems();
            if (channelList == null || channelList.isEmpty()) {
                Log.info("No channel, skipping");
                continue;
            }
            String uploadPlaylistId = channelList.get(0).getContentDetails().getRelatedPlaylists().getUploads();

            // Get 50 latest uploads from this channel
            YouTube.PlaylistItems.List playlistRequest = youtube.playlistItems().list("snippet");
            playlistRequest.setMaxResults(50L);
            playlistRequest.setPlaylistId(uploadPlaylistId);
            PlaylistItemListResponse playlistResult = ClientRequestHelper.executeRetry(playlistRequest);

            // Get details on those videos
            List<String> idList = new ArrayList<>();
            for (PlaylistItem playlistItem : playlistResult.getItems()) {
                idList.add(playlistItem.getSnippet().getResourceId().getVideoId());
            }

            YouTube.Videos.List videosRequest = youtube.videos().list("snippet,contentDetails,statistics");
            videosRequest.setMaxResults(50L);
            videosRequest.setId(Joiner.on(",").join(idList));
            VideoListResponse videosResult = ClientRequestHelper.executeRetry(videosRequest);
            List<Video> videoList = videosResult.getItems();

            latestVideoList.addAll(videoList);

            break; // TODO Remove me
        }

        return latestVideoList;
    }

    private void saveVideos(List<Video> latestVideoList) {
        for (Video video : latestVideoList) {
            VideoEntity videoEntity = VideoEntity.findByYoutubeId(video.getId())
                    .orElse(new VideoEntity());

            videoEntity.youtubeId = video.getId();
            videoEntity.title = video.getSnippet().getTitle();
            videoEntity.description = video.getSnippet().getDescription();
            videoEntity.channelId = video.getSnippet().getChannelId();
            videoEntity.channelTitle = video.getSnippet().getChannelTitle();
            videoEntity.publishedDate = parseGoogleDate(video.getSnippet().getPublishedAt());
            if (video.getStatistics() != null) {
                if (video.getStatistics().getViewCount() != null) {
                    videoEntity.viewCount = video.getStatistics().getViewCount().longValue();
                }
                if (video.getStatistics().getLikeCount() != null) {
                    videoEntity.likeCount = video.getStatistics().getLikeCount().longValue();
                }
            }
            videoEntity.duration = video.getContentDetails().getDuration();
            videoEntity.thumbnailUrl = video.getSnippet().getThumbnails().getMedium().getUrl();
            videoEntity.persist();
        }
    }

    private LocalDateTime parseGoogleDate(DateTime dateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return LocalDateTime.parse(dateTime.toStringRfc3339(), dateTimeFormatter);
    }
}
