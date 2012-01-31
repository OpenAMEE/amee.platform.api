package com.amee.platform.resource.profileitem;

import com.amee.base.resource.*;
import com.amee.base.validation.ValidationException;
import com.amee.domain.ProfileItemsFilter;
import com.amee.domain.profile.Profile;
import com.amee.platform.science.StartEndDate;

import java.util.Date;
import java.util.Map;

public interface ProfileItemsResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, Profile profile, ProfileItemsFilter filter);

        Renderer getRenderer(RequestWrapper requestWrapper);

        ProfileItemResource.Builder getProfileItemBuilder(RequestWrapper requestWrapper);

        FilterValidator getValidator(RequestWrapper requestWrapper);

    }

    interface Renderer extends ResourceRenderer {

        void newProfileItem(ProfileItemResource.Renderer renderer);

        void setTruncated(boolean truncated);
    }

    interface FormAcceptor extends ResourceAcceptor {

        Object handle(RequestWrapper requestWrapper) throws ValidationException;
    }

    interface FilterValidator {
        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        ProfileItemsFilter getObject();

        void setObject(ProfileItemsFilter object);

        StartEndDate getDefaultStartDate();

        void setDefaultStartDate(StartEndDate defaultStartDate);

        ValidationResult getValidationResult();
    }
}
