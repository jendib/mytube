package org.bgamard.mytube.resource;

import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bgamard.mytube.entity.VideoEntity;
import org.bgamard.mytube.service.UpdateService;
import org.bgamard.mytube.service.VideoService;

import java.util.List;
import java.util.UUID;

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
public class VideoResource {
    @Inject
    UpdateService updateService;

    @Inject
    VideoService videoService;

    @GET
    @Transactional
    public List<VideoEntity> get(
            @QueryParam("watchLaterOnly") @DefaultValue("false") boolean watchLaterOnly,
            @QueryParam("markAllAsSeen") @DefaultValue("false") boolean markAllAsSeen,
            @QueryParam("sortBy") @DefaultValue("publishedDate") String sortBy,
            @QueryParam("sortOrder") @DefaultValue("DESC") String sortOrder) {
        String query = "from VideoEntity";
        if (watchLaterOnly) {
            query += " where watchLater = true";
        }

        Sort.Direction direction = "ASC".equalsIgnoreCase(sortOrder) ? Sort.Direction.Ascending : Sort.Direction.Descending;
        List<String> validSortBy = List.of("publishedDate", "duration", "title", "channelTitle");
        if (!validSortBy.contains(sortBy)) {
            sortBy = "publishedDate";
        }

        return VideoEntity.<VideoEntity>find(query, Sort.by(sortBy, direction))
                .range(0, 200)
                .stream()
                .peek(video -> {
                    if (markAllAsSeen && !video.seen) {
                        VideoEntity.markAsSeen(video.id);
                    }
                })
                .toList();
    }

    @POST
    @Path("watch-later")
    @Transactional
    public VideoEntity watchLater(@QueryParam("id") UUID id, @QueryParam("watchLater") boolean watchLater) {
        VideoEntity videoEntity = VideoEntity.findById(id);
        videoEntity.watchLater = watchLater;
        videoEntity.persist();
        return videoEntity;
    }

    @POST
    public Response addByUrl(@QueryParam("url") String url) {
        String videoId = null;
        if (url.contains("v=")) {
            videoId = url.split("v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            videoId = url.split("youtu.be/")[1].split("\\?")[0];
        }

        if (videoId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid YouTube URL").build();
        }

        videoService.fetchAndSaveVideos(videoId, true);
        return Response.ok().build();
    }

    @GET
    @Path("update")
    public Response update() {
        updateService.update();
        return Response.ok().build();
    }
}
