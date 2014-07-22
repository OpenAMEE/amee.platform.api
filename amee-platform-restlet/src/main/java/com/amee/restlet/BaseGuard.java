package com.amee.restlet;

import org.restlet.Application;
import org.restlet.Guard;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Request;

import java.util.Collection;

public abstract class BaseGuard extends Guard {

    public BaseGuard(Application application, ChallengeScheme challengeScheme, String realm) throws IllegalArgumentException {
        super(application.getContext(), challengeScheme, realm);
    }

    public BaseGuard(Application application, String realm, Collection<String> baseUris, String serverKey) {
        super(application.getContext(), realm, baseUris, serverKey);
    }

    protected RequestContext getRequestContext(Request request) {
        RequestContext requestContext = (RequestContext) request.getAttributes().get("requestContext");
        if (requestContext == null) {
            throw new RuntimeException("RequestContext was null.");
        }
        return requestContext;
    }
}
