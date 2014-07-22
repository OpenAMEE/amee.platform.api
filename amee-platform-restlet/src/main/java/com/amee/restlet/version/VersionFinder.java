package com.amee.restlet.version;

import com.amee.base.domain.Version;
import com.amee.restlet.resource.GenericResource;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.ext.spring.SpringFinder;
import org.restlet.resource.Resource;

public class VersionFinder extends SpringFinder {

    private Version since = new Version("0");
    private Version until = new Version("x");

    public VersionFinder() {
        super();
    }

    public VersionFinder(Context context) {
        super(context);
    }

    public VersionFinder(Context context, Class<? extends Resource> targetClass) {
        super(context, targetClass);
    }

    @Override
    public Resource createTarget(Request request, Response response) {
        Resource resource = super.createTarget(request, response);
        if (resource != null) {
            if (GenericResource.class.isAssignableFrom(resource.getClass())) {
                // Configure the Resource with since and until Versions.
                GenericResource genericResource = (GenericResource) resource;
                genericResource.setSince(getSince());
                genericResource.setUntil(getUntil());
            }
        }
        return resource;
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
