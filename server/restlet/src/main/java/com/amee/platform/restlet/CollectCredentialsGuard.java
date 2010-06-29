package com.amee.platform.restlet;

import com.amee.restlet.BaseGuard;
import org.restlet.Application;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Request;

/**
 * A Guard that only collects the basic auth credentials and places them in the request attributes. The
 * result of checkSecret will always be true and logic downstream is expected to perform authentication and
 * authorization based on the collected 'username' and 'password' attributes.
 */
public class CollectCredentialsGuard extends BaseGuard {

    public CollectCredentialsGuard(Application application) {
        super(application, ChallengeScheme.HTTP_BASIC, "AMEE Platform API");
    }

    @Override
    public boolean checkSecret(Request request, String identifier, char[] secret) {
//        request.getAttributes().put("username", identifier);
//        request.getAttributes().put("password", new String(secret));
//        return true;
        return ((identifier != null) &&
                (secret != null) &&
                "platform".equalsIgnoreCase(identifier) &&
                "OoKtNjw13b".equals(new String(secret)));
    }
}
