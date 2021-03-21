package org.example.ui.resource;
/*
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.jwt.JsonWebToken;
import io.quarkus.oidc.IdToken;
import io.quarkus.security.Authenticated;

@Path("/web-app")
@Authenticated
public class ProtectedResource {

    @Inject
    JsonWebToken accessToken;

    // or
    // @Inject
    // AccessTokenCredential accessTokenCredential;

    @GET
    public String getReservationOnBehalfOfUser() {
        String rawAccessToken = accessToken.getRawToken();
        //or
        //String rawAccessToken = accessTokenCredential.getToken();

        // Use the raw access token to access a remote endpoint
        return rawAccessToken;
        //return getReservationfromRemoteEndpoint(rawAccesstoken);
    }
}
*/