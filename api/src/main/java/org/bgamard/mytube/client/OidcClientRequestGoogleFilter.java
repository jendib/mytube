package org.bgamard.mytube.client;

import io.quarkus.oidc.client.OidcClient;
import io.quarkus.oidc.client.Tokens;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class OidcClientRequestGoogleFilter implements ClientRequestFilter {
    @ConfigProperty(name = "mytube.google.refresh-token")
    String refreshToken;

    @Inject
    OidcClient oidcClient;

    volatile Tokens currentTokens;

    @PostConstruct
    public void init() {
        currentTokens = getTokens();
    }

    @Override
    public void filter(ClientRequestContext requestContext) {
        Tokens tokens = currentTokens;
        if (tokens.isAccessTokenExpired()) {
            tokens = getTokens();
            currentTokens = tokens;
        }

        requestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken());
    }

    private Tokens getTokens() {
        return oidcClient.refreshTokens(refreshToken).await().atMost(Duration.ofSeconds(30));
    }
}