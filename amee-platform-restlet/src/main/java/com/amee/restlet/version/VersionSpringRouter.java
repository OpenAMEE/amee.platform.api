package com.amee.restlet.version;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Route;
import org.restlet.ext.spring.SpringRouter;

public class VersionSpringRouter extends SpringRouter {

    public VersionSpringRouter() {
        super();
    }

    public VersionSpringRouter(Context context) {
        super(context);
    }

    public VersionSpringRouter(Restlet parent) {
        super(parent);
    }

    @Override
    protected Route createRoute(String uriPattern, Restlet target) {
        final Route result = new VersionRoute(this, uriPattern, target);
        result.getTemplate().setMatchingMode(getDefaultMatchingMode());
        result.setMatchQuery(getDefaultMatchQuery());
        return result;
    }
}
