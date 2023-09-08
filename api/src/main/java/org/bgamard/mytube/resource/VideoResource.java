package org.bgamard.mytube.resource;

import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.bgamard.mytube.entity.VideoEntity;
import org.bgamard.mytube.service.UpdateService;

import java.util.List;

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VideoResource {
    @Inject
    UpdateService updateService;

    @GET
    public List<VideoEntity> get() {
        return VideoEntity.findAll(Sort.by("publishedDate", Sort.Direction.Descending))
                .range(0, 200)
                .list();
    }

    @GET
    @Path("update")
    public Response update() throws Exception {
        updateService.update();
        return Response.ok().build();
    }
}
