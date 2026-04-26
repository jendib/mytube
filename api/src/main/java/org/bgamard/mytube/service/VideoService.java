package org.bgamard.mytube.service;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.bgamard.mytube.client.YoutubeClientService;
import org.bgamard.mytube.client.model.Video;
import org.bgamard.mytube.client.model.VideoList;
import org.bgamard.mytube.entity.VideoEntity;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.util.List;

@ApplicationScoped
public class VideoService {
    @Inject
    @RestClient
    YoutubeClientService youtubeClientService;

    @Transactional
    public void fetchAndSaveVideos(String ids, Boolean watchLater) {
        VideoList videosResult = youtubeClientService.videos(ids, "snippet,contentDetails,statistics", 50);
        saveVideos(videosResult.items, watchLater);
    }

    public void saveVideos(List<Video> latestVideoList, Boolean watchLater) {
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
            if (watchLater != null) {
                videoEntity.watchLater = watchLater;
            }
            videoEntity.persist();
        }
    }
}
