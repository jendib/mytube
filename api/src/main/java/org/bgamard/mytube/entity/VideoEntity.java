package org.bgamard.mytube.entity;

import io.quarkus.hibernate.orm.panache.Panache;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
    @Column(nullable = false)
    public boolean watchLater;
    @Column(nullable = false)
    public boolean seen;

    public static Optional<VideoEntity> findByYoutubeId(String youtubeId) {
        return find("youtubeId", youtubeId).firstResultOptional();
    }

    public static void markAsSeen(UUID id) {
        Panache.getEntityManager()
                .createNativeQuery("update video set seen = true where id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }
}