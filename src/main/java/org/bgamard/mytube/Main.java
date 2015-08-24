package org.bgamard.mytube;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

/**
 * App entry point.
 * 
 * @author bgamard
 */
public final class Main {
    /**
     * Max number of videos.
     */
    private static final int MAX_VIDEOS = 200;
    
    /**
     * Define a global instance of a Youtube object, which will be used
     * to make YouTube Data API requests.
     */
    private static YouTube youtube;
    
    /**
     * List of latest videos.
     */
    private static List<Video> latestVideoList = Lists.newArrayList();
    
    /**
     * Main process.
     * 
     * @param args Arguments
     */
    public static void main(String[] args) {
        // JSON output
        String output = "data.json";
        if (args.length == 1) {
            output = args[0];
        }
        System.out.println("Outputting JSON to: " + output);
        
        // This OAuth 2.0 access scope allows for read-only access to the
        // authenticated user's account, but not other types of account access.
        List<String> scopes = Lists.newArrayList("https://www.googleapis.com/auth/youtube.readonly");

        try {
            // Authorize the request.
            Credential credential = Auth.authorize(scopes, "mytube");

            // This object is used to make YouTube Data API requests.
            youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT, Auth.JSON_FACTORY, credential).setApplicationName("jendib-mytube").build();

            List<Subscription> subscriptionList = Lists.newArrayList();
            
            String nextToken = "";

            // Call the API one or more times to retrieve all items in the
            // list. As long as the API response returns a nextPageToken,
            // there are still more items to retrieve.
            do {
                YouTube.Subscriptions.List subscriptionRequest = youtube.subscriptions().list("snippet");
                subscriptionRequest.setMine(true);
                subscriptionRequest.setPageToken(nextToken);
                subscriptionRequest.setMaxResults(50l);
                SubscriptionListResponse subscriptionResult = ClientRequestHelper.executeRetry(subscriptionRequest);

                subscriptionList.addAll(subscriptionResult.getItems());

                nextToken = subscriptionResult.getNextPageToken();
            } while (nextToken != null);

            // Grab all latest videos
            getLatestVideos(subscriptionList);
            
            // Output latest videos
            outputVideos(output);
        } catch (GoogleJsonResponseException e) {
            e.printStackTrace();
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * Get latest videos.
     * 
     * @param subscriptionList List of subscriptions
     * @throws Exception
     */
    private static void getLatestVideos(List<Subscription> subscriptionList) throws Exception {
        System.out.println("Subscriptions = " + subscriptionList.size());
        
        for (Subscription subscription : subscriptionList) {
            System.out.println("Grabbing videos for = " + subscription.getSnippet().getTitle()
                    + " (" + (subscriptionList.indexOf(subscription) + 1) + "/" + subscriptionList.size() + ")");
            
            // Grab uploads playlist from this channel
            YouTube.Channels.List channelRequest = youtube.channels().list("contentDetails");
            channelRequest.setMaxResults(1l);
            channelRequest.setId(subscription.getSnippet().getResourceId().getChannelId());
            ChannelListResponse channelResult = ClientRequestHelper.executeRetry(channelRequest);
            List<Channel> channelList = channelResult.getItems();
            String uploadPlaylistId = channelList.get(0).getContentDetails().getRelatedPlaylists().getUploads();
            
            // Get 50 latest uploads from this channel
            YouTube.PlaylistItems.List playlistRequest = youtube.playlistItems().list("snippet");
            playlistRequest.setMaxResults(50l);
            playlistRequest.setPlaylistId(uploadPlaylistId);
            PlaylistItemListResponse playlistResult = ClientRequestHelper.executeRetry(playlistRequest);
            
            // Get details on those videos
            List<String> idList = Lists.newArrayList();
            for (PlaylistItem playlistItem : playlistResult.getItems()) {
                idList.add(playlistItem.getSnippet().getResourceId().getVideoId());
            }
            
            YouTube.Videos.List videosRequest = youtube.videos().list("snippet,contentDetails,statistics");
            videosRequest.setMaxResults(50l);
            videosRequest.setId(Joiner.on(",").join(idList));
            VideoListResponse videosResult = ClientRequestHelper.executeRetry(videosRequest);
            List<Video> videoList = videosResult.getItems();
            
            latestVideoList.addAll(videoList);
        }
    }
    
    /**
     * Output the videos in JSON format.
     * 
     * @param output JSON output
     * @throws Exception
     */
    private static void outputVideos(String output) throws Exception {
        // Sort videos
        Collections.sort(latestVideoList, new Comparator<Video>() {
            @Override
            public int compare(Video o1, Video o2) {
                return - Long.compare(o1.getSnippet().getPublishedAt().getValue(), o2.getSnippet().getPublishedAt().getValue());
            }
        });
        
        // Trim MAX_VIDEOS videos
        latestVideoList = latestVideoList.subList(0, latestVideoList.size() > MAX_VIDEOS ? MAX_VIDEOS : latestVideoList.size() - 1);
        
        // Display videos
        JsonArrayBuilder videos = Json.createArrayBuilder();
        for (Video video : latestVideoList) {
            videos.add(Json.createObjectBuilder()
                    .add("id", video.getId())
                    .add("title", video.getSnippet().getTitle())
                    .add("description", video.getSnippet().getDescription())
                    .add("channel_id", video.getSnippet().getChannelId())
                    .add("channel_title", video.getSnippet().getChannelTitle())
                    .add("published_date", video.getSnippet().getPublishedAt().getValue())
                    .add("view_count", video.getStatistics().getViewCount())
                    .add("like_count", video.getStatistics().getLikeCount())
                    .add("dislike_count", video.getStatistics().getDislikeCount())
                    .add("duration", video.getContentDetails().getDuration())
                    .add("thumbnails", Json.createObjectBuilder()
                            .add("medium", Json.createObjectBuilder()
                                    .add("url", video.getSnippet().getThumbnails().getMedium().getUrl())
                                    .add("width", video.getSnippet().getThumbnails().getMedium().getWidth())
                                    .add("height", video.getSnippet().getThumbnails().getMedium().getHeight()))
                            .add("high", Json.createObjectBuilder()
                                    .add("url", video.getSnippet().getThumbnails().getHigh().getUrl())
                                    .add("width", video.getSnippet().getThumbnails().getHigh().getWidth())
                                    .add("height", video.getSnippet().getThumbnails().getHigh().getHeight()))));
        }
        JsonObjectBuilder json = Json.createObjectBuilder()
            .add("time", new Date().getTime())
            .add("videos", videos);
        
        // Save the JSON file
        Map<String, Boolean> config = new HashMap<>();
        config.put(JsonGenerator.PRETTY_PRINTING, true);
        JsonWriterFactory jsonWriterFactory = Json.createWriterFactory(config);
        
        try (OutputStream os = new FileOutputStream(new File(output))) {
            jsonWriterFactory.createWriter(os).write(json.build());
        }
    }
}
