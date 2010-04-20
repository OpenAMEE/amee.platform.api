package com.amee.restlet.version;

import com.amee.base.domain.Version;
import com.amee.base.domain.Versions;
import com.amee.restlet.BaseFilter;
import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Autowired;

public class VersionFilter extends BaseFilter {

    @Autowired
    private Versions versions;

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