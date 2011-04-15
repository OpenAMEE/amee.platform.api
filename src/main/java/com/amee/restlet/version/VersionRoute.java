package com.amee.restlet.version;

import com.amee.base.domain.Version;
import org.restlet.Restlet;
import org.restlet.Route;
import org.restlet.Router;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.util.Template;

/**
 * Extends Restlet {@link Route} to provide versioning functionality with the since and until properties. The
 * intention is to ensure a {@link Route} is only available between the specified since and until versions.
 */
public class VersionRoute extends Route {

    /**
     * A {link Version} which defines when a {@link Route} has been available since.
     */
    private Version since = new Version("0");

    /**
     * A {link Version} which defines when a {@link Route} is available until.
     */
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

    /**
     * Retrieves the since and until version values from a {@link VersionFinder}, if that is the next Restlet. If a
     * VersionFinder is not available then the since and until versions will default to the widest range (0 to x).
     */
    public void updateVersions() {
        if ((getNext() != null) && (VersionFinder.class.isAssignableFrom(getNext().getClass()))) {
            VersionFinder versionFinder = (VersionFinder) getNext();
            setSince(versionFinder.getSince());
            setUntil(versionFinder.getUntil());
        }
    }

    /**
     * Returns the score for a given call (usually between 0 and 1.0). This overrides the method in {@link Route} to
     * return a score of zero if the 'versionSupported' request attribute indicates a version not supported by
     * this {@link VersionRoute} (before the since or after/on the until version).
     *
     * @param request  The request to score.
     * @param response The response to score.
     * @return The score for a given call (between 0 and 1.0).
     */
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
