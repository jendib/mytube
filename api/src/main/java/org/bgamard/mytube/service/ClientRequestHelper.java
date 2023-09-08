package org.bgamard.mytube.service;

import java.io.IOException;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;

/**
 * Google client request helper.
 * 
 * @author bgamard
 */
public class ClientRequestHelper {
    /**
     * Max retries
     */
    private static final int MAX_RETRY = 5;

    /**
     * Execute an API call with retries.
     * 
     * @param clientRequest Google client request
     * @return Response
     * @throws IOException e
     */
    public static <T> T executeRetry(AbstractGoogleClientRequest<T> clientRequest) throws IOException {
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                return clientRequest.execute();
            } catch (IOException e) {
                if (i < MAX_RETRY - 1) {
                    System.out.println("Retrying on " + e.getMessage());
                } else {
                    throw e;
                }
            }
        }
        
        return null;
    }
}
