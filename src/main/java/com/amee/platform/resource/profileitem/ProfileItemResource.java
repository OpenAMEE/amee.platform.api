package com.amee.platform.resource.profileitem;

import com.amee.base.resource.*;
import com.amee.domain.ProfileItemService;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.domain.profile.Profile;
import com.amee.platform.science.ReturnValues;

import java.util.Map;
import java.util.TimeZone;

public interface ProfileItemResource {

    interface Builder extends ResourceBuilder {

        void handle(RequestWrapper requestWrapper, ProfileItem profileItem);

        ProfileItemResource.Renderer getRenderer(RequestWrapper requestWrapper);

//        ProfileItemValuesResource.ProfileItemValuesFilterValidator getValidator(RequestWrapper requestWrapper);
    }

    interface Renderer extends ResourceRenderer {

        void newProfileItem(ProfileItem profileItem);

        void addBasic();

        void addName();

        void addDates(TimeZone timeZone);

        void addCategory();

        void addAudit();
        
        void addNote();

        void addReturnValues(ReturnValues returnValues);
    }

    interface FormAcceptor extends ResourceAcceptor {
    }

    interface ProfileItemValidator {
        void initialise();

        boolean isValid(Map<String, String> queryParameters);

        ProfileItem getObject();

        void setObject(ProfileItem object);

        ValidationResult getValidationResult();

        void setProfileItemService(ProfileItemService profileItemService);
    }

    interface Remover extends ResourceRemover {
    }
}
