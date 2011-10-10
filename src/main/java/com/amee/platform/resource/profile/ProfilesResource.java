package com.amee.platform.resource.profile;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;

public interface ProfilesResource {

    public static interface Builder extends ResourceBuilder {
        public ProfilesResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newProfile(ProfileResource.Renderer renderer);

        public void setTruncated(boolean truncated);
    }

//    public static interface FormAcceptor extends ResourceAcceptor {
//
//        public ProfileResource.ProfileValidator getValidator(RequestWrapper requestWrapper);
//    }
}
