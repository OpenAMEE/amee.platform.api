package com.amee.restlet.version;

import com.amee.base.domain.Version;
import com.amee.base.domain.Versions;
import com.amee.restlet.BaseFilter;
import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A Restlet Filter to add versioning support to resource representations and end-points. An attribute
 * named 'version' is required. This is extracted by Restlet from the start of the URL.
 * <p/>
 * This filter supports URLs that look like the following examples: "/1/a/path", "/1.0/a/path", "/1.0.0/a/path".
 * <p/>
 * The version string is validated and matched against Versions defined in the Versions bean. If the Version is
 * supported then the request will continue to next Restlet. If the Version is not supported then a 404 response
 * is returned. A versionSupported attribute is added to the request which matches the latest version supported for
 * the requested version.
 */
public class VersionFilter extends BaseFilter {

    @Autowired
    private Versions versions;

    /**
     * Overrides doHandle in {@link Filter} to only handle requests where the version is correct. See rules above.
     *
     * @param request  The request to handle.
     * @param response The response to update.
     * @return The continuation status. Either {@link #CONTINUE} or {@link #STOP}.
     * @see {@link Filter}
     */
    @Override
    protected int doHandle(Request request, Response response) {
        String v = (String) request.getAttributes().get("version");
        if (Version.isValidVersion(v)) {
            Version version = new Version(v);
            request.getAttributes().put("version", version);
            Version versionSupported = versions.getSupportedVersion(version);
            if (versionSupported != null) {
                request.getAttributes().put("versionSupported", versionSupported);
                return super.doHandle(request, response);
            } else {
                response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                return Filter.STOP;
            }
        } else {
            response.setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            return Filter.STOP;
        }
    }
}