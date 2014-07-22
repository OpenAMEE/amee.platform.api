package com.amee.platform.resource.profile;

import com.amee.base.resource.*;
import com.amee.domain.data.DataCategory;
import com.amee.domain.profile.Profile;

import java.util.Map;

public interface ProfileResource {

        public static interface Builder extends ResourceBuilder {

        public void handle(RequestWrapper requestWrapper, Profile profile);

        public ProfileResource.Renderer getRenderer(RequestWrapper requestWrapper);
    }

    public static interface Renderer extends ResourceRenderer {

        public void newProfile(Profile profile);

        public void addBasic();

        public void addAudit();

        public void startCategories();

        public void newCategory(DataCategory category);
    }

    public static interface FormAcceptor extends ResourceAcceptor {

        public ProfileResource.ProfileValidator getValidator(RequestWrapper requestWrapper);
    }

    public static interface ProfileValidator {

        public void initialise();

        public void initialise(boolean addUnitType);

        public boolean isValid(Map<String, String> queryParameters);

        public Profile getObject();

        public void setObject(Profile object);

        public ValidationResult getValidationResult();
    }

    public static interface Remover extends ResourceRemover {
    }
}
