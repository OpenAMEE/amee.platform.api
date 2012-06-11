package com.amee.platform.restlet;

import com.amee.domain.auth.User;
import com.amee.restlet.BaseGuard;
import com.amee.restlet.RequestContext;
import com.amee.service.auth.AuthenticationService;
import org.restlet.Application;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Request;
import org.slf4j.MDC;

/**
 * A Guard that only collects the basic auth credentials and places them in the request attributes. The
 * result of checkSecret will always be true and logic downstream is expected to perform authentication and
 * authorization based on the collected 'username' and 'password' attributes.
 */
public class CollectCredentialsGuard extends BaseGuard {

    private AuthenticationService authenticationService;

    public CollectCredentialsGuard(Application application, AuthenticationService authenticationService) {
        super(application, ChallengeScheme.HTTP_BASIC, "AMEE Platform API");
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean checkSecret(Request request, String identifier, char[] secret) {
        User sampleUser = new User();
        sampleUser.setUsername(identifier);
        sampleUser.setPasswordInClear(new String(secret));
        User activeUser = authenticationService.authenticate(sampleUser);
        if (activeUser != null) {
            request.getAttributes().put("activeUser", activeUser);
            request.getAttributes().put("activeUserUid", activeUser.getUid());
            RequestContext context = (RequestContext) request.getAttributes().get("requestContext");
            context.setUserUid(activeUser.getUid());

            // Store the user UID in the MDC for logging
            MDC.put("userUid", activeUser.getUid());
            return true;
        } else {
            return false;
        }
    }
}
