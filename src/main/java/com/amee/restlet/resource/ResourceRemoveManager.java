package com.amee.restlet.resource;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceRemover;
import org.json.JSONObject;
import org.restlet.data.Status;

public class ResourceRemoveManager extends ResourceManager {

    private ResourceRemover<JSONObject> remover;

    public void remove() {
        if (remover != null) {
            JSONObject result = remover.remove(new RequestWrapper(getAttributes()));
            if (isOk(result)) {
                getResponse().setStatus(Status.SUCCESS_NO_CONTENT);
            } else if (isNotFound(result)) {
                getResponse().setStatus(Status.CLIENT_ERROR_NOT_FOUND);
            } else {
                getResponse().setStatus(Status.SERVER_ERROR_INTERNAL);
            }
        } else {
            throw new UnsupportedOperationException("A ResourceRemover is not available.");
        }
    }

    public ResourceRemover getRemover() {
        return remover;
    }

    public void setRemover(ResourceRemover<JSONObject> remover) {
        this.remover = remover;
    }
}
