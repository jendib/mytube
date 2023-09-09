package org.bgamard.mytube.client;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.bgamard.mytube.client.model.ChannelList;
import org.bgamard.mytube.client.model.PlaylistItemList;
import org.bgamard.mytube.client.model.SubscriptionList;
import org.bgamard.mytube.client.model.VideoList;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "youtube-api")
@ApplicationScoped
@RegisterProvider(OidcClientRequestGoogleFilter.class)
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface YoutubeClientService {
    @GET
    @Path("subscriptions")
    SubscriptionList subscriptions(
            @QueryParam("part") String part,
            @QueryParam("order") String order,
            @QueryParam("mine") boolean mine,
            @QueryParam("maxResults") int maxResults,
            @QueryParam("pageToken") String pageToken);

    @GET
    @Path("channels")
    ChannelList channels(
            @QueryParam("id") String id,
            @QueryParam("part") String part,
            @QueryParam("maxResults") int maxResults);

    @GET
    @Path("playlistItems")
    PlaylistItemList playlistItems(
            @QueryParam("playlistId") String playlistId,
            @QueryParam("part") String part,
            @QueryParam("maxResults") int maxResults);

    @GET
    @Path("videos")
    VideoList videos(
            @QueryParam("id") String id,
            @QueryParam("part") String part,
            @QueryParam("maxResults") int maxResults);
}