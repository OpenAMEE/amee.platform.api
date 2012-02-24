package com.amee.platform.resource.profileitem;

import java.util.Map;
import java.util.TimeZone;

import com.amee.base.resource.RequestWrapper;
import com.amee.base.resource.ResourceAcceptor;
import com.amee.base.resource.ResourceBuilder;
import com.amee.base.resource.ResourceRemover;
import com.amee.base.resource.ResourceRenderer;
import com.amee.base.resource.ValidationResult;
import com.amee.domain.ProfileItemService;
import com.amee.domain.item.profile.ProfileItem;
import com.amee.platform.science.ReturnValues;

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
        
        void addNote();
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
