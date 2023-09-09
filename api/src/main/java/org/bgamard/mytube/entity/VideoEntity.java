package org.bgamard.mytube.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "video")
public class VideoEntity extends BaseEntity {
    @Column(nullable = false)
    public String title;
    @Column(nullable = false)
    public String youtubeId;
    @Column
    public String description;
    @Column(nullable = false)
    public String channelId;
    @Column(nullable = false)
    public String channelTitle;
    @Column(nullable = false)
    public LocalDateTime publishedDate;
    @Column
    public Long viewCount;
    @Column
    public Long likeCount;
    @Column(nullable = false)
    public Duration duration;
    @Column(nullable = false)
    public String thumbnailUrl;

    public static Optional<VideoEntity> findByYoutubeId(String youtubeId) {
        return find("youtubeId", youtubeId).firstResultOptional();
    }
}