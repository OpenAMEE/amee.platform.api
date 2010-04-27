package com.amee.restlet.version;

import com.amee.base.domain.Version;
import org.restlet.Restlet;
import org.restlet.Route;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.util.Template;
import org.springframework.beans.factory.annotation.Qualifier;

public class VersionRoute extends Route {

    private Version since = new Version("0");
    private Version until = new Version("x");

    public VersionRoute(Restlet next) {
        super(next);
        updateVersions();
    }

    public VersionRoute(Router router, String uriTemplate, Restlet next) {
        super(router, uriTemplate, next);
        updateVersions();
    }

    public VersionRoute(Router router, Template template, Restlet next) {
        super(router, template, next);
        updateVersions();
    }

    public void updateVersions() {
        if ((getNext() != null) && (VersionFinder.class.isAssignableFrom(getNext().getClass()))) {
            VersionFinder versionFinder = (VersionFinder) getNext();
            setSince(versionFinder.getSince());
            setUntil(versionFinder.getUntil());
        }
    }

    @Override
    public float score(Request request, Response response) {
        float result = super.score(request, response);
        if (result > 0F) {
            Version versionSupported = (Version) request.getAttributes().get("versionSupported");
            if (versionSupported.before(getSince())) {
                result = 0F;
            }
            if (versionSupported.after(getUntil())) {
                result = 0F;
            }
        }
        return result;
    }

    public Version getSince() {
        return since;
    }

    public void setSince(Version since) {
        if (since == null) {
            throw new IllegalArgumentException("The since parameter was null.");
        }
        this.since = since;
    }

    public Version getUntil() {
        return until;
    }

    public void setUntil(Version until) {
        if (until == null) {
            throw new IllegalArgumentException("The until parameter was null.");
        }
        this.until = until;
    }
}
