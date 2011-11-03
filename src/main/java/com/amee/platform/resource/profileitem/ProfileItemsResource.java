package com.amee.platform.resource.profileitem;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.validation.ValidationException;
import com.amee.domain.item.profile.ProfileItem;

public interface ProfileItemsResource {

    interface Builder extends ResourceBuilder {
        ProfileItemsResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newProfileItem(ProfileItemResource.Renderer renderer);

        void setTruncated(boolean truncated);
    }

    interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper, ProfileItem profileItem);
    }
}
