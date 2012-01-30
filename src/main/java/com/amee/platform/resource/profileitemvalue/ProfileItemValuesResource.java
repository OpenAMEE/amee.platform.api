package com.amee.platform.resource.profileitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.domain.item.profile.ProfileItem;

public interface ProfileItemValuesResource {

    static interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, ProfileItem profileItem);

        ProfileItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper);

        ProfileItemValueResource.Builder getProfileItemValueBuilder(RequestWrapper requestWrapper);
    }
    
    static interface Renderer extends ResourceRenderer {
        
        void newProfileItemValue(ProfileItemValueResource.Renderer renderer);
        
        void setTruncated(boolean truncated);
    }
}
