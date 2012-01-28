package com.amee.platform.resource.profileitemvalue;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ValidationResult;
import com.amee.domain.ProfileItemValuesFilter;
import com.amee.domain.item.profile.ProfileItem;

import java.util.Map;

public interface ProfileItemValuesResource {

    static interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, ProfileItem profileItem, ProfileItemValuesFilter filter);

        ProfileItemValuesResource.Renderer getRenderer(RequestWrapper requestWrapper);

        ProfileItemValueResource.Builder getProfileItemValueBuilder(RequestWrapper requestWrapper);

        ProfileItemValuesFilterValidator getValidator(RequestWrapper requestWrapper);
        
    }
    
    static interface Renderer extends ResourceRenderer {
        
        void newProfileItemValue(ProfileItemValueResource.Renderer renderer);
        
        void setTruncated(boolean truncated);
    }
    
    static interface ProfileItemValuesFilterValidator {
        
        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        ProfileItemValuesFilter getObject();

        void setObject(ProfileItemValuesFilter object);

        ValidationResult getValidationResult();
    }
}
