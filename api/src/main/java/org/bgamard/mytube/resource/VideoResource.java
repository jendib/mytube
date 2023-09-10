package org.bgamard.mytube.resource;

import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bgamard.mytube.entity.VideoEntity;
import org.bgamard.mytube.service.UpdateService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VideoResource {
    @Inject
    UpdateService updateService;

    @GET
    @Transactional
    public List<VideoEntity> get(
            @QueryParam("watchLaterOnly") @DefaultValue("false") boolean watchLaterOnly,
            @QueryParam("markAllAsSeen") @DefaultValue("false") boolean markAllAsSeen) {
        String query = "from VideoEntity";
        if (watchLaterOnly) {
            query += " where watchLater = true";
        }

        return VideoEntity.<VideoEntity>find(query, Sort.by("publishedDate", Sort.Direction.Descending))
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

    @GET
    @Path("update")
    public Response update() {
        updateService.update();
        return Response.ok().build();
    }
}
